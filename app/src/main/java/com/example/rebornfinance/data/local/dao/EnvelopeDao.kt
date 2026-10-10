package com.example.rebornfinance.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.rebornfinance.data.local.entity.EnvelopeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EnvelopeDao {
    @Query("SELECT * FROM envelopes ORDER BY name ASC")
    fun getAllEnvelopes(): Flow<List<EnvelopeEntity>>

    @Query("SELECT * FROM envelopes WHERE id = :id")
    suspend fun getEnvelopeById(id: Long): EnvelopeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEnvelope(entity: EnvelopeEntity): Long

    @Update
    suspend fun updateEnvelope(entity: EnvelopeEntity)

    @Delete
    suspend fun deleteEnvelope(entity: EnvelopeEntity)
}
