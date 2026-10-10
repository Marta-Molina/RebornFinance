package com.example.rebornfinance.domain.repository

import com.example.rebornfinance.domain.model.HairMaterial
import com.example.rebornfinance.domain.model.HairPurchaseLot
import com.example.rebornfinance.domain.model.ProjectHairConsumption
import kotlinx.coroutines.flow.Flow

interface HairMaterialRepository {
    fun getAllHairMaterials(): Flow<List<HairMaterial>>
    suspend fun getHairMaterialById(id: Long): HairMaterial?
    suspend fun insertHairMaterial(material: HairMaterial, initialLot: HairPurchaseLot?, paidAmountCents: Long?): Long
    suspend fun updateHairMaterial(material: HairMaterial)
    suspend fun deleteHairMaterial(material: HairMaterial)

    fun getLotsForHairMaterial(materialId: Long): Flow<List<HairPurchaseLot>>
    suspend fun addPurchaseLot(lot: HairPurchaseLot): Long

    fun getConsumptionsForProject(projectId: Long): Flow<List<ProjectHairConsumption>>
    suspend fun assignHairToProject(projectId: Long, hairMaterialId: Long, quantityGrams: Long): Result<ProjectHairConsumption>
    suspend fun cancelHairConsumption(consumptionId: Long): Result<Unit>
}
