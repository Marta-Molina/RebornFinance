package com.example.rebornfinance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.rebornfinance.domain.model.Material

@Entity(tableName = "materials")
data class MaterialEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val category: String,
    val subCategory: String? = null,
    val brand: String? = null,
    val unit: String,
    val totalPurchased: Long,
    val availableQuantity: Long,
    val photoUri: String? = null,
    val isActive: Boolean = true,
    val createdAt: Long,
    val updatedAt: Long
) {
    fun toDomain(): Material = Material(
        id = id,
        name = name,
        category = category,
        subCategory = subCategory,
        brand = brand,
        unit = unit,
        totalPurchased = totalPurchased,
        availableQuantity = availableQuantity,
        photoUri = photoUri,
        isActive = isActive,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    companion object {
        fun fromDomain(m: Material): MaterialEntity = MaterialEntity(
            id = m.id,
            name = m.name,
            category = m.category,
            subCategory = m.subCategory,
            brand = m.brand,
            unit = m.unit,
            totalPurchased = m.totalPurchased,
            availableQuantity = m.availableQuantity,
            photoUri = m.photoUri,
            isActive = m.isActive,
            createdAt = m.createdAt,
            updatedAt = m.updatedAt
        )
    }
}
