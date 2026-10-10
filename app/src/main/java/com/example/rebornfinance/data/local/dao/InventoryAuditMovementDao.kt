package com.example.rebornfinance.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.rebornfinance.data.local.entity.InventoryAuditMovementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InventoryAuditMovementDao {
    @Query("SELECT * FROM inventory_audit_movements ORDER BY date DESC, createdAt DESC")
    fun getAllAuditMovements(): Flow<List<InventoryAuditMovementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuditMovement(entity: InventoryAuditMovementEntity): Long
}
