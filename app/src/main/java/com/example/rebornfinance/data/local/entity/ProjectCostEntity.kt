package com.example.rebornfinance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.rebornfinance.domain.model.ProjectCost

@Entity(tableName = "project_costs")
data class ProjectCostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val projectId: Long,
    val concept: String,
    val amountCents: Long,
    val date: Long,
    val notes: String? = null,
    val createdAt: Long
) {
    fun toDomain(): ProjectCost = ProjectCost(
        id = id,
        projectId = projectId,
        concept = concept,
        amountCents = amountCents,
        date = date,
        notes = notes,
        createdAt = createdAt
    )

    companion object {
        fun fromDomain(cost: ProjectCost): ProjectCostEntity = ProjectCostEntity(
            id = cost.id,
            projectId = cost.projectId,
            concept = cost.concept,
            amountCents = cost.amountCents,
            date = cost.date,
            notes = cost.notes,
            createdAt = cost.createdAt
        )
    }
}
