package com.example.rebornfinance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.rebornfinance.domain.model.ProjectMaterialConsumption

@Entity(tableName = "project_material_consumptions")
data class ProjectMaterialConsumptionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val projectId: Long,
    val materialId: Long? = null,
    val category: String,
    val consumedQuantity: Long,
    val unit: String,
    val unitCostCents: Long,
    val assignedCostCents: Long,
    val isAutomatic: Boolean = true,
    val manualReason: String? = null,
    val date: Long,
    val createdAt: Long
) {
    fun toDomain(): ProjectMaterialConsumption = ProjectMaterialConsumption(
        id = id,
        projectId = projectId,
        materialId = materialId,
        category = category,
        consumedQuantity = consumedQuantity,
        unit = unit,
        unitCostCents = unitCostCents,
        assignedCostCents = assignedCostCents,
        isAutomatic = isAutomatic,
        manualReason = manualReason,
        date = date,
        createdAt = createdAt
    )

    companion object {
        fun fromDomain(c: ProjectMaterialConsumption): ProjectMaterialConsumptionEntity = ProjectMaterialConsumptionEntity(
            id = c.id,
            projectId = c.projectId,
            materialId = c.materialId,
            category = c.category,
            consumedQuantity = c.consumedQuantity,
            unit = c.unit,
            unitCostCents = c.unitCostCents,
            assignedCostCents = c.assignedCostCents,
            isAutomatic = c.isAutomatic,
            manualReason = c.manualReason,
            date = c.date,
            createdAt = c.createdAt
        )
    }
}
