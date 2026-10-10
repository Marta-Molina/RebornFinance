package com.example.rebornfinance.domain.model

data class InventoryAuditMovement(
    val id: Long = 0L,
    val materialType: String, // "EYE" or "HAIR"
    val materialId: Long,
    val projectId: Long? = null,
    val movementType: String, // "PURCHASE", "ASSIGNMENT", "CANCELLATION", "ADJUSTMENT"
    val quantity: Long,
    val date: Long,
    val notes: String? = null,
    val createdAt: Long
)
