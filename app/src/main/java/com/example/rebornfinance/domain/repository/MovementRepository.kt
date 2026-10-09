package com.example.rebornfinance.domain.repository

import com.example.rebornfinance.domain.model.Movement
import kotlinx.coroutines.flow.Flow

interface MovementRepository {
    fun getAllMovements(): Flow<List<Movement>>
    suspend fun getMovementById(id: Long): Movement?
    suspend fun insertMovement(movement: Movement): Long
    suspend fun updateMovement(movement: Movement)
    suspend fun deleteMovement(movement: Movement)
}
