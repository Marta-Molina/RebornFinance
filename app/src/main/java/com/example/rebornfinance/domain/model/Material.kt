package com.example.rebornfinance.domain.model

data class Material(
    val id: Long = 0L,
    val name: String,
    val category: String, // "Imprimación", "Pintura", "Barniz", "Otros"
    val brand: String? = null,
    val unit: String, // "ml", "g", "unidades"
    val totalPurchased: Long, // in thousandths (e.g. 10000 = 10.000 ml)
    val availableQuantity: Long, // in thousandths
    val isActive: Boolean = true,
    val createdAt: Long,
    val updatedAt: Long
)
