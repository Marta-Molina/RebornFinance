package com.example.rebornfinance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.rebornfinance.domain.model.EnvelopeOperation

@Entity(tableName = "envelope_operations")
data class EnvelopeOperationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val envelopeId: Long,
    val operationType: String,
    val targetEnvelopeId: Long? = null,
    val amountCents: Long,
    val date: Long,
    val notes: String? = null,
    val createdAt: Long
) {
    fun toDomain(): EnvelopeOperation = EnvelopeOperation(
        id = id,
        envelopeId = envelopeId,
        operationType = operationType,
        targetEnvelopeId = targetEnvelopeId,
        amountCents = amountCents,
        date = date,
        notes = notes,
        createdAt = createdAt
    )

    companion object {
        fun fromDomain(op: EnvelopeOperation): EnvelopeOperationEntity = EnvelopeOperationEntity(
            id = op.id,
            envelopeId = op.envelopeId,
            operationType = op.operationType,
            targetEnvelopeId = op.targetEnvelopeId,
            amountCents = op.amountCents,
            date = op.date,
            notes = op.notes,
            createdAt = op.createdAt
        )
    }
}
