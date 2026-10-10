package com.example.rebornfinance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.rebornfinance.domain.model.ProjectHairConsumption

@Entity(tableName = "project_hair_consumptions")
data class ProjectHairConsumptionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val projectId: Long,
    val hairMaterialId: Long,
    val consumedQuantityGrams: Long,
    val unitCostCentsPerGram: Long,
    val assignedCostCents: Long,
    val manualReason: String? = null,
    val isCancelled: Boolean = false,
    val date: Long,
    val createdAt: Long
) {
    fun toDomain(): ProjectHairConsumption = ProjectHairConsumption(
        id = id,
        projectId = projectId,
        hairMaterialId = hairMaterialId,
        consumedQuantityGrams = consumedQuantityGrams,
        unitCostCentsPerGram = unitCostCentsPerGram,
        assignedCostCents = assignedCostCents,
        manualReason = manualReason,
        isCancelled = isCancelled,
        date = date,
        createdAt = createdAt
    )

    companion object {
        fun fromDomain(c: ProjectHairConsumption): ProjectHairConsumptionEntity = ProjectHairConsumptionEntity(
            id = c.id,
            projectId = c.projectId,
            hairMaterialId = c.hairMaterialId,
            consumedQuantityGrams = c.consumedQuantityGrams,
            unitCostCentsPerGram = c.unitCostCentsPerGram,
            assignedCostCents = c.assignedCostCents,
            manualReason = c.manualReason,
            isCancelled = c.isCancelled,
            date = c.date,
            createdAt = c.createdAt
        )
    }
}
