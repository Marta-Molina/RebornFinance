package com.example.rebornfinance.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.rebornfinance.data.local.entity.MaterialConsumptionRuleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MaterialConsumptionRuleDao {
    @Query("SELECT * FROM material_consumption_rules WHERE category = :category AND isActive = 1")
    fun getActiveRulesForCategory(category: String): Flow<List<MaterialConsumptionRuleEntity>>

    @Query("SELECT * FROM material_consumption_rules WHERE category = :category AND :inches >= minInches AND :inches <= maxInches AND isActive = 1 LIMIT 1")
    suspend fun getRuleForCategoryAndInches(category: String, inches: Double): MaterialConsumptionRuleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRule(rule: MaterialConsumptionRuleEntity): Long

    @Update
    suspend fun updateRule(rule: MaterialConsumptionRuleEntity)

    @Delete
    suspend fun deleteRule(rule: MaterialConsumptionRuleEntity)
}
