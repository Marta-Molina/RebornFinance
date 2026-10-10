package com.example.rebornfinance.feature.statistics

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.rebornfinance.core.util.CsvExportUtils
import com.example.rebornfinance.data.local.AppDatabase
import com.example.rebornfinance.data.repository.MovementRepositoryImpl
import com.example.rebornfinance.domain.calculator.FinancialStatistics
import com.example.rebornfinance.domain.calculator.StatisticsCalculator
import com.example.rebornfinance.domain.model.Movement
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StatisticsViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val movementRepository = MovementRepositoryImpl(database.movementDao())

    val movements: StateFlow<List<Movement>> = movementRepository.getAllMovements()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val selectedFilter = MutableStateFlow("THIS_MONTH")

    val statistics: StateFlow<FinancialStatistics> = combine(
        movements,
        selectedFilter
    ) { movs, filter ->
        val range = StatisticsCalculator.getDateRangeForFilter(filter)
        StatisticsCalculator.calculateStatistics(movs, range.first, range.second)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        FinancialStatistics(0L, 0L, 0L, emptyMap(), emptyMap(), 0)
    )

    fun setFilter(filterType: String) {
        selectedFilter.value = filterType
    }

    fun exportCsvData(): String {
        return CsvExportUtils.generateMovementsCsv(movements.value)
    }
}
