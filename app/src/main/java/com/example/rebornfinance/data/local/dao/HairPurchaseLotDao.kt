package com.example.rebornfinance.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.rebornfinance.data.local.entity.HairPurchaseLotEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HairPurchaseLotDao {
    @Query("SELECT * FROM hair_purchase_lots WHERE hairMaterialId = :materialId ORDER BY purchaseDate ASC, createdAt ASC")
    fun getLotsForHairMaterial(materialId: Long): Flow<List<HairPurchaseLotEntity>>

    @Query("SELECT * FROM hair_purchase_lots WHERE hairMaterialId = :materialId")
    suspend fun getAllLotsForHairMaterial(materialId: Long): List<HairPurchaseLotEntity>

    @Query("SELECT * FROM hair_purchase_lots WHERE hairMaterialId = :materialId AND remainingQuantityGrams > 0 ORDER BY purchaseDate ASC, createdAt ASC")
    suspend fun getAvailableLotsForHairMaterial(materialId: Long): List<HairPurchaseLotEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLot(lot: HairPurchaseLotEntity): Long

    @Update
    suspend fun updateLot(lot: HairPurchaseLotEntity)
}
