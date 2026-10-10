package com.example.rebornfinance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.rebornfinance.domain.model.Budget
import com.example.rebornfinance.domain.model.BudgetPeriod

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val category: String,
    val limitAmountCents: Long,
    val period: BudgetPeriod,
    val startDate: Long,
    val endDate: Long,
    val isActive: Boolean = true,
    val createdAt: Long,
    val updatedAt: Long
) {
    fun toDomain(): Budget = Budget(
        id = id,
        category = category,
        limitAmountCents = limitAmountCents,
        period = period,
        startDate = startDate,
        endDate = endDate,
        isActive = isActive,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    companion object {
        fun fromDomain(b: Budget): BudgetEntity = BudgetEntity(
            id = b.id,
            category = b.category,
            limitAmountCents = b.limitAmountCents,
            period = b.period,
            startDate = b.startDate,
            endDate = b.endDate,
            isActive = b.isActive,
            createdAt = b.createdAt,
            updatedAt = b.updatedAt
        )
    }
}
