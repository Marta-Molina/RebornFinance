package com.example.rebornfinance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.rebornfinance.domain.model.HairPurchaseLot

@Entity(tableName = "hair_purchase_lots")
data class HairPurchaseLotEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val hairMaterialId: Long,
    val purchasedQuantityGrams: Long,
    val remainingQuantityGrams: Long,
    val paidAmountCents: Long,
    val purchaseDate: Long,
    val notes: String? = null,
    val createdAt: Long
) {
    fun toDomain(): HairPurchaseLot = HairPurchaseLot(
        id = id,
        hairMaterialId = hairMaterialId,
        purchasedQuantityGrams = purchasedQuantityGrams,
        remainingQuantityGrams = remainingQuantityGrams,
        paidAmountCents = paidAmountCents,
        purchaseDate = purchaseDate,
        notes = notes,
        createdAt = createdAt
    )

    companion object {
        fun fromDomain(l: HairPurchaseLot): HairPurchaseLotEntity = HairPurchaseLotEntity(
            id = l.id,
            hairMaterialId = l.hairMaterialId,
            purchasedQuantityGrams = l.purchasedQuantityGrams,
            remainingQuantityGrams = l.remainingQuantityGrams,
            paidAmountCents = l.paidAmountCents,
            purchaseDate = l.purchaseDate,
            notes = l.notes,
            createdAt = l.createdAt
        )
    }
}
