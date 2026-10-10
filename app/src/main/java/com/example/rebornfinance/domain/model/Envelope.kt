package com.example.rebornfinance.domain.model

data class Envelope(
    val id: Long = 0L,
    val name: String,
    val description: String? = null,
    val targetAmountCents: Long? = null,
    val currentAmountCents: Long = 0L,
    val color: String? = null,
    val isActive: Boolean = true,
    val createdAt: Long,
    val updatedAt: Long
)
