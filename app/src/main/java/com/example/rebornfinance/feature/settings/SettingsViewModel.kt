package com.example.rebornfinance.feature.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.rebornfinance.core.money.MoneyUtils
import com.example.rebornfinance.data.local.AppDatabase
import com.example.rebornfinance.data.preferences.UserPreferencesDataSource
import com.example.rebornfinance.data.repository.CategoryRepositoryImpl
import com.example.rebornfinance.data.repository.SettingsRepositoryImpl
import com.example.rebornfinance.domain.model.Category
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val settingsRepository = SettingsRepositoryImpl(UserPreferencesDataSource(application))
    private val categoryRepository = CategoryRepositoryImpl(database.categoryDao())

    val initialBalanceCents: StateFlow<Long> = settingsRepository.initialBalanceCents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val categories: StateFlow<List<Category>> = categoryRepository.getAllCategories()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateInitialBalance(amountStr: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val cents = MoneyUtils.parseAmountToCents(amountStr)
        if (cents == null) {
            onError("Importe inválido.")
            return
        }
        viewModelScope.launch {
            settingsRepository.saveInitialBalance(cents)
            onSuccess()
        }
    }
}
