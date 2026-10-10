package com.example.rebornfinance.domain.model

data class ParsedOperationProposal(
    val operationType: String, // "EXPENSE", "INCOME", "REFUND", "ENVELOPE_ADD", "ENVELOPE_WITHDRAW"
    val amountCents: Long? = null,
    val currency: String = "EUR",
    val description: String,
    val category: String,
    val date: Long,
    val projectName: String? = null,
    val materialName: String? = null,
    val quantity: Double? = null,
    val unit: String? = null,
    val envelopeName: String? = null,
    val confidence: Double = 1.0,
    val pendingFields: List<String> = emptyList(),
    val explanation: String? = null
)
