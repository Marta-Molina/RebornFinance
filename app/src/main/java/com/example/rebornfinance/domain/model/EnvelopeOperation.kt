package com.example.rebornfinance.domain.model

data class EnvelopeOperation(
    val id: Long = 0L,
    val envelopeId: Long,
    val operationType: String, // "ADD", "WITHDRAW", "TRANSFER"
    val targetEnvelopeId: Long? = null,
    val amountCents: Long,
    val date: Long,
    val notes: String? = null,
    val createdAt: Long
)
