package com.example.rebornfinance.domain.repository

import com.example.rebornfinance.domain.model.SavingsGoal
import kotlinx.coroutines.flow.Flow

interface SavingsGoalRepository {
    fun getAllSavingsGoals(): Flow<List<SavingsGoal>>
    suspend fun getSavingsGoalById(id: Long): SavingsGoal?
    suspend fun insertSavingsGoal(goal: SavingsGoal): Long
    suspend fun updateSavingsGoal(goal: SavingsGoal)
    suspend fun deleteSavingsGoal(goal: SavingsGoal)
}
