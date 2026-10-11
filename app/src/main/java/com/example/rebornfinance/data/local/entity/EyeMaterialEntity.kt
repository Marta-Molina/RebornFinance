package com.example.rebornfinance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.rebornfinance.domain.model.EyeMaterial
import com.example.rebornfinance.domain.model.EyeModality

@Entity(tableName = "eye_materials")
data class EyeMaterialEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val color: String,
    val diameterMm: Double? = null,
    val type: String,
    val subCategory: String? = null,
    val modality: EyeModality,
    val brand: String? = null,
    val photoUri: String? = null,
    val notes: String? = null,
    val isActive: Boolean = true,
    val createdAt: Long,
    val updatedAt: Long
) {
    fun toDomain(): EyeMaterial = EyeMaterial(
        id = id,
        name = name,
        color = color,
        diameterMm = diameterMm,
        type = type,
        subCategory = subCategory,
        modality = modality,
        brand = brand,
        photoUri = photoUri,
        notes = notes,
        isActive = isActive,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    companion object {
        fun fromDomain(m: EyeMaterial): EyeMaterialEntity = EyeMaterialEntity(
            id = m.id,
            name = m.name,
            color = m.color,
            diameterMm = m.diameterMm,
            type = m.type,
            subCategory = m.subCategory,
            modality = m.modality,
            brand = m.brand,
            photoUri = m.photoUri,
            notes = m.notes,
            isActive = m.isActive,
            createdAt = m.createdAt,
            updatedAt = m.updatedAt
        )
    }
}
