package com.example.rebornfinance.domain.calculator

import com.example.rebornfinance.domain.model.Movement
import com.example.rebornfinance.domain.model.MovementType
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Calendar

class FinancialCalculatorTest {

    @Test
    fun testCalculateCurrentBalance() {
        val initialBalance = 10000L // 100.00 €
        val calendar = Calendar.getInstance()
        val now = calendar.timeInMillis

        val movements = listOf(
            Movement(id = 1, type = MovementType.INCOME, amountCents = 5000L, description = "Venta", category = "Venta", date = now, createdAt = now, updatedAt = now),
            Movement(id = 2, type = MovementType.EXPENSE, amountCents = 2000L, description = "Silicona", category = "Materiales", date = now, createdAt = now, updatedAt = now),
            Movement(id = 3, type = MovementType.REFUND, amountCents = 500L, description = "Devolución", category = "Reembolso", date = now, createdAt = now, updatedAt = now)
        )

        // 10000 + 5000 - 2000 + 500 = 13500 (135.00 €)
        val balance = FinancialCalculator.calculateCurrentBalance(initialBalance, movements)
        assertEquals(13500L, balance)
    }

    @Test
    fun testCalculateMonthlyTotals() {
        val calendar = Calendar.getInstance().apply {
            set(2025, Calendar.MAY, 10, 12, 0)
        }
        val mayTime = calendar.timeInMillis

        val movements = listOf(
            Movement(id = 1, type = MovementType.INCOME, amountCents = 45000L, description = "Reborn", category = "Venta", date = mayTime, createdAt = mayTime, updatedAt = mayTime),
            Movement(id = 2, type = MovementType.EXPENSE, amountCents = 3500L, description = "Pintura", category = "Materiales", date = mayTime, createdAt = mayTime, updatedAt = mayTime),
            Movement(id = 3, type = MovementType.REFUND, amountCents = 1500L, description = "Envío", category = "Reembolso", date = mayTime, createdAt = mayTime, updatedAt = mayTime)
        )

        val income = FinancialCalculator.calculateMonthlyIncome(movements, 2025, Calendar.MAY)
        val expense = FinancialCalculator.calculateMonthlyExpense(movements, 2025, Calendar.MAY)
        val refund = FinancialCalculator.calculateMonthlyRefund(movements, 2025, Calendar.MAY)
        val net = FinancialCalculator.calculateMonthlyNet(income, expense, refund)

        assertEquals(45000L, income)
        assertEquals(3500L, expense)
        assertEquals(1500L, refund)
        assertEquals(43000L, net) // 45000 + 1500 - 3500 = 43000
    }
}
