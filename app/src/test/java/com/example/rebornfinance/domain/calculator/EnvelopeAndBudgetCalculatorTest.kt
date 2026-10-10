package com.example.rebornfinance.domain.calculator

import com.example.rebornfinance.domain.model.Envelope
import com.example.rebornfinance.domain.model.Movement
import com.example.rebornfinance.domain.model.MovementType
import org.junit.Assert.assertEquals
import org.junit.Test

class EnvelopeAndBudgetCalculatorTest {

    @Test
    fun testCalculateUnreservedMoney() {
        val currentBalance = 50000L // 500.00 €
        val envelopes = listOf(
            Envelope(id = 1, name = "Materiales", currentAmountCents = 10000L, createdAt = 0L, updatedAt = 0L),
            Envelope(id = 2, name = "Ahorro", currentAmountCents = 15000L, createdAt = 0L, updatedAt = 0L)
        )

        val reserved = EnvelopeAndBudgetCalculator.calculateTotalReserved(envelopes)
        val unreserved = EnvelopeAndBudgetCalculator.calculateUnreservedMoney(currentBalance, envelopes)

        assertEquals(25000L, reserved)
        assertEquals(25000L, unreserved) // 50000 - 25000 = 25000
    }

    @Test
    fun testCalculateSpentForCategoryAndPeriod() {
        val now = 1000L
        val movements = listOf(
            Movement(id = 1, type = MovementType.EXPENSE, amountCents = 3500L, description = "Silicona", category = "Materiales", date = 100L, createdAt = 0L, updatedAt = 0L),
            Movement(id = 2, type = MovementType.EXPENSE, amountCents = 1200L, description = "Envío", category = "Envíos", date = 150L, createdAt = 0L, updatedAt = 0L),
            Movement(id = 3, type = MovementType.EXPENSE, amountCents = 1500L, description = "Pintura", category = "Materiales", date = 200L, createdAt = 0L, updatedAt = 0L)
        )

        val spent = EnvelopeAndBudgetCalculator.calculateSpentForCategoryAndPeriod(movements, "Materiales", 0L, 500L)
        assertEquals(5000L, spent) // 3500 + 1500 = 5000 (50.00 €)
    }
}
