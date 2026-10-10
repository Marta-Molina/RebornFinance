package com.example.rebornfinance.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.rebornfinance.data.local.entity.EyeMaterialEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EyeMaterialDao {
    @Query("SELECT * FROM eye_materials ORDER BY name ASC")
    fun getAllEyeMaterials(): Flow<List<EyeMaterialEntity>>

    @Query("SELECT * FROM eye_materials WHERE id = :id")
    suspend fun getEyeMaterialById(id: Long): EyeMaterialEntity?

    @Query("SELECT * FROM eye_materials WHERE isActive = 1")
    suspend fun getActiveEyeMaterials(): List<EyeMaterialEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEyeMaterial(entity: EyeMaterialEntity): Long

    @Update
    suspend fun updateEyeMaterial(entity: EyeMaterialEntity)

    @Delete
    suspend fun deleteEyeMaterial(entity: EyeMaterialEntity)
}
