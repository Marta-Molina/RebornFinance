package com.example.rebornfinance.domain.model

data class ProjectCost(
    val id: Long = 0L,
    val projectId: Long,
    val concept: String,
    val amountCents: Long,
    val date: Long,
    val notes: String? = null,
    val createdAt: Long
)
