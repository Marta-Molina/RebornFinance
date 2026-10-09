package com.example.rebornfinance.data.repository

import androidx.room.withTransaction
import com.example.rebornfinance.core.date.DateUtils
import com.example.rebornfinance.data.local.AppDatabase
import com.example.rebornfinance.data.local.dao.MaterialConsumptionRuleDao
import com.example.rebornfinance.data.local.dao.MaterialDao
import com.example.rebornfinance.data.local.dao.MaterialPurchaseLotDao
import com.example.rebornfinance.data.local.dao.MovementDao
import com.example.rebornfinance.data.local.dao.ProjectMaterialConsumptionDao
import com.example.rebornfinance.data.local.entity.MaterialConsumptionRuleEntity
import com.example.rebornfinance.data.local.entity.MaterialEntity
import com.example.rebornfinance.data.local.entity.MaterialPurchaseLotEntity
import com.example.rebornfinance.data.local.entity.MovementEntity
import com.example.rebornfinance.data.local.entity.ProjectMaterialConsumptionEntity
import com.example.rebornfinance.domain.calculator.MaterialCostCalculator
import com.example.rebornfinance.domain.model.Material
import com.example.rebornfinance.domain.model.MaterialConsumptionRule
import com.example.rebornfinance.domain.model.MaterialPurchaseLot
import com.example.rebornfinance.domain.model.MovementType
import com.example.rebornfinance.domain.model.ProjectMaterialConsumption
import com.example.rebornfinance.domain.repository.MaterialRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MaterialRepositoryImpl(
    private val database: AppDatabase,
    private val materialDao: MaterialDao,
    private val lotDao: MaterialPurchaseLotDao,
    private val ruleDao: MaterialConsumptionRuleDao,
    private val consumptionDao: ProjectMaterialConsumptionDao,
    private val movementDao: MovementDao
) : MaterialRepository {

    override fun getAllMaterials(): Flow<List<Material>> {
        return materialDao.getAllMaterials().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getMaterialById(id: Long): Material? {
        return materialDao.getMaterialById(id)?.toDomain()
    }

    override suspend fun insertMaterial(
        material: Material,
        initialLot: MaterialPurchaseLot?,
        paidAmountCents: Long?
    ): Long {
        var materialId = 0L
        database.withTransaction {
            val now = DateUtils.getCurrentTimestamp()
            val entity = MaterialEntity.fromDomain(material)
            materialId = materialDao.insertMaterial(entity)

            if (initialLot != null && paidAmountCents != null) {
                val lotEntity = MaterialPurchaseLotEntity(
                    materialId = materialId,
                    purchasedQuantity = initialLot.purchasedQuantity,
                    remainingQuantity = initialLot.purchasedQuantity,
                    paidAmountCents = paidAmountCents,
                    purchaseDate = initialLot.purchaseDate,
                    notes = initialLot.notes,
                    createdAt = now
                )
                val lotId = lotDao.insertLot(lotEntity)

                // Record financial movement for purchase (Expense)
                val movement = MovementEntity(
                    type = MovementType.EXPENSE,
                    amountCents = paidAmountCents,
                    description = "Compra material: ${material.name}",
                    category = "Materiales",
                    date = initialLot.purchaseDate,
                    createdAt = now,
                    updatedAt = now,
                    notes = "Lote de inventario ID: $lotId"
                )
                movementDao.insertMovement(movement)
            }
        }
        return materialId
    }

    override suspend fun updateMaterial(material: Material) {
        materialDao.updateMaterial(MaterialEntity.fromDomain(material))
    }

    override suspend fun deleteMaterial(material: Material) {
        materialDao.deleteMaterial(MaterialEntity.fromDomain(material))
    }

    override fun getLotsForMaterial(materialId: Long): Flow<List<MaterialPurchaseLot>> {
        return lotDao.getLotsForMaterial(materialId).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun addPurchaseLot(lot: MaterialPurchaseLot, movementId: Long?): Long {
        var lotId = 0L
        database.withTransaction {
            val now = DateUtils.getCurrentTimestamp()
            val lotEntity = MaterialPurchaseLotEntity(
                materialId = lot.materialId,
                purchasedQuantity = lot.purchasedQuantity,
                remainingQuantity = lot.purchasedQuantity,
                paidAmountCents = lot.paidAmountCents,
                purchaseDate = lot.purchaseDate,
                notes = lot.notes,
                createdAt = now
            )
            lotId = lotDao.insertLot(lotEntity)

            // Update material available quantity and total purchased
            materialDao.getMaterialById(lot.materialId)?.let { mat ->
                val updatedMat = mat.copy(
                    totalPurchased = mat.totalPurchased + lot.purchasedQuantity,
                    availableQuantity = mat.availableQuantity + lot.purchasedQuantity,
                    updatedAt = now
                )
                materialDao.updateMaterial(updatedMat)
            }

            // Record financial movement for purchase
            materialDao.getMaterialById(lot.materialId)?.let { mat ->
                val movement = MovementEntity(
                    type = MovementType.EXPENSE,
                    amountCents = lot.paidAmountCents,
                    description = "Compra material: ${mat.name}",
                    category = "Materiales",
                    date = lot.purchaseDate,
                    createdAt = now,
                    updatedAt = now,
                    notes = "Lote ID: $lotId"
                )
                movementDao.insertMovement(movement)
            }
        }
        return lotId
    }

    override fun getActiveRulesForCategory(category: String): Flow<List<MaterialConsumptionRule>> {
        return ruleDao.getActiveRulesForCategory(category).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getRuleForCategoryAndInches(category: String, inches: Double): MaterialConsumptionRule? {
        return ruleDao.getRuleForCategoryAndInches(category, inches)?.toDomain()
    }

    override suspend fun insertRule(rule: MaterialConsumptionRule): Long {
        return ruleDao.insertRule(MaterialConsumptionRuleEntity.fromDomain(rule))
    }

    override suspend fun updateRule(rule: MaterialConsumptionRule) {
        ruleDao.updateRule(MaterialConsumptionRuleEntity.fromDomain(rule))
    }

    override suspend fun deleteRule(rule: MaterialConsumptionRule) {
        ruleDao.deleteRule(MaterialConsumptionRuleEntity.fromDomain(rule))
    }

    override fun getConsumptionsForProject(projectId: Long): Flow<List<ProjectMaterialConsumption>> {
        return consumptionDao.getConsumptionsForProject(projectId).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun assignMaterialConsumptionForProject(
        projectId: Long,
        category: String,
        kitInches: Double
    ): Result<ProjectMaterialConsumption> {
        val ruleEntity = ruleDao.getRuleForCategoryAndInches(category, kitInches)
            ?: return Result.failure(Exception("No existe regla de consumo configurada para '$category' con $kitInches pulgadas."))

        val materials = materialDao.getActiveMaterialsByCategory(category)
        val material = materials.firstOrNull()
            ?: return Result.failure(Exception("No hay materiales activos en la categoría '$category'."))

        val availableLotsEntities = lotDao.getAvailableLotsForMaterial(material.id)
        val lots = availableLotsEntities.map { it.toDomain() }

        val requestedQty = ruleEntity.estimatedConsumption
        val allocation = MaterialCostCalculator.allocateConsumptionFIFO(requestedQty, lots)

        if (!allocation.isSuccessful) {
            return Result.failure(Exception(allocation.errorMessage ?: "Stock insuficiente para '$category'."))
        }

        var consumptionId = 0L
        database.withTransaction {
            val now = DateUtils.getCurrentTimestamp()

            var remToDeduct = requestedQty
            for (alloc in allocation.allocations) {
                val lotEntity = availableLotsEntities.first { it.id == alloc.lotId }
                val newRemaining = lotEntity.remainingQuantity - alloc.consumedQuantity
                lotDao.updateLot(lotEntity.copy(remainingQuantity = newRemaining))
                remToDeduct -= alloc.consumedQuantity
            }

            val newAvailable = material.availableQuantity - requestedQty
            materialDao.updateMaterial(material.copy(availableQuantity = newAvailable, updatedAt = now))

            val consumptionEntity = ProjectMaterialConsumptionEntity(
                projectId = projectId,
                materialId = material.id,
                category = category,
                consumedQuantity = requestedQty,
                unit = ruleEntity.unit,
                unitCostCents = allocation.averageUnitCostCentsPerThousand,
                assignedCostCents = allocation.totalCostCents,
                isAutomatic = true,
                date = now,
                createdAt = now
            )
            consumptionId = consumptionDao.insertConsumption(consumptionEntity)
        }

        val created = ProjectMaterialConsumption(
            id = consumptionId,
            projectId = projectId,
            materialId = material.id,
            category = category,
            consumedQuantity = requestedQty,
            unit = ruleEntity.unit,
            unitCostCents = allocation.averageUnitCostCentsPerThousand,
            assignedCostCents = allocation.totalCostCents,
            isAutomatic = true,
            date = DateUtils.getCurrentTimestamp(),
            createdAt = DateUtils.getCurrentTimestamp()
        )
        return Result.success(created)
    }

    override suspend fun addManualConsumption(consumption: ProjectMaterialConsumption): Long {
        return consumptionDao.insertConsumption(ProjectMaterialConsumptionEntity.fromDomain(consumption))
    }

    override suspend fun deleteConsumption(consumption: ProjectMaterialConsumption) {
        consumptionDao.deleteConsumption(ProjectMaterialConsumptionEntity.fromDomain(consumption))
    }
}
