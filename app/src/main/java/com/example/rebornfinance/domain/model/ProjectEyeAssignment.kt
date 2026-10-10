package com.example.rebornfinance.domain.model

data class ProjectEyeAssignment(
    val id: Long = 0L,
    val projectId: Long,
    val eyeMaterialId: Long,
    val quantity: Long,
    val assignedCostCents: Long,
    val isCancelled: Boolean = false,
    val date: Long,
    val createdAt: Long
)
