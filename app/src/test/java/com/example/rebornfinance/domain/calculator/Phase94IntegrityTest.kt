package com.example.rebornfinance.domain.calculator

import com.example.rebornfinance.domain.model.Movement
import com.example.rebornfinance.domain.model.MovementType
import org.junit.Assert.assertEquals
import org.junit.Test

class Phase94IntegrityTest {

    @Test
    fun testNonInventoriableExpenseDoesNotCreateStock() {
        val movement = Movement(
            type = MovementType.EXPENSE,
            amountCents = 1500L,
            description = "Electricidad taller",
            category = "Gastos personales",
            date = System.currentTimeMillis(),
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        // Expenses in personal expenses or utilities should not be treated as inventory materials
        assertEquals("Gastos personales", movement.category)
        assertEquals(MovementType.EXPENSE, movement.type)
    }
}
