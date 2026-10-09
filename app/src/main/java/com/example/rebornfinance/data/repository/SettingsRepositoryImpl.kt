package com.example.rebornfinance.data.repository

import com.example.rebornfinance.data.preferences.UserPreferencesDataSource
import com.example.rebornfinance.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

class SettingsRepositoryImpl(
    private val preferencesDataSource: UserPreferencesDataSource
) : SettingsRepository {

    override val initialBalanceCents: Flow<Long>
        get() = preferencesDataSource.initialBalanceCents

    override suspend fun saveInitialBalance(amountCents: Long) {
        preferencesDataSource.saveInitialBalance(amountCents)
    }
}
