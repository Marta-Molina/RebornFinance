package com.example.rebornfinance.domain.repository

import com.example.rebornfinance.domain.model.Envelope
import com.example.rebornfinance.domain.model.EnvelopeOperation
import kotlinx.coroutines.flow.Flow

interface EnvelopeRepository {
    fun getAllEnvelopes(): Flow<List<Envelope>>
    suspend fun getEnvelopeById(id: Long): Envelope?
    suspend fun insertEnvelope(envelope: Envelope): Long
    suspend fun updateEnvelope(envelope: Envelope)
    suspend fun deleteEnvelope(envelope: Envelope)

    fun getOperationsForEnvelope(envelopeId: Long): Flow<List<EnvelopeOperation>>
    suspend fun addMoneyToEnvelope(envelopeId: Long, amountCents: Long, notes: String?): Result<Unit>
    suspend fun withdrawMoneyFromEnvelope(envelopeId: Long, amountCents: Long, notes: String?): Result<Unit>
    suspend fun transferMoneyBetweenEnvelopes(fromId: Long, toId: Long, amountCents: Long, notes: String?): Result<Unit>
}
