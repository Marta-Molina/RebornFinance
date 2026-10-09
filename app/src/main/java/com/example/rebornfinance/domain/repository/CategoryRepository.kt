package com.example.rebornfinance.domain.repository

import com.example.rebornfinance.domain.model.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun getAllCategories(): Flow<List<Category>>
    suspend fun insertCategory(category: Category): Long
    suspend fun updateCategory(category: Category)
}
