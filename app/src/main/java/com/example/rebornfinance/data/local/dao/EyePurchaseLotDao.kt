package com.example.rebornfinance.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.rebornfinance.data.local.entity.EyePurchaseLotEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EyePurchaseLotDao {
    @Query("SELECT * FROM eye_purchase_lots WHERE eyeMaterialId = :materialId ORDER BY purchaseDate ASC, createdAt ASC")
    fun getLotsForEyeMaterial(materialId: Long): Flow<List<EyePurchaseLotEntity>>

    @Query("SELECT * FROM eye_purchase_lots WHERE eyeMaterialId = :materialId")
    suspend fun getAllLotsForEyeMaterial(materialId: Long): List<EyePurchaseLotEntity>

    @Query("SELECT * FROM eye_purchase_lots WHERE eyeMaterialId = :materialId AND remainingQuantity > 0 ORDER BY purchaseDate ASC, createdAt ASC")
    suspend fun getAvailableLotsForEyeMaterial(materialId: Long): List<EyePurchaseLotEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLot(lot: EyePurchaseLotEntity): Long

    @Update
    suspend fun updateLot(lot: EyePurchaseLotEntity)
}
