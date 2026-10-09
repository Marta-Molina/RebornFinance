package com.example.rebornfinance.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.rebornfinance.data.local.entity.RebornProjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RebornProjectDao {
    @Query("SELECT * FROM reborn_projects ORDER BY updatedAt DESC, createdAt DESC")
    fun getAllProjects(): Flow<List<RebornProjectEntity>>

    @Query("SELECT * FROM reborn_projects WHERE id = :id")
    suspend fun getProjectById(id: Long): RebornProjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: RebornProjectEntity): Long

    @Update
    suspend fun updateProject(project: RebornProjectEntity)

    @Delete
    suspend fun deleteProject(project: RebornProjectEntity)
}
