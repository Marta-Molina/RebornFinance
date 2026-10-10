package com.example.rebornfinance.domain.calculator

import com.example.rebornfinance.domain.model.Envelope
import com.example.rebornfinance.domain.model.Movement
import com.example.rebornfinance.domain.model.MovementType

object EnvelopeAndBudgetCalculator {

    fun calculateTotalReserved(envelopes: List<Envelope>): Long {
        return envelopes.filter { it.isActive }.sumOf { it.currentAmountCents }
    }

    fun calculateUnreservedMoney(currentBalanceCents: Long, envelopes: List<Envelope>): Long {
        val reserved = calculateTotalReserved(envelopes)
        return currentBalanceCents - reserved
    }

    fun calculateSpentForCategoryAndPeriod(
        movements: List<Movement>,
        category: String,
        startDate: Long,
        endDate: Long
    ): Long {
        return movements
            .filter { mov ->
                mov.isCounted &&
                        mov.type == MovementType.EXPENSE &&
                        mov.category.equals(category, ignoreCase = true) &&
                        mov.date in startDate..endDate
            }
            .sumOf { it.amountCents }
    }
}
