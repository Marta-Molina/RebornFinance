package com.example.rebornfinance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.rebornfinance.domain.model.EyePurchaseLot

@Entity(tableName = "eye_purchase_lots")
data class EyePurchaseLotEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val eyeMaterialId: Long,
    val purchasedQuantity: Long,
    val remainingQuantity: Long,
    val paidAmountCents: Long,
    val purchaseDate: Long,
    val notes: String? = null,
    val createdAt: Long
) {
    fun toDomain(): EyePurchaseLot = EyePurchaseLot(
        id = id,
        eyeMaterialId = eyeMaterialId,
        purchasedQuantity = purchasedQuantity,
        remainingQuantity = remainingQuantity,
        paidAmountCents = paidAmountCents,
        purchaseDate = purchaseDate,
        notes = notes,
        createdAt = createdAt
    )

    companion object {
        fun fromDomain(l: EyePurchaseLot): EyePurchaseLotEntity = EyePurchaseLotEntity(
            id = l.id,
            eyeMaterialId = l.eyeMaterialId,
            purchasedQuantity = l.purchasedQuantity,
            remainingQuantity = l.remainingQuantity,
            paidAmountCents = l.paidAmountCents,
            purchaseDate = l.purchaseDate,
            notes = l.notes,
            createdAt = l.createdAt
        )
    }
}
