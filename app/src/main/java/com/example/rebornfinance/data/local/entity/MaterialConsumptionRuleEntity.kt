package com.example.rebornfinance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.rebornfinance.domain.model.MaterialConsumptionRule

@Entity(tableName = "material_consumption_rules")
data class MaterialConsumptionRuleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val category: String,
    val minInches: Double,
    val maxInches: Double,
    val estimatedConsumption: Long,
    val unit: String,
    val isActive: Boolean = true,
    val createdAt: Long
) {
    fun toDomain(): MaterialConsumptionRule = MaterialConsumptionRule(
        id = id,
        category = category,
        minInches = minInches,
        maxInches = maxInches,
        estimatedConsumption = estimatedConsumption,
        unit = unit,
        isActive = isActive,
        createdAt = createdAt
    )

    companion object {
        fun fromDomain(rule: MaterialConsumptionRule): MaterialConsumptionRuleEntity = MaterialConsumptionRuleEntity(
            id = rule.id,
            category = rule.category,
            minInches = rule.minInches,
            maxInches = rule.maxInches,
            estimatedConsumption = rule.estimatedConsumption,
            unit = rule.unit,
            isActive = rule.isActive,
            createdAt = rule.createdAt
        )
    }
}
