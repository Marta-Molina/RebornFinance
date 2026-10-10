package com.example.rebornfinance.domain.calculator

import com.example.rebornfinance.domain.model.Movement
import com.example.rebornfinance.domain.model.MovementType
import org.junit.Assert.assertEquals
import org.junit.Test

class StatisticsCalculatorTest {

    @Test
    fun testCalculateStatistics() {
        val movements = listOf(
            Movement(id = 1, type = MovementType.INCOME, amountCents = 45000L, description = "Venta", category = "Venta de reborns", date = 1000L, createdAt = 0L, updatedAt = 0L),
            Movement(id = 2, type = MovementType.EXPENSE, amountCents = 5000L, description = "Materiales", category = "Materiales", date = 1000L, createdAt = 0L, updatedAt = 0L),
            Movement(id = 3, type = MovementType.REFUND, amountCents = 1000L, description = "Devolución", category = "Reembolso", date = 1000L, createdAt = 0L, updatedAt = 0L),
            Movement(id = 4, type = MovementType.EXPENSE, amountCents = 2000L, description = "Comida", category = "Gastos personales", date = 5000L, createdAt = 0L, updatedAt = 0L) // outside range
        )

        val stats = StatisticsCalculator.calculateStatistics(movements, 0L, 2000L)

        assertEquals(46000L, stats.totalIncomeCents) // 45000 + 1000
        assertEquals(5000L, stats.totalExpenseCents) // 5000
        assertEquals(41000L, stats.netBalanceCents) // 46000 - 5000 = 41000
        assertEquals(3, stats.movementCount)
    }
}
