package com.example.rebornfinance.data.repository

import com.example.rebornfinance.data.local.dao.BudgetDao
import com.example.rebornfinance.data.local.entity.BudgetEntity
import com.example.rebornfinance.domain.model.Budget
import com.example.rebornfinance.domain.repository.BudgetRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BudgetRepositoryImpl(
    private val budgetDao: BudgetDao
) : BudgetRepository {

    override fun getAllBudgets(): Flow<List<Budget>> {
        return budgetDao.getAllBudgets().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getBudgetById(id: Long): Budget? {
        return budgetDao.getBudgetById(id)?.toDomain()
    }

    override suspend fun insertBudget(budget: Budget): Long {
        return budgetDao.insertBudget(BudgetEntity.fromDomain(budget))
    }

    override suspend fun updateBudget(budget: Budget) {
        budgetDao.updateBudget(BudgetEntity.fromDomain(budget))
    }

    override suspend fun deleteBudget(budget: Budget) {
        budgetDao.deleteBudget(BudgetEntity.fromDomain(budget))
    }
}
