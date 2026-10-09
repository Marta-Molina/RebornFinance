package com.example.rebornfinance.domain.model

data class Movement(
    val id: Long = 0L,
    val type: MovementType,
    val amountCents: Long, // Stored in cents
    val description: String,
    val category: String,
    val date: Long, // Epoch millis of movement date
    val createdAt: Long,
    val updatedAt: Long,
    val notes: String? = null,
    val isCounted: Boolean = true
)
