package com.example.rebornfinance.feature.projects

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.rebornfinance.core.date.DateUtils
import com.example.rebornfinance.core.money.MoneyUtils
import com.example.rebornfinance.data.local.AppDatabase
import com.example.rebornfinance.data.repository.RebornProjectRepositoryImpl
import com.example.rebornfinance.domain.model.ProjectStatus
import com.example.rebornfinance.domain.model.RebornProject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class ProjectFormViewModel(application: Application, private val projectId: Long?) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = RebornProjectRepositoryImpl(database.rebornProjectDao(), database.projectCostDao())

    val name = MutableStateFlow("")
    val kitName = MutableStateFlow("")
    val sculptorName = MutableStateFlow("")
    val sizeInchesStr = MutableStateFlow("")
    val status = MutableStateFlow(ProjectStatus.PLANNED)
    val predictedPriceStr = MutableStateFlow("")
    val notes = MutableStateFlow("")

    val isEditMode = projectId != null && projectId > 0L
    private var createdAtOriginal = DateUtils.getCurrentTimestamp()
    private var startDateOriginal = DateUtils.getCurrentTimestamp()

    init {
        if (isEditMode) {
            viewModelScope.launch {
                repository.getProjectById(projectId!!)?.let { proj ->
                    name.value = proj.name
                    kitName.value = proj.kitName
                    sculptorName.value = proj.sculptorName
                    sizeInchesStr.value = proj.sizeInches.toString().replace('.', ',')
                    status.value = proj.status
                    predictedPriceStr.value = proj.predictedSalePriceCents?.let { (it / 100.0).toString().replace('.', ',') } ?: ""
                    notes.value = proj.notes ?: ""
                    createdAtOriginal = proj.createdAt
                    startDateOriginal = proj.startDate
                }
            }
        }
    }

    fun saveProject(onSuccess: () -> Unit, onError: (String) -> Unit) {
        if (name.value.isBlank()) {
            onError("El nombre del proyecto es obligatorio.")
            return
        }
        val size = sizeInchesStr.value.replace(',', '.').toDoubleOrNull()
        if (size != null && size <= 0.0) {
            onError("El tamaño en pulgadas debe ser mayor que 0.")
            return
        }

        val predictedCents = if (predictedPriceStr.value.isNotBlank()) {
            MoneyUtils.parseAmountToCents(predictedPriceStr.value)
        } else null

        viewModelScope.launch {
            val now = DateUtils.getCurrentTimestamp()
            val project = RebornProject(
                id = projectId ?: 0L,
                name = name.value.trim(),
                kitName = kitName.value.trim(),
                sculptorName = sculptorName.value.trim(),
                sizeInches = size ?: 0.0,
                startDate = startDateOriginal,
                status = status.value,
                predictedSalePriceCents = predictedCents,
                actualSalePriceCents = null,
                saleDate = null,
                notes = notes.value.takeIf { it.isNotBlank() },
                createdAt = if (isEditMode) createdAtOriginal else now,
                updatedAt = now
            )

            if (isEditMode) {
                repository.updateProject(project)
            } else {
                repository.insertProject(project)
            }
            onSuccess()
        }
    }
}
