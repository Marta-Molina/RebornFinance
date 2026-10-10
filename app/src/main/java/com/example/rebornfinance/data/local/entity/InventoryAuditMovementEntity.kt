package com.example.rebornfinance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.rebornfinance.domain.model.InventoryAuditMovement

@Entity(tableName = "inventory_audit_movements")
data class InventoryAuditMovementEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val materialType: String,
    val materialId: Long,
    val projectId: Long? = null,
    val movementType: String,
    val quantity: Long,
    val date: Long,
    val notes: String? = null,
    val createdAt: Long
) {
    fun toDomain(): InventoryAuditMovement = InventoryAuditMovement(
        id = id,
        materialType = materialType,
        materialId = materialId,
        projectId = projectId,
        movementType = movementType,
        quantity = quantity,
        date = date,
        notes = notes,
        createdAt = createdAt
    )

    companion object {
        fun fromDomain(m: InventoryAuditMovement): InventoryAuditMovementEntity = InventoryAuditMovementEntity(
            id = m.id,
            materialType = m.materialType,
            materialId = m.materialId,
            projectId = m.projectId,
            movementType = m.movementType,
            quantity = m.quantity,
            date = m.date,
            notes = m.notes,
            createdAt = m.createdAt
        )
    }
}
