package com.example.rebornfinance.feature.movements

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.rebornfinance.data.local.AppDatabase
import com.example.rebornfinance.data.repository.MovementRepositoryImpl
import com.example.rebornfinance.domain.model.Movement
import com.example.rebornfinance.domain.model.MovementType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MovementsViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val movementRepository = MovementRepositoryImpl(database.movementDao())

    private val allMovements = movementRepository.getAllMovements()

    val searchQuery = MutableStateFlow("")
    val selectedTypeFilter = MutableStateFlow<MovementType?>(null)
    val selectedCategoryFilter = MutableStateFlow<String?>(null)

    val filteredMovements: StateFlow<List<Movement>> = combine(
        allMovements,
        searchQuery,
        selectedTypeFilter,
        selectedCategoryFilter
    ) { movements, query, typeFilter, categoryFilter ->
        movements.filter { mov ->
            val matchesQuery = query.isBlank() ||
                    mov.description.contains(query, ignoreCase = true) ||
                    (mov.notes?.contains(query, ignoreCase = true) == true) ||
                    mov.category.contains(query, ignoreCase = true)

            val matchesType = typeFilter == null || mov.type == typeFilter
            val matchesCategory = categoryFilter.isNullOrBlank() || mov.category == categoryFilter

            matchesQuery && matchesType && matchesCategory
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun setTypeFilter(type: MovementType?) {
        selectedTypeFilter.value = type
    }

    fun setCategoryFilter(category: String?) {
        selectedCategoryFilter.value = category
    }

    fun deleteMovement(movement: Movement) {
        viewModelScope.launch {
            movementRepository.deleteMovement(movement)
        }
    }
}
