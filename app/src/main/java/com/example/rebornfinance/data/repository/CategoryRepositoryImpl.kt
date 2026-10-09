package com.example.rebornfinance.data.repository

import com.example.rebornfinance.data.local.dao.CategoryDao
import com.example.rebornfinance.data.local.entity.CategoryEntity
import com.example.rebornfinance.domain.model.Category
import com.example.rebornfinance.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CategoryRepositoryImpl(
    private val categoryDao: CategoryDao
) : CategoryRepository {

    override fun getAllCategories(): Flow<List<Category>> {
        return categoryDao.getAllCategories().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun insertCategory(category: Category): Long {
        return categoryDao.insertCategory(CategoryEntity.fromDomain(category))
    }

    override suspend fun updateCategory(category: Category) {
        categoryDao.updateCategory(CategoryEntity.fromDomain(category))
    }
}
