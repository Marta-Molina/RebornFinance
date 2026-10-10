package com.example.rebornfinance.data.repository

import androidx.room.withTransaction
import com.example.rebornfinance.core.date.DateUtils
import com.example.rebornfinance.data.local.AppDatabase
import com.example.rebornfinance.data.local.dao.HairMaterialDao
import com.example.rebornfinance.data.local.dao.HairPurchaseLotDao
import com.example.rebornfinance.data.local.dao.MovementDao
import com.example.rebornfinance.data.local.dao.ProjectHairConsumptionDao
import com.example.rebornfinance.data.local.entity.HairMaterialEntity
import com.example.rebornfinance.data.local.entity.HairPurchaseLotEntity
import com.example.rebornfinance.data.local.entity.MovementEntity
import com.example.rebornfinance.data.local.entity.ProjectHairConsumptionEntity
import com.example.rebornfinance.domain.calculator.EyeAndHairCostCalculator
import com.example.rebornfinance.domain.model.HairMaterial
import com.example.rebornfinance.domain.model.HairPurchaseLot
import com.example.rebornfinance.domain.model.MovementType
import com.example.rebornfinance.domain.model.ProjectHairConsumption
import com.example.rebornfinance.domain.repository.HairMaterialRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HairMaterialRepositoryImpl(
    private val database: AppDatabase,
    private val hairDao: HairMaterialDao,
    private val lotDao: HairPurchaseLotDao,
    private val consumptionDao: ProjectHairConsumptionDao,
    private val movementDao: MovementDao
) : HairMaterialRepository {

    override fun getAllHairMaterials(): Flow<List<HairMaterial>> {
        return hairDao.getAllHairMaterials().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getHairMaterialById(id: Long): HairMaterial? {
        return hairDao.getHairMaterialById(id)?.toDomain()
    }

    override suspend fun insertHairMaterial(
        material: HairMaterial,
        initialLot: HairPurchaseLot?,
        paidAmountCents: Long?
    ): Long {
        var materialId = 0L
        database.withTransaction {
            val now = DateUtils.getCurrentTimestamp()
            val entity = HairMaterialEntity.fromDomain(material)
            materialId = hairDao.insertHairMaterial(entity)

            if (initialLot != null && paidAmountCents != null) {
                val lotEntity = HairPurchaseLotEntity(
                    hairMaterialId = materialId,
                    purchasedQuantityGrams = initialLot.purchasedQuantityGrams,
                    remainingQuantityGrams = initialLot.purchasedQuantityGrams,
                    paidAmountCents = paidAmountCents,
                    purchaseDate = initialLot.purchaseDate,
                    notes = initialLot.notes,
                    createdAt = now
                )
                val lotId = lotDao.insertLot(lotEntity)

                val movement = MovementEntity(
                    type = MovementType.EXPENSE,
                    amountCents = paidAmountCents,
                    description = "Compra pelo: ${material.name}",
                    category = "Materiales",
                    date = initialLot.purchaseDate,
                    createdAt = now,
                    updatedAt = now,
                    notes = "Lote de pelo ID: $lotId"
                )
                movementDao.insertMovement(movement)
            }
        }
        return materialId
    }

    override suspend fun updateHairMaterial(material: HairMaterial) {
        hairDao.updateHairMaterial(HairMaterialEntity.fromDomain(material))
    }

    override suspend fun deleteHairMaterial(material: HairMaterial) {
        hairDao.deleteHairMaterial(HairMaterialEntity.fromDomain(material))
    }

    override fun getLotsForHairMaterial(materialId: Long): Flow<List<HairPurchaseLot>> {
        return lotDao.getLotsForHairMaterial(materialId).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun addPurchaseLot(lot: HairPurchaseLot): Long {
        var lotId = 0L
        database.withTransaction {
            val now = DateUtils.getCurrentTimestamp()
            val lotEntity = HairPurchaseLotEntity(
                hairMaterialId = lot.hairMaterialId,
                purchasedQuantityGrams = lot.purchasedQuantityGrams,
                remainingQuantityGrams = lot.purchasedQuantityGrams,
                paidAmountCents = lot.paidAmountCents,
                purchaseDate = lot.purchaseDate,
                notes = lot.notes,
                createdAt = now
            )
            lotId = lotDao.insertLot(lotEntity)

            hairDao.getHairMaterialById(lot.hairMaterialId)?.let { mat ->
                val movement = MovementEntity(
                    type = MovementType.EXPENSE,
                    amountCents = lot.paidAmountCents,
                    description = "Compra pelo: ${mat.name}",
                    category = "Materiales",
                    date = lot.purchaseDate,
                    createdAt = now,
                    updatedAt = now,
                    notes = "Lote ID: $lotId"
                )
                movementDao.insertMovement(movement)
            }
        }
        return lotId
    }

    override fun getConsumptionsForProject(projectId: Long): Flow<List<ProjectHairConsumption>> {
        return consumptionDao.getConsumptionsForProject(projectId).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun assignHairToProject(
        projectId: Long,
        hairMaterialId: Long,
        quantityGrams: Long
    ): Result<ProjectHairConsumption> {
        val availableLotsEntities = lotDao.getAvailableLotsForHairMaterial(hairMaterialId)
        val lots = availableLotsEntities.map { it.toDomain() }

        val allocation = EyeAndHairCostCalculator.allocateHairConsumptionFIFO(quantityGrams, lots)
        if (!allocation.isSuccessful) {
            return Result.failure(Exception(allocation.errorMessage ?: "Stock de pelo insuficiente."))
        }

        var consumptionId = 0L
        database.withTransaction {
            val now = DateUtils.getCurrentTimestamp()

            for (alloc in allocation.allocations) {
                val lotEntity = availableLotsEntities.first { it.id == alloc.lotId }
                val newRemaining = lotEntity.remainingQuantityGrams - alloc.consumedQuantityGrams
                lotDao.updateLot(lotEntity.copy(remainingQuantityGrams = newRemaining))
            }

            val consumptionEntity = ProjectHairConsumptionEntity(
                projectId = projectId,
                hairMaterialId = hairMaterialId,
                consumedQuantityGrams = quantityGrams,
                unitCostCentsPerGram = allocation.averageUnitCostCentsPerGram,
                assignedCostCents = allocation.totalCostCents,
                isCancelled = false,
                date = now,
                createdAt = now
            )
            consumptionId = consumptionDao.insertConsumption(consumptionEntity)
        }

        val created = ProjectHairConsumption(
            id = consumptionId,
            projectId = projectId,
            hairMaterialId = hairMaterialId,
            consumedQuantityGrams = quantityGrams,
            unitCostCentsPerGram = allocation.averageUnitCostCentsPerGram,
            assignedCostCents = allocation.totalCostCents,
            isCancelled = false,
            date = DateUtils.getCurrentTimestamp(),
            createdAt = DateUtils.getCurrentTimestamp()
        )
        return Result.success(created)
    }

    override suspend fun cancelHairConsumption(consumptionId: Long): Result<Unit> {
        val consumption = consumptionDao.getConsumptionById(consumptionId)
            ?: return Result.failure(Exception("Consumo de pelo no encontrado."))
        if (consumption.isCancelled) {
            return Result.failure(Exception("El consumo ya estaba cancelado."))
        }

        database.withTransaction {
            var remToReturn = consumption.consumedQuantityGrams
            val lots = lotDao.getAllLotsForHairMaterial(consumption.hairMaterialId)
            for (lot in lots.sortedByDescending { it.purchaseDate }) {
                if (remToReturn <= 0L) break
                val spaceInLot = lot.purchasedQuantityGrams - lot.remainingQuantityGrams
                val returnToThisLot = minOf(remToReturn, spaceInLot)
                if (returnToThisLot > 0L) {
                    lotDao.updateLot(lot.copy(remainingQuantityGrams = lot.remainingQuantityGrams + returnToThisLot))
                    remToReturn -= returnToThisLot
                }
            }
            consumptionDao.updateConsumption(consumption.copy(isCancelled = true))
        }
        return Result.success(Unit)
    }
}
