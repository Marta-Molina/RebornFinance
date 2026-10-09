package com.example.rebornfinance.domain.calculator

import com.example.rebornfinance.domain.model.Movement
import com.example.rebornfinance.domain.model.MovementType
import java.util.Calendar

object FinancialCalculator {

    fun calculateCurrentBalance(initialBalanceCents: Long, movements: List<Movement>): Long {
        var balance = initialBalanceCents
        for (movement in movements) {
            if (!movement.isCounted) continue
            when (movement.type) {
                MovementType.INCOME -> balance += movement.amountCents
                MovementType.EXPENSE -> balance -= movement.amountCents
                MovementType.REFUND -> balance += movement.amountCents
                MovementType.BALANCE_ADJUSTMENT -> balance += movement.amountCents
            }
        }
        return balance
    }

    fun calculateMonthlyIncome(movements: List<Movement>, year: Int, month: Int): Long {
        return movements
            .filter { it.isCounted && it.type == MovementType.INCOME && isMovementInMonth(it.date, year, month) }
            .sumOf { it.amountCents }
    }

    fun calculateMonthlyExpense(movements: List<Movement>, year: Int, month: Int): Long {
        return movements
            .filter { it.isCounted && it.type == MovementType.EXPENSE && isMovementInMonth(it.date, year, month) }
            .sumOf { it.amountCents }
    }

    fun calculateMonthlyRefund(movements: List<Movement>, year: Int, month: Int): Long {
        return movements
            .filter { it.isCounted && it.type == MovementType.REFUND && isMovementInMonth(it.date, year, month) }
            .sumOf { it.amountCents }
    }

    fun calculateMonthlyNet(income: Long, expense: Long, refund: Long): Long {
        return income + refund - expense
    }

    private fun isMovementInMonth(timestamp: Long, year: Int, month: Int): Boolean {
        val calendar = Calendar.getInstance().apply { timeInMillis = timestamp }
        return calendar.get(Calendar.YEAR) == year && calendar.get(Calendar.MONTH) == month
    }
}
