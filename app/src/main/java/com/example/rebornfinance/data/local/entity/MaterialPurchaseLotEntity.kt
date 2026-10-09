package com.example.rebornfinance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.rebornfinance.domain.model.MaterialPurchaseLot

@Entity(tableName = "material_purchase_lots")
data class MaterialPurchaseLotEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val materialId: Long,
    val purchasedQuantity: Long,
    val remainingQuantity: Long,
    val paidAmountCents: Long,
    val purchaseDate: Long,
    val notes: String? = null,
    val createdAt: Long
) {
    fun toDomain(): MaterialPurchaseLot = MaterialPurchaseLot(
        id = id,
        materialId = materialId,
        purchasedQuantity = purchasedQuantity,
        remainingQuantity = remainingQuantity,
        paidAmountCents = paidAmountCents,
        purchaseDate = purchaseDate,
        notes = notes,
        createdAt = createdAt
    )

    companion object {
        fun fromDomain(lot: MaterialPurchaseLot): MaterialPurchaseLotEntity = MaterialPurchaseLotEntity(
            id = lot.id,
            materialId = lot.materialId,
            purchasedQuantity = lot.purchasedQuantity,
            remainingQuantity = lot.remainingQuantity,
            paidAmountCents = lot.paidAmountCents,
            purchaseDate = lot.purchaseDate,
            notes = lot.notes,
            createdAt = lot.createdAt
        )
    }
}
