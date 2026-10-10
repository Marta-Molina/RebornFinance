package com.example.rebornfinance.domain.model

data class HairPurchaseLot(
    val id: Long = 0L,
    val hairMaterialId: Long,
    val purchasedQuantityGrams: Long, // in thousandths (e.g. 10000 = 10.000 g)
    val remainingQuantityGrams: Long,
    val paidAmountCents: Long,
    val purchaseDate: Long,
    val notes: String? = null,
    val createdAt: Long
)
