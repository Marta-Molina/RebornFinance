package com.example.rebornfinance.data.repository

import com.example.rebornfinance.data.local.dao.MovementDao
import com.example.rebornfinance.data.local.entity.MovementEntity
import com.example.rebornfinance.domain.model.Movement
import com.example.rebornfinance.domain.repository.MovementRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MovementRepositoryImpl(
    private val movementDao: MovementDao
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
}
