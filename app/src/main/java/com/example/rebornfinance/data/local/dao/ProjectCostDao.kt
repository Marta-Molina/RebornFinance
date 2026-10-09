package com.example.rebornfinance.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.rebornfinance.data.local.entity.ProjectCostEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectCostDao {
    @Query("SELECT * FROM project_costs WHERE projectId = :projectId ORDER BY date DESC, createdAt DESC")
    fun getCostsForProject(projectId: Long): Flow<List<ProjectCostEntity>>

    @Query("SELECT * FROM project_costs WHERE id = :id")
    suspend fun getCostById(id: Long): ProjectCostEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCost(cost: ProjectCostEntity): Long

    @Update
    suspend fun updateCost(cost: ProjectCostEntity)

    @Delete
    suspend fun deleteCost(cost: ProjectCostEntity)
}
