package com.example.rebornfinance.domain.model

data class SavingsGoal(
    val id: Long = 0L,
    val name: String,
    val description: String? = null,
    val targetAmountCents: Long,
    val currentAmountCents: Long = 0L,
    val linkedEnvelopeId: Long? = null,
    val targetDate: Long? = null,
    val status: SavingsGoalStatus = SavingsGoalStatus.ACTIVE,
    val createdAt: Long,
    val updatedAt: Long
)
