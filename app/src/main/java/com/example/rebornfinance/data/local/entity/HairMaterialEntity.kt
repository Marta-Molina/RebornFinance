package com.example.rebornfinance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.rebornfinance.domain.model.HairMaterial

@Entity(tableName = "hair_materials")
data class HairMaterialEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val hairType: String,
    val color: String,
    val brand: String? = null,
    val notes: String? = null,
    val isActive: Boolean = true,
    val createdAt: Long,
    val updatedAt: Long
) {
    fun toDomain(): HairMaterial = HairMaterial(
        id = id,
        name = name,
        hairType = hairType,
        color = color,
        brand = brand,
        notes = notes,
        isActive = isActive,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    companion object {
        fun fromDomain(m: HairMaterial): HairMaterialEntity = HairMaterialEntity(
            id = m.id,
            name = m.name,
            hairType = m.hairType,
            color = m.color,
            brand = m.brand,
            notes = m.notes,
            isActive = m.isActive,
            createdAt = m.createdAt,
            updatedAt = m.updatedAt
        )
    }
}
