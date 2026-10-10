package com.example.rebornfinance.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.rebornfinance.data.local.entity.EnvelopeOperationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EnvelopeOperationDao {
    @Query("SELECT * FROM envelope_operations WHERE envelopeId = :envelopeId ORDER BY date DESC, createdAt DESC")
    fun getOperationsForEnvelope(envelopeId: Long): Flow<List<EnvelopeOperationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOperation(entity: EnvelopeOperationEntity): Long
}
