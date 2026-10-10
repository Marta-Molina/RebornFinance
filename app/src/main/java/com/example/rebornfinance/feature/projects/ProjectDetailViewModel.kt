package com.example.rebornfinance.feature.projects

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.rebornfinance.core.date.DateUtils
import com.example.rebornfinance.core.money.MoneyUtils
import com.example.rebornfinance.data.local.AppDatabase
import com.example.rebornfinance.data.repository.EyeMaterialRepositoryImpl
import com.example.rebornfinance.data.repository.HairMaterialRepositoryImpl
import com.example.rebornfinance.data.repository.MaterialRepositoryImpl
import com.example.rebornfinance.data.repository.MovementRepositoryImpl
import com.example.rebornfinance.data.repository.RebornProjectRepositoryImpl
import com.example.rebornfinance.domain.model.EyeMaterial
import com.example.rebornfinance.domain.model.HairMaterial
import com.example.rebornfinance.domain.model.Movement
import com.example.rebornfinance.domain.model.MovementType
import com.example.rebornfinance.domain.model.ProjectCost
import com.example.rebornfinance.domain.model.ProjectEyeAssignment
import com.example.rebornfinance.domain.model.ProjectHairConsumption
import com.example.rebornfinance.domain.model.ProjectMaterialConsumption
import com.example.rebornfinance.domain.model.ProjectStatus
import com.example.rebornfinance.domain.model.RebornProject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProjectDetailViewModel(application: Application, private val projectId: Long) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val projectRepository = RebornProjectRepositoryImpl(database.rebornProjectDao(), database.projectCostDao())
    private val materialRepository = MaterialRepositoryImpl(
        database,
        database.materialDao(),
        database.materialPurchaseLotDao(),
        database.materialConsumptionRuleDao(),
        database.projectMaterialConsumptionDao(),
        database.movementDao()
    )
    private val eyeRepository = EyeMaterialRepositoryImpl(
        database,
        database.eyeMaterialDao(),
        database.eyePurchaseLotDao(),
        database.projectEyeAssignmentDao(),
        database.movementDao()
    )
    private val hairRepository = HairMaterialRepositoryImpl(
        database,
        database.hairMaterialDao(),
        database.hairPurchaseLotDao(),
        database.projectHairConsumptionDao(),
        database.movementDao()
    )
    private val movementRepository = MovementRepositoryImpl(database.movementDao())

    val project: StateFlow<RebornProject?> = MutableStateFlow<RebornProject?>(null).apply {
        viewModelScope.launch {
            value = projectRepository.getProjectById(projectId)
        }
    }

    val costs: StateFlow<List<ProjectCost>> = projectRepository.getCostsForProject(projectId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val materialConsumptions: StateFlow<List<ProjectMaterialConsumption>> = materialRepository.getConsumptionsForProject(projectId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val eyeAssignments: StateFlow<List<ProjectEyeAssignment>> = eyeRepository.getAssignmentsForProject(projectId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val hairConsumptions: StateFlow<List<ProjectHairConsumption>> = hairRepository.getConsumptionsForProject(projectId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allEyeMaterials: StateFlow<List<EyeMaterial>> = eyeRepository.getAllEyeMaterials()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allHairMaterials: StateFlow<List<HairMaterial>> = hairRepository.getAllHairMaterials()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addCost(concept: String, amountStr: String, notes: String?, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val cents = MoneyUtils.parseAmountToCents(amountStr)
        if (cents == null || cents <= 0L) {
            onError("Introduce un importe válido.")
            return
        }
        if (concept.isBlank()) {
            onError("El concepto es obligatorio.")
            return
        }

        viewModelScope.launch {
            val now = DateUtils.getCurrentTimestamp()
            val cost = ProjectCost(
                projectId = projectId,
                concept = concept.trim(),
                amountCents = cents,
                date = now,
                notes = notes?.takeIf { it.isNotBlank() },
                createdAt = now
            )
            projectRepository.insertCost(cost)
            onSuccess()
        }
    }

    fun deleteCost(cost: ProjectCost) {
        viewModelScope.launch {
            projectRepository.deleteCost(cost)
        }
    }

    fun assignMaterialConsumption(category: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val proj = project.value ?: return@launch
            val result = materialRepository.assignMaterialConsumptionForProject(projectId, category, proj.sizeInches)
            if (result.isSuccess) {
                onSuccess()
            } else {
                onError(result.exceptionOrNull()?.message ?: "Error al calcular el consumo de '$category'.")
            }
        }
    }

    fun deleteMaterialConsumption(consumption: ProjectMaterialConsumption) {
        viewModelScope.launch {
            materialRepository.deleteConsumption(consumption)
        }
    }

    fun assignEyes(eyeMaterialId: Long, quantity: Long, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val result = eyeRepository.assignEyesToProject(projectId, eyeMaterialId, quantity)
            if (result.isSuccess) {
                onSuccess()
            } else {
                onError(result.exceptionOrNull()?.message ?: "Error al asignar ojos.")
            }
        }
    }

    fun assignHair(hairMaterialId: Long, quantityGrams: Long, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val result = hairRepository.assignHairToProject(projectId, hairMaterialId, quantityGrams)
            if (result.isSuccess) {
                onSuccess()
            } else {
                onError(result.exceptionOrNull()?.message ?: "Error al asignar pelo.")
            }
        }
    }

    fun cancelEyeAssignment(assignmentId: Long) {
        viewModelScope.launch {
            eyeRepository.cancelEyeAssignment(assignmentId)
        }
    }

    fun cancelHairConsumption(consumptionId: Long) {
        viewModelScope.launch {
            hairRepository.cancelHairConsumption(consumptionId)
        }
    }

    fun updateStatus(newStatus: ProjectStatus) {
        viewModelScope.launch {
            projectRepository.getProjectById(projectId)?.let { proj ->
                val updated = proj.copy(status = newStatus, updatedAt = DateUtils.getCurrentTimestamp())
                projectRepository.updateProject(updated)
                refreshProject()
            }
        }
    }

    fun registerSale(salePriceStr: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val cents = MoneyUtils.parseAmountToCents(salePriceStr)
        if (cents == null || cents <= 0L) {
            onError("Introduce un precio de venta válido.")
            return
        }

        viewModelScope.launch {
            val proj = projectRepository.getProjectById(projectId) ?: return@launch
            val now = DateUtils.getCurrentTimestamp()

            val movement = Movement(
                type = MovementType.INCOME,
                amountCents = cents,
                description = "Venta reborn: ${proj.name}",
                category = "Venta de reborns",
                date = now,
                createdAt = now,
                updatedAt = now,
                notes = "Proyecto vinculado ID: ${proj.id}"
            )
            movementRepository.insertMovement(movement)

            val updated = proj.copy(
                status = ProjectStatus.SOLD,
                actualSalePriceCents = cents,
                saleDate = now,
                updatedAt = now
            )
            projectRepository.updateProject(updated)
            refreshProject()
            onSuccess()
        }
    }

    fun deleteProject(onSuccess: () -> Unit) {
        viewModelScope.launch {
            projectRepository.getProjectById(projectId)?.let { proj ->
                projectRepository.deleteProject(proj)
                onSuccess()
            }
        }
    }

    private fun refreshProject() {
        viewModelScope.launch {
            (project as MutableStateFlow).value = projectRepository.getProjectById(projectId)
        }
    }
}
