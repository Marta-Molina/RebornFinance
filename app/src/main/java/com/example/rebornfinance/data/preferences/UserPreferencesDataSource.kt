package com.example.rebornfinance.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesDataSource(private val context: Context) {
    private object PreferencesKeys {
        val INITIAL_BALANCE_CENTS = longPreferencesKey("initial_balance_cents")
    }

    val initialBalanceCents: Flow<Long> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.INITIAL_BALANCE_CENTS] ?: 0L
        }

    suspend fun saveInitialBalance(amountCents: Long) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.INITIAL_BALANCE_CENTS] = amountCents
        }
    }
}
