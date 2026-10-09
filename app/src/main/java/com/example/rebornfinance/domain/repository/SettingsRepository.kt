package com.example.rebornfinance.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val initialBalanceCents: Flow<Long>
    suspend fun saveInitialBalance(amountCents: Long)
}
