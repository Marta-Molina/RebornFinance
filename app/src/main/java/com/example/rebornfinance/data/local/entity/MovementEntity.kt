package com.example.rebornfinance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.rebornfinance.domain.model.Movement
import com.example.rebornfinance.domain.model.MovementType

@Entity(tableName = "movements")
data class MovementEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val type: MovementType,
    val amountCents: Long,
    val description: String,
    val category: String,
    val date: Long,
    val createdAt: Long,
    val updatedAt: Long,
    val notes: String? = null,
    val isCounted: Boolean = true
) {
    fun toDomain(): Movement = Movement(
        id = id,
        type = type,
        amountCents = amountCents,
        description = description,
        category = category,
        date = date,
        createdAt = createdAt,
        updatedAt = updatedAt,
        notes = notes,
        isCounted = isCounted
    )

    companion object {
        fun fromDomain(movement: Movement): MovementEntity = MovementEntity(
            id = movement.id,
            type = movement.type,
            amountCents = movement.amountCents,
            description = movement.description,
            category = movement.category,
            date = movement.date,
            createdAt = movement.createdAt,
            updatedAt = movement.updatedAt,
            notes = movement.notes,
            isCounted = movement.isCounted
        )
    }
}
