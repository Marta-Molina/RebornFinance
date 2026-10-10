package com.example.rebornfinance.domain.model

data class Budget(
    val id: Long = 0L,
    val category: String,
    val limitAmountCents: Long,
    val period: BudgetPeriod,
    val startDate: Long,
    val endDate: Long,
    val isActive: Boolean = true,
    val createdAt: Long,
    val updatedAt: Long
)
