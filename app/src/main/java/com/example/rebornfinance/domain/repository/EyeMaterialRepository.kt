package com.example.rebornfinance.domain.repository

import com.example.rebornfinance.domain.model.EyeMaterial
import com.example.rebornfinance.domain.model.EyePurchaseLot
import com.example.rebornfinance.domain.model.ProjectEyeAssignment
import kotlinx.coroutines.flow.Flow

interface EyeMaterialRepository {
    fun getAllEyeMaterials(): Flow<List<EyeMaterial>>
    suspend fun getEyeMaterialById(id: Long): EyeMaterial?
    suspend fun insertEyeMaterial(material: EyeMaterial, initialLot: EyePurchaseLot?, paidAmountCents: Long?): Long
    suspend fun updateEyeMaterial(material: EyeMaterial)
    suspend fun deleteEyeMaterial(material: EyeMaterial)

    fun getLotsForEyeMaterial(materialId: Long): Flow<List<EyePurchaseLot>>
    suspend fun addPurchaseLot(lot: EyePurchaseLot): Long

    fun getAssignmentsForProject(projectId: Long): Flow<List<ProjectEyeAssignment>>
    suspend fun assignEyesToProject(projectId: Long, eyeMaterialId: Long, quantity: Long): Result<ProjectEyeAssignment>
    suspend fun cancelEyeAssignment(assignmentId: Long): Result<Unit>
}
