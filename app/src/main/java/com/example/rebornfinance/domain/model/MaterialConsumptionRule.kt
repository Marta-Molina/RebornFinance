package com.example.rebornfinance.domain.model

data class MaterialConsumptionRule(
    val id: Long = 0L,
    val category: String, // "Imprimación", "Pintura", "Barniz"
    val minInches: Double,
    val maxInches: Double,
    val estimatedConsumption: Long, // in thousandths
    val unit: String,
    val isActive: Boolean = true,
    val createdAt: Long
)
