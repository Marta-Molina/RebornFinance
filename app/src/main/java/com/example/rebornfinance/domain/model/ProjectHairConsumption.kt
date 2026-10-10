package com.example.rebornfinance.domain.model

data class ProjectHairConsumption(
    val id: Long = 0L,
    val projectId: Long,
    val hairMaterialId: Long,
    val consumedQuantityGrams: Long, // in thousandths
    val unitCostCentsPerGram: Long,
    val assignedCostCents: Long,
    val manualReason: String? = null,
    val isCancelled: Boolean = false,
    val date: Long,
    val createdAt: Long
)
