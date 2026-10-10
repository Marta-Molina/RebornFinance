package com.example.rebornfinance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.rebornfinance.domain.model.SavingsGoal
import com.example.rebornfinance.domain.model.SavingsGoalStatus

@Entity(tableName = "savings_goals")
data class SavingsGoalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val description: String? = null,
    val targetAmountCents: Long,
    val currentAmountCents: Long = 0L,
    val linkedEnvelopeId: Long? = null,
    val targetDate: Long? = null,
    val status: SavingsGoalStatus = SavingsGoalStatus.ACTIVE,
    val createdAt: Long,
    val updatedAt: Long
) {
    fun toDomain(): SavingsGoal = SavingsGoal(
        id = id,
        name = name,
        description = description,
        targetAmountCents = targetAmountCents,
        currentAmountCents = currentAmountCents,
        linkedEnvelopeId = linkedEnvelopeId,
        targetDate = targetDate,
        status = status,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    companion object {
        fun fromDomain(g: SavingsGoal): SavingsGoalEntity = SavingsGoalEntity(
            id = g.id,
            name = g.name,
            description = g.description,
            targetAmountCents = g.targetAmountCents,
            currentAmountCents = g.currentAmountCents,
            linkedEnvelopeId = g.linkedEnvelopeId,
            targetDate = g.targetDate,
            status = g.status,
            createdAt = g.createdAt,
            updatedAt = g.updatedAt
        )
    }
}
