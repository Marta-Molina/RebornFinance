package com.example.rebornfinance.core.money

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object MoneyUtils {
    private val spanishSymbols = DecimalFormatSymbols(Locale("es", "ES")).apply {
        groupingSeparator = '.'
        decimalSeparator = ','
    }

    private val currencyFormatter = DecimalFormat("#,##0.00", spanishSymbols)

    fun formatCents(cents: Long): String {
        val amount = cents / 100.0
        return "${currencyFormatter.format(amount)} €"
    }

    fun parseAmountToCents(amountStr: String): Long? {
        val cleaned = amountStr
            .replace("€", "")
            .replace(" ", "")
            .replace(".", "")
            .replace(",", ".")
            .trim()
        val parsedDouble = cleaned.toDoubleOrNull() ?: return null
        return Math.round(parsedDouble * 100)
    }
}
