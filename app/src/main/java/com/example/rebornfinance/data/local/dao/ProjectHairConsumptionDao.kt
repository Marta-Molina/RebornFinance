package com.example.rebornfinance.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.rebornfinance.data.local.entity.ProjectHairConsumptionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectHairConsumptionDao {
    @Query("SELECT * FROM project_hair_consumptions WHERE projectId = :projectId")
    fun getConsumptionsForProject(projectId: Long): Flow<List<ProjectHairConsumptionEntity>>

    @Query("SELECT * FROM project_hair_consumptions WHERE id = :id")
    suspend fun getConsumptionById(id: Long): ProjectHairConsumptionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConsumption(consumption: ProjectHairConsumptionEntity): Long

    @Update
    suspend fun updateConsumption(consumption: ProjectHairConsumptionEntity)

    @Delete
    suspend fun deleteConsumption(consumption: ProjectHairConsumptionEntity)
}
