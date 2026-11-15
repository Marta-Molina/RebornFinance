package com.example.rebornfinance.domain.model

data class EyePurchaseLot(
    val id: Long = 0L,
    val eyeMaterialId: Long,
    val movementId: Long? = null,
    val purchasedQuantity: Long,
    val remainingQuantity: Long,
    val paidAmountCents: Long,
    val purchaseDate: Long,
    val notes: String? = null,
    val createdAt: Long
)
