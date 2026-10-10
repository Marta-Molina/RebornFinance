package com.example.rebornfinance.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.rebornfinance.data.local.entity.HairMaterialEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HairMaterialDao {
    @Query("SELECT * FROM hair_materials ORDER BY name ASC")
    fun getAllHairMaterials(): Flow<List<HairMaterialEntity>>

    @Query("SELECT * FROM hair_materials WHERE id = :id")
    suspend fun getHairMaterialById(id: Long): HairMaterialEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHairMaterial(entity: HairMaterialEntity): Long

    @Update
    suspend fun updateHairMaterial(entity: HairMaterialEntity)

    @Delete
    suspend fun deleteHairMaterial(entity: HairMaterialEntity)
}
