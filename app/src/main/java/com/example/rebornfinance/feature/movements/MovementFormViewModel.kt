package com.example.rebornfinance.feature.movements

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.rebornfinance.core.date.DateUtils
import com.example.rebornfinance.core.money.MoneyUtils
import com.example.rebornfinance.data.local.AppDatabase
import com.example.rebornfinance.data.repository.CategoryRepositoryImpl
import com.example.rebornfinance.data.repository.MovementRepositoryImpl
import com.example.rebornfinance.domain.model.Category
import com.example.rebornfinance.domain.model.Movement
import com.example.rebornfinance.domain.model.MovementType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MovementFormViewModel(application: Application, private val movementId: Long?) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val movementRepository = MovementRepositoryImpl(database.movementDao())
    private val categoryRepository = CategoryRepositoryImpl(database.categoryDao())

    val categories: StateFlow<List<Category>> = categoryRepository.getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    var type = MutableStateFlow(MovementType.EXPENSE)
    var amountStr = MutableStateFlow("")
    var description = MutableStateFlow("")
    var category = MutableStateFlow("")
    var dateMillis = MutableStateFlow(DateUtils.getCurrentTimestamp())
    var notes = MutableStateFlow("")

    var isEditMode = movementId != null && movementId > 0L
    private var createdAtOriginal = DateUtils.getCurrentTimestamp()

    init {
        if (isEditMode) {
            viewModelScope.launch {
                movementRepository.getMovementById(movementId!!)?.let { mov ->
                    type.value = mov.type
                    amountStr.value = (mov.amountCents / 100.0).toString().replace('.', ',')
                    description.value = mov.description
                    category.value = mov.category
                    dateMillis.value = mov.date
                    notes.value = mov.notes ?: ""
                    createdAtOriginal = mov.createdAt
                }
            }
        }
    }

    fun saveMovement(onSuccess: () -> Unit, onError: (String) -> Unit) {
        val cents = MoneyUtils.parseAmountToCents(amountStr.value)
        if (cents == null || cents <= 0L) {
            onError("Introduce un importe válido.")
            return
        }
        if (description.value.isBlank()) {
            onError("La descripción es obligatoria.")
            return
        }
        if (category.value.isBlank()) {
            onError("Selecciona una categoría.")
            return
        }

        viewModelScope.launch {
            val now = DateUtils.getCurrentTimestamp()
            val movement = Movement(
                id = movementId ?: 0L,
                type = type.value,
                amountCents = cents,
                description = description.value.trim(),
                category = category.value,
                date = dateMillis.value,
                createdAt = if (isEditMode) createdAtOriginal else now,
                updatedAt = now,
                notes = notes.value.takeIf { it.isNotBlank() }
            )

            if (isEditMode) {
                movementRepository.updateMovement(movement)
            } else {
                movementRepository.insertMovement(movement)
            }
            onSuccess()
        }
    }
}
