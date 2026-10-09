package com.example.rebornfinance.domain.model

data class FinancialSummary(
    val currentBalanceCents: Long,
    val monthlyIncomeCents: Long,
    val monthlyExpenseCents: Long,
    val monthlyRefundCents: Long,
    val monthlyNetCents: Long
)
