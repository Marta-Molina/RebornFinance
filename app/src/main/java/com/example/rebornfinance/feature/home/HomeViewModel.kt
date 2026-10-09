package com.example.rebornfinance.feature.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.rebornfinance.data.local.AppDatabase
import com.example.rebornfinance.data.preferences.UserPreferencesDataSource
import com.example.rebornfinance.data.repository.MovementRepositoryImpl
import com.example.rebornfinance.data.repository.SettingsRepositoryImpl
import com.example.rebornfinance.domain.calculator.FinancialCalculator
import com.example.rebornfinance.domain.model.Movement
import com.example.rebornfinance.domain.model.FinancialSummary
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val movementRepository = MovementRepositoryImpl(database.movementDao())
    private val settingsRepository = SettingsRepositoryImpl(UserPreferencesDataSource(application))

    val movements: StateFlow<List<Movement>> = movementRepository.getAllMovements()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val initialBalance: StateFlow<Long> = settingsRepository.initialBalanceCents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val financialSummary: StateFlow<FinancialSummary> = combine(
        initialBalance,
        movements
    ) { balance, movs ->
        val currentBalance = FinancialCalculator.calculateCurrentBalance(balance, movs)
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)

        val income = FinancialCalculator.calculateMonthlyIncome(movs, year, month)
        val expense = FinancialCalculator.calculateMonthlyExpense(movs, year, month)
        val refund = FinancialCalculator.calculateMonthlyRefund(movs, year, month)
        val net = FinancialCalculator.calculateMonthlyNet(income, expense, refund)

        FinancialSummary(
            currentBalanceCents = currentBalance,
            monthlyIncomeCents = income,
            monthlyExpenseCents = expense,
            monthlyRefundCents = refund,
            monthlyNetCents = net
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        FinancialSummary(0L, 0L, 0L, 0L, 0L)
    )
}
