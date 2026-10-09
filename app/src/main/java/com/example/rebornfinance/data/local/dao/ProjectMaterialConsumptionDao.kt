package com.example.rebornfinance.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.rebornfinance.data.local.entity.ProjectMaterialConsumptionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectMaterialConsumptionDao {
    @Query("SELECT * FROM project_material_consumptions WHERE projectId = :projectId")
    fun getConsumptionsForProject(projectId: Long): Flow<List<ProjectMaterialConsumptionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConsumption(consumption: ProjectMaterialConsumptionEntity): Long

    @Update
    suspend fun updateConsumption(consumption: ProjectMaterialConsumptionEntity)

    @Delete
    suspend fun deleteConsumption(consumption: ProjectMaterialConsumptionEntity)
}
