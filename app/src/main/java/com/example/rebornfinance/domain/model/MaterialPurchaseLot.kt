package com.example.rebornfinance.domain.model

data class MaterialPurchaseLot(
    val id: Long = 0L,
    val materialId: Long,
    val purchasedQuantity: Long, // in thousandths
    val remainingQuantity: Long, // in thousandths
    val paidAmountCents: Long,
    val purchaseDate: Long,
    val notes: String? = null,
    val createdAt: Long
)
