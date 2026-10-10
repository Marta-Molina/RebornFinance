package com.example.rebornfinance.data.repository

import androidx.room.withTransaction
import com.example.rebornfinance.core.date.DateUtils
import com.example.rebornfinance.data.local.AppDatabase
import com.example.rebornfinance.data.local.dao.EnvelopeDao
import com.example.rebornfinance.data.local.dao.EnvelopeOperationDao
import com.example.rebornfinance.data.local.entity.EnvelopeEntity
import com.example.rebornfinance.data.local.entity.EnvelopeOperationEntity
import com.example.rebornfinance.domain.model.Envelope
import com.example.rebornfinance.domain.model.EnvelopeOperation
import com.example.rebornfinance.domain.repository.EnvelopeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class EnvelopeRepositoryImpl(
    private val database: AppDatabase,
    private val envelopeDao: EnvelopeDao,
    private val operationDao: EnvelopeOperationDao
) : EnvelopeRepository {

    override fun getAllEnvelopes(): Flow<List<Envelope>> {
        return envelopeDao.getAllEnvelopes().map { list -> list.map { it.toDomain() } }
    }

    override suspend fun getEnvelopeById(id: Long): Envelope? {
        return envelopeDao.getEnvelopeById(id)?.toDomain()
    }

    override suspend fun insertEnvelope(envelope: Envelope): Long {
        return envelopeDao.insertEnvelope(EnvelopeEntity.fromDomain(envelope))
    }

    override suspend fun updateEnvelope(envelope: Envelope) {
        envelopeDao.updateEnvelope(EnvelopeEntity.fromDomain(envelope))
    }

    override suspend fun deleteEnvelope(envelope: Envelope) {
        envelopeDao.deleteEnvelope(EnvelopeEntity.fromDomain(envelope))
    }

    override fun getOperationsForEnvelope(envelopeId: Long): Flow<List<EnvelopeOperation>> {
        return operationDao.getOperationsForEnvelope(envelopeId).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun addMoneyToEnvelope(envelopeId: Long, amountCents: Long, notes: String?): Result<Unit> {
        if (amountCents <= 0L) return Result.failure(Exception("Importe inválido."))
        val envelope = envelopeDao.getEnvelopeById(envelopeId) ?: return Result.failure(Exception("Sobre no encontrado."))

        database.withTransaction {
            val now = DateUtils.getCurrentTimestamp()
            val newAmount = envelope.currentAmountCents + amountCents
            envelopeDao.updateEnvelope(envelope.copy(currentAmountCents = newAmount, updatedAt = now))

            val op = EnvelopeOperationEntity(
                envelopeId = envelopeId,
                operationType = "ADD",
                amountCents = amountCents,
                date = now,
                notes = notes,
                createdAt = now
            )
            operationDao.insertOperation(op)
        }
        return Result.success(Unit)
    }

    override suspend fun withdrawMoneyFromEnvelope(envelopeId: Long, amountCents: Long, notes: String?): Result<Unit> {
        if (amountCents <= 0L) return Result.failure(Exception("Importe inválido."))
        val envelope = envelopeDao.getEnvelopeById(envelopeId) ?: return Result.failure(Exception("Sobre no encontrado."))

        if (envelope.currentAmountCents < amountCents) {
            return Result.failure(Exception("Saldo insuficiente en el sobre '${envelope.name}'."))
        }

        database.withTransaction {
            val now = DateUtils.getCurrentTimestamp()
            val newAmount = envelope.currentAmountCents - amountCents
            envelopeDao.updateEnvelope(envelope.copy(currentAmountCents = newAmount, updatedAt = now))

            val op = EnvelopeOperationEntity(
                envelopeId = envelopeId,
                operationType = "WITHDRAW",
                amountCents = amountCents,
                date = now,
                notes = notes,
                createdAt = now
            )
            operationDao.insertOperation(op)
        }
        return Result.success(Unit)
    }

    override suspend fun transferMoneyBetweenEnvelopes(
        fromId: Long,
        toId: Long,
        amountCents: Long,
        notes: String?
    ): Result<Unit> {
        if (amountCents <= 0L) return Result.failure(Exception("Importe inválido."))
        if (fromId == toId) return Result.failure(Exception("No se puede transferir al mismo sobre."))

        val fromEnv = envelopeDao.getEnvelopeById(fromId) ?: return Result.failure(Exception("Sobre origen no encontrado."))
        val toEnv = envelopeDao.getEnvelopeById(toId) ?: return Result.failure(Exception("Sobre destino no encontrado."))

        if (fromEnv.currentAmountCents < amountCents) {
            return Result.failure(Exception("Saldo insuficiente en el sobre '${fromEnv.name}'."))
        }

        database.withTransaction {
            val now = DateUtils.getCurrentTimestamp()
            envelopeDao.updateEnvelope(fromEnv.copy(currentAmountCents = fromEnv.currentAmountCents - amountCents, updatedAt = now))
            envelopeDao.updateEnvelope(toEnv.copy(currentAmountCents = toEnv.currentAmountCents + amountCents, updatedAt = now))

            val opFrom = EnvelopeOperationEntity(
                envelopeId = fromId,
                operationType = "TRANSFER",
                targetEnvelopeId = toId,
                amountCents = amountCents,
                date = now,
                notes = notes,
                createdAt = now
            )
            operationDao.insertOperation(opFrom)
        }
        return Result.success(Unit)
    }
}
