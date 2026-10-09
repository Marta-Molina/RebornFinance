package com.example.rebornfinance.domain.repository

import com.example.rebornfinance.domain.model.Material
import com.example.rebornfinance.domain.model.MaterialConsumptionRule
import com.example.rebornfinance.domain.model.MaterialPurchaseLot
import com.example.rebornfinance.domain.model.ProjectMaterialConsumption
import kotlinx.coroutines.flow.Flow

interface MaterialRepository {
    fun getAllMaterials(): Flow<List<Material>>
    suspend fun getMaterialById(id: Long): Material?
    suspend fun insertMaterial(material: Material, initialLot: MaterialPurchaseLot?, paidAmountCents: Long?): Long
    suspend fun updateMaterial(material: Material)
    suspend fun deleteMaterial(material: Material)

    fun getLotsForMaterial(materialId: Long): Flow<List<MaterialPurchaseLot>>
    suspend fun addPurchaseLot(lot: MaterialPurchaseLot, movementId: Long?): Long

    fun getActiveRulesForCategory(category: String): Flow<List<MaterialConsumptionRule>>
    suspend fun getRuleForCategoryAndInches(category: String, inches: Double): MaterialConsumptionRule?
    suspend fun insertRule(rule: MaterialConsumptionRule): Long
    suspend fun updateRule(rule: MaterialConsumptionRule)
    suspend fun deleteRule(rule: MaterialConsumptionRule)

    fun getConsumptionsForProject(projectId: Long): Flow<List<ProjectMaterialConsumption>>
    suspend fun assignMaterialConsumptionForProject(
        projectId: Long,
        category: String,
        kitInches: Double
    ): Result<ProjectMaterialConsumption>
    suspend fun addManualConsumption(consumption: ProjectMaterialConsumption): Long
    suspend fun deleteConsumption(consumption: ProjectMaterialConsumption)
}
