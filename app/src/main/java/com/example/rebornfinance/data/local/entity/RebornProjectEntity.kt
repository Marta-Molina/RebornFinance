package com.example.rebornfinance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.rebornfinance.domain.model.ProjectStatus
import com.example.rebornfinance.domain.model.RebornProject

@Entity(tableName = "reborn_projects")
data class RebornProjectEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val kitName: String,
    val sculptorName: String,
    val sizeInches: Double,
    val startDate: Long,
    val status: ProjectStatus,
    val predictedSalePriceCents: Long? = null,
    val actualSalePriceCents: Long? = null,
    val saleDate: Long? = null,
    val notes: String? = null,
    val paintingSystem: String = "NONE",
    val createdAt: Long,
    val updatedAt: Long
) {
    fun toDomain(): RebornProject = RebornProject(
        id = id,
        name = name,
        kitName = kitName,
        sculptorName = sculptorName,
        sizeInches = sizeInches,
        startDate = startDate,
        status = status,
        predictedSalePriceCents = predictedSalePriceCents,
        actualSalePriceCents = actualSalePriceCents,
        saleDate = saleDate,
        notes = notes,
        paintingSystem = paintingSystem,
        createdAt = createdAt,
        updatedAt = updatedAt
    )

    companion object {
        fun fromDomain(project: RebornProject): RebornProjectEntity = RebornProjectEntity(
            id = project.id,
            name = project.name,
            kitName = project.kitName,
            sculptorName = project.sculptorName,
            sizeInches = project.sizeInches,
            startDate = project.startDate,
            status = project.status,
            predictedSalePriceCents = project.predictedSalePriceCents,
            actualSalePriceCents = project.actualSalePriceCents,
            saleDate = project.saleDate,
            notes = project.notes,
            paintingSystem = project.paintingSystem,
            createdAt = project.createdAt,
            updatedAt = project.updatedAt
        )
    }
}
