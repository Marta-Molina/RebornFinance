package com.example.rebornfinance.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.rebornfinance.data.local.entity.MaterialPurchaseLotEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MaterialPurchaseLotDao {
    @Query("SELECT * FROM material_purchase_lots WHERE materialId = :materialId ORDER BY purchaseDate ASC, createdAt ASC")
    fun getLotsForMaterial(materialId: Long): Flow<List<MaterialPurchaseLotEntity>>

    @Query("SELECT * FROM material_purchase_lots WHERE materialId = :materialId AND remainingQuantity > 0 ORDER BY purchaseDate ASC, createdAt ASC")
    suspend fun getAvailableLotsForMaterial(materialId: Long): List<MaterialPurchaseLotEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLot(lot: MaterialPurchaseLotEntity): Long

    @Update
    suspend fun updateLot(lot: MaterialPurchaseLotEntity)
}
