package com.example.rebornfinance.domain.model

data class ProjectMaterialConsumption(
    val id: Long = 0L,
    val projectId: Long,
    val materialId: Long? = null,
    val category: String, // "Imprimación", "Pintura", "Barniz"
    val consumedQuantity: Long, // in thousandths
    val unit: String,
    val unitCostCents: Long, // cost per unit in cents (e.g. per ml or gram)
    val assignedCostCents: Long, // total cost assigned for this project
    val isAutomatic: Boolean = true,
    val manualReason: String? = null,
    val date: Long,
    val createdAt: Long
)
