package com.example.rebornfinance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.rebornfinance.domain.model.MaterialAdjustment

@Entity(tableName = "material_adjustments")
data class MaterialAdjustmentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val materialId: Long,
    val quantityDelta: Long,
    val reason: String,
    val date: Long,
    val createdAt: Long
) {
    fun toDomain(): MaterialAdjustment = MaterialAdjustment(
        id = id,
        materialId = materialId,
        quantityDelta = quantityDelta,
        reason = reason,
        date = date,
        createdAt = createdAt
    )

    companion object {
        fun fromDomain(a: MaterialAdjustment): MaterialAdjustmentEntity = MaterialAdjustmentEntity(
            id = a.id,
            materialId = a.materialId,
            quantityDelta = a.quantityDelta,
            reason = a.reason,
            date = a.date,
            createdAt = a.createdAt
        )
    }
}
