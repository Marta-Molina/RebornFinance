package com.example.rebornfinance.domain.repository

import com.example.rebornfinance.domain.model.Budget
import kotlinx.coroutines.flow.Flow

interface BudgetRepository {
    fun getAllBudgets(): Flow<List<Budget>>
    suspend fun getBudgetById(id: Long): Budget?
    suspend fun insertBudget(budget: Budget): Long
    suspend fun updateBudget(budget: Budget)
    suspend fun deleteBudget(budget: Budget)
}
