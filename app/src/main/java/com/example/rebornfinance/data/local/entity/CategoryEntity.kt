package com.example.rebornfinance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.rebornfinance.domain.model.Category
import com.example.rebornfinance.domain.model.MovementType

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val type: MovementType,
    val isActive: Boolean = true
) {
    fun toDomain(): Category = Category(
        id = id,
        name = name,
        type = type,
        isActive = isActive
    )

    companion object {
        fun fromDomain(category: Category): CategoryEntity = CategoryEntity(
            id = category.id,
            name = category.name,
            type = category.type,
            isActive = category.isActive
        )
    }
}
