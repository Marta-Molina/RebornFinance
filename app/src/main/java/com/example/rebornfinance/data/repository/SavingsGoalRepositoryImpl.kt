package com.example.rebornfinance.data.repository

import com.example.rebornfinance.data.local.dao.SavingsGoalDao
import com.example.rebornfinance.data.local.entity.SavingsGoalEntity
import com.example.rebornfinance.domain.model.SavingsGoal
import com.example.rebornfinance.domain.repository.SavingsGoalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SavingsGoalRepositoryImpl(
    private val savingsGoalDao: SavingsGoalDao
) : SavingsGoalRepository {

    override fun getAllSavingsGoals(): Flow<List<SavingsGoal>> {
        return savingsGoalDao.getAllSavingsGoals().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getSavingsGoalById(id: Long): SavingsGoal? {
        return savingsGoalDao.getSavingsGoalById(id)?.toDomain()
    }

    override suspend fun insertSavingsGoal(goal: SavingsGoal): Long {
        return savingsGoalDao.insertSavingsGoal(SavingsGoalEntity.fromDomain(goal))
    }

    override suspend fun updateSavingsGoal(goal: SavingsGoal) {
        savingsGoalDao.updateSavingsGoal(SavingsGoalEntity.fromDomain(goal))
    }

    override suspend fun deleteSavingsGoal(goal: SavingsGoal) {
        savingsGoalDao.deleteSavingsGoal(SavingsGoalEntity.fromDomain(goal))
    }
}
