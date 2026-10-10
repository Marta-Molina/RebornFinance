package com.example.rebornfinance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.rebornfinance.domain.model.ProjectEyeAssignment

@Entity(tableName = "project_eye_assignments")
data class ProjectEyeAssignmentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val projectId: Long,
    val eyeMaterialId: Long,
    val quantity: Long,
    val assignedCostCents: Long,
    val isCancelled: Boolean = false,
    val date: Long,
    val createdAt: Long
) {
    fun toDomain(): ProjectEyeAssignment = ProjectEyeAssignment(
        id = id,
        projectId = projectId,
        eyeMaterialId = eyeMaterialId,
        quantity = quantity,
        assignedCostCents = assignedCostCents,
        isCancelled = isCancelled,
        date = date,
        createdAt = createdAt
    )

    companion object {
        fun fromDomain(a: ProjectEyeAssignment): ProjectEyeAssignmentEntity = ProjectEyeAssignmentEntity(
            id = a.id,
            projectId = a.projectId,
            eyeMaterialId = a.eyeMaterialId,
            quantity = a.quantity,
            assignedCostCents = a.assignedCostCents,
            isCancelled = a.isCancelled,
            date = a.date,
            createdAt = a.createdAt
        )
    }
}
