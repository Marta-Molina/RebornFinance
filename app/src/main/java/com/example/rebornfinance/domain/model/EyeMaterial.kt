package com.example.rebornfinance.domain.model

data class EyeMaterial(
    val id: Long = 0L,
    val name: String,
    val color: String,
    val diameterMm: Double? = null,
    val type: String, // e.g. "Vidrio", "Acrílico"
    val modality: EyeModality,
    val brand: String? = null,
    val notes: String? = null,
    val isActive: Boolean = true,
    val createdAt: Long,
    val updatedAt: Long
)
