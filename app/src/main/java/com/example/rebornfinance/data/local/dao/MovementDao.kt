package com.example.rebornfinance.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.rebornfinance.data.local.entity.MovementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MovementDao {
    @Query("SELECT * FROM movements ORDER BY date DESC, createdAt DESC")
    fun getAllMovements(): Flow<List<MovementEntity>>

    @Query("SELECT * FROM movements WHERE id = :id")
    suspend fun getMovementById(id: Long): MovementEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovement(movement: MovementEntity): Long

    @Update
    suspend fun updateMovement(movement: MovementEntity)

    @Delete
    suspend fun deleteMovement(movement: MovementEntity)
}
