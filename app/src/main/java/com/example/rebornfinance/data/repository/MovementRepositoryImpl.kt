package com.example.rebornfinance.data.repository

import androidx.room.withTransaction
import com.example.rebornfinance.core.date.DateUtils
import com.example.rebornfinance.data.local.AppDatabase
import com.example.rebornfinance.data.local.dao.EyePurchaseLotDao
import com.example.rebornfinance.data.local.dao.HairPurchaseLotDao
import com.example.rebornfinance.data.local.dao.MaterialDao
import com.example.rebornfinance.data.local.dao.MaterialPurchaseLotDao
import com.example.rebornfinance.data.local.dao.MovementDao
import com.example.rebornfinance.data.local.entity.MovementEntity
import com.example.rebornfinance.domain.model.Movement
import com.example.rebornfinance.domain.repository.MovementRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MovementRepositoryImpl(
    private val movementDao: MovementDao,
    private val database: AppDatabase? = null,
    private val materialDao: MaterialDao? = null,
    private val materialLotDao: MaterialPurchaseLotDao? = null,
    private val eyeLotDao: EyePurchaseLotDao? = null,
    private val hairLotDao: HairPurchaseLotDao? = null
) : MovementRepository {

    override fun getAllMovements(): Flow<List<Movement>> {
        return movementDao.getAllMovements().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getMovementById(id: Long): Movement? {
        return movementDao.getMovementById(id)?.toDomain()
    }

    override suspend fun insertMovement(movement: Movement): Long {
        return movementDao.insertMovement(MovementEntity.fromDomain(movement))
    }

    override suspend fun updateMovement(movement: Movement) {
        movementDao.updateMovement(MovementEntity.fromDomain(movement))
    }

    override suspend fun deleteMovement(movement: Movement) {
        movementDao.deleteMovement(MovementEntity.fromDomain(movement))
    }

    override suspend fun deleteMovementSafely(movementId: Long): Result<Unit> {
        val db = database ?: return try {
            val mov = movementDao.getMovementById(movementId)
            if (mov != null) movementDao.deleteMovement(mov)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }

        val matLotDao = materialLotDao ?: db.materialPurchaseLotDao()
        val eLotDao = eyeLotDao ?: db.eyePurchaseLotDao()
        val hLotDao = hairLotDao ?: db.hairPurchaseLotDao()
        val matDao = materialDao ?: db.materialDao()

        return try {
            db.withTransaction {
                val generalLots = matLotDao.getLotsByMovementId(movementId)
                val eyeLots = eLotDao.getLotsByMovementId(movementId)
                val hairLots = hLotDao.getLotsByMovementId(movementId)

                // Verify if any lot has been consumed
                for (lot in generalLots) {
                    if (lot.remainingQuantity < lot.purchasedQuantity) {
                        throw IllegalStateException("No se puede eliminar la compra: el material ya ha sido consumido en proyectos reborn.")
                    }
                }
                for (lot in eyeLots) {
                    if (lot.remainingQuantity < lot.purchasedQuantity) {
                        throw IllegalStateException("No se puede eliminar la compra: los ojos ya han sido asignados a un proyecto reborn.")
                    }
                }
                for (lot in hairLots) {
                    if (lot.remainingQuantityGrams < lot.purchasedQuantityGrams) {
                        throw IllegalStateException("No se puede eliminar la compra: el pelo ya ha sido consumido en un proyecto reborn.")
                    }
                }

                // Revert stock and delete lots
                for (lot in generalLots) {
                    matDao.getMaterialById(lot.materialId)?.let { mat ->
                        val newAvail = maxOf(0L, mat.availableQuantity - lot.purchasedQuantity)
                        val newTotal = maxOf(0L, mat.totalPurchased - lot.purchasedQuantity)
                        matDao.updateMaterial(mat.copy(availableQuantity = newAvail, totalPurchased = newTotal, updatedAt = DateUtils.getCurrentTimestamp()))
                    }
                    matLotDao.deleteLot(lot)
                }
                for (lot in eyeLots) {
                    eLotDao.deleteLot(lot)
                }
                for (lot in hairLots) {
                    hLotDao.deleteLot(lot)
                }

                // Delete movement
                val movEntity = movementDao.getMovementById(movementId)
                if (movEntity != null) {
                    movementDao.deleteMovement(movEntity)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
