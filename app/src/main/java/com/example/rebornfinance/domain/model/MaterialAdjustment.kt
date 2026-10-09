package com.example.rebornfinance.domain.model

data class MaterialAdjustment(
    val id: Long = 0L,
    val materialId: Long,
    val quantityDelta: Long, // in thousandths (can be positive or negative)
    val reason: String,
    val date: Long,
    val createdAt: Long
)
