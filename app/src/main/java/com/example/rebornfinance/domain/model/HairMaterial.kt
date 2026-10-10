package com.example.rebornfinance.domain.model

data class HairMaterial(
    val id: Long = 0L,
    val name: String,
    val hairType: String, // e.g. "Mohair", "Pelo humano"
    val color: String,
    val brand: String? = null,
    val notes: String? = null,
    val isActive: Boolean = true,
    val createdAt: Long,
    val updatedAt: Long
)
