package com.example.rebornfinance.domain.model

data class Material(
    val id: Long = 0L,
    val name: String,
    val category: String, // "Imprimación", "Pintura", "Barniz", "Kits", "Rellenos", etc.
    val subCategory: String? = null,
    val brand: String? = null,
    val unit: String, // "ml", "g", "unidades"
    val totalPurchased: Long, // in thousandths
    val availableQuantity: Long, // in thousandths
    val photoUri: String? = null,
    val isActive: Boolean = true,
    val createdAt: Long,
    val updatedAt: Long
)
