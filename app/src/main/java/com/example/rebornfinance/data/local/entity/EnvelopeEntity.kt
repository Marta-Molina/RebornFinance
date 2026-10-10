package com.example.rebornfinance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.rebornfinance.domain.model.Envelope

@Entity(tableName = "envelopes")
data class EnvelopeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val description: String? = null,
    val targetAmountCents: Long? = null,
    val currentAmountCents: Long = 0L,
    val color: String? = null,
    val isActive: Boolean = true,
    val createdAt: Long,
    val updatedAt: Long
) {
    fun toDomain(): Envelope = Envelope(
        id = id,
        name = name,
        description = description,
        targetAmountCents = targetAmountCents,
        currentAmountCents = currentAmountCents,
        color = color,
        isActive = isActive,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    companion object {
        fun fromDomain(e: Envelope): EnvelopeEntity = EnvelopeEntity(
            id = e.id,
            name = e.name,
            description = e.description,
            targetAmountCents = e.targetAmountCents,
            currentAmountCents = e.currentAmountCents,
            color = e.color,
            isActive = e.isActive,
            createdAt = e.createdAt,
            updatedAt = e.updatedAt
        )
    }
}
