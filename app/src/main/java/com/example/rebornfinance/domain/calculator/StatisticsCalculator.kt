package com.example.rebornfinance.domain.calculator

import com.example.rebornfinance.domain.model.Movement
import com.example.rebornfinance.domain.model.MovementType
import java.util.Calendar

data class FinancialStatistics(
    val totalIncomeCents: Long,
    val totalExpenseCents: Long,
    val netBalanceCents: Long,
    val expenseByCategory: Map<String, Long>,
    val incomeByCategory: Map<String, Long>,
    val movementCount: Int
)

object StatisticsCalculator {

    fun calculateStatistics(movements: List<Movement>, startTimeMillis: Long, endTimeMillis: Long): FinancialStatistics {
        val filtered = movements.filter { mov ->
            mov.isCounted && mov.date in startTimeMillis..endTimeMillis
        }

        var totalIncome = 0L
        var totalExpense = 0L
        val expenseMap = mutableMapOf<String, Long>()
        val incomeMap = mutableMapOf<String, Long>()

        for (mov in filtered) {
            when (mov.type) {
                MovementType.INCOME, MovementType.REFUND -> {
                    totalIncome += mov.amountCents
                    val current = incomeMap[mov.category] ?: 0L
                    incomeMap[mov.category] = current + mov.amountCents
                }
                MovementType.EXPENSE -> {
                    totalExpense += mov.amountCents
                    val current = expenseMap[mov.category] ?: 0L
                    expenseMap[mov.category] = current + mov.amountCents
                }
                MovementType.BALANCE_ADJUSTMENT -> {
                    if (mov.amountCents >= 0) {
                        totalIncome += mov.amountCents
                    } else {
                        totalExpense += -mov.amountCents
                    }
                }
            }
        }

        val netBalance = totalIncome - totalExpense

        return FinancialStatistics(
            totalIncomeCents = totalIncome,
            totalExpenseCents = totalExpense,
            netBalanceCents = netBalance,
            expenseByCategory = expenseMap,
            incomeByCategory = incomeMap,
            movementCount = filtered.size
        )
    }

    fun getDateRangeForFilter(filterType: String): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        val endCalendar = Calendar.getInstance()

        when (filterType) {
            "THIS_MONTH" -> {
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)

                endCalendar.set(Calendar.DAY_OF_MONTH, endCalendar.getActualMaximum(Calendar.DAY_OF_MONTH))
                endCalendar.set(Calendar.HOUR_OF_DAY, 23)
                endCalendar.set(Calendar.MINUTE, 59)
                endCalendar.set(Calendar.SECOND, 59)
                endCalendar.set(Calendar.MILLISECOND, 999)
            }
            "LAST_MONTH" -> {
                calendar.add(Calendar.MONTH, -1)
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)

                endCalendar.add(Calendar.MONTH, -1)
                endCalendar.set(Calendar.DAY_OF_MONTH, endCalendar.getActualMaximum(Calendar.DAY_OF_MONTH))
                endCalendar.set(Calendar.HOUR_OF_DAY, 23)
                endCalendar.set(Calendar.MINUTE, 59)
                endCalendar.set(Calendar.SECOND, 59)
                endCalendar.set(Calendar.MILLISECOND, 999)
            }
            "THIS_YEAR" -> {
                calendar.set(Calendar.DAY_OF_YEAR, 1)
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)

                endCalendar.set(Calendar.MONTH, Calendar.DECEMBER)
                endCalendar.set(Calendar.DAY_OF_MONTH, 31)
                endCalendar.set(Calendar.HOUR_OF_DAY, 23)
                endCalendar.set(Calendar.MINUTE, 59)
                endCalendar.set(Calendar.SECOND, 59)
                endCalendar.set(Calendar.MILLISECOND, 999)
            }
            else -> {
                // All time / default (last 10 years to next 10 years)
                calendar.set(2020, Calendar.JANUARY, 1, 0, 0, 0)
                endCalendar.set(2035, Calendar.DECEMBER, 31, 23, 59, 59)
            }
        }
        return Pair(calendar.timeInMillis, endCalendar.timeInMillis)
    }
}
