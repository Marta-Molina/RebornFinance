package com.example.rebornfinance.core.money

object MoneyUtils {

    fun formatCents(cents: Long): String {
        val isNegative = cents < 0L
        val absCents = if (isNegative) {
            if (cents == Long.MIN_VALUE) Long.MAX_VALUE else -cents
        } else {
            cents
        }
        val euros = absCents / 100L
        val remainingCents = absCents % 100L

        val euroStr = formatWithThousandsSeparator(euros)
        val centStr = if (remainingCents < 10L) "0$remainingCents" else remainingCents.toString()
        val sign = if (isNegative) "-" else ""

        return "$sign$euroStr,$centStr €"
    }

    private fun formatWithThousandsSeparator(value: Long): String {
        val s = value.toString()
        if (s.length <= 3) return s
        val sb = StringBuilder()
        var count = 0
        for (i in s.length - 1 downTo 0) {
            sb.append(s[i])
            count++
            if (count % 3 == 0 && i > 0) {
                sb.append('.')
            }
        }
        return sb.reverse().toString()
    }

    fun parseAmountToCents(amountStr: String): Long? {
        var cleaned = amountStr
            .replace("€", "")
            .replace("EUR", "", ignoreCase = true)
            .replace(" ", "")
            .replace("\u00A0", "")
            .trim()

        if (cleaned.isEmpty()) return null

        var isNegative = false
        if (cleaned.startsWith("-")) {
            isNegative = true
            cleaned = cleaned.substring(1).trim()
        } else if (cleaned.startsWith("+")) {
            cleaned = cleaned.substring(1).trim()
        }

        if (cleaned.isEmpty()) return null

        val hasComma = cleaned.contains(',')
        val hasDot = cleaned.contains('.')

        val integerStr: String
        val decimalStr: String

        if (hasComma && hasDot) {
            val lastComma = cleaned.lastIndexOf(',')
            val lastDot = cleaned.lastIndexOf('.')
            if (lastComma > lastDot) {
                // Dot is thousands separator, comma is decimal (Spanish format: 1.250,50)
                integerStr = cleaned.substring(0, lastComma).replace(".", "")
                decimalStr = cleaned.substring(lastComma + 1)
            } else {
                // Comma is thousands separator, dot is decimal (Anglo format: 1,250.50)
                integerStr = cleaned.substring(0, lastDot).replace(",", "")
                decimalStr = cleaned.substring(lastDot + 1)
            }
        } else if (hasComma) {
            val parts = cleaned.split(',')
            if (parts.size > 2) return null
            integerStr = parts[0]
            decimalStr = if (parts.size == 2) parts[1] else ""
        } else if (hasDot) {
            val parts = cleaned.split('.')
            if (parts.size == 2 && parts[1].length <= 2) {
                // Single dot with 1 or 2 decimals (e.g. 12.50 or 12.5)
                integerStr = parts[0]
                decimalStr = parts[1]
            } else if (parts.size == 2 && parts[0] == "0") {
                // e.g. 0.50
                integerStr = parts[0]
                decimalStr = parts[1]
            } else {
                // Dot as thousand separator with no decimals (e.g. 1.000 or 1.000.000)
                integerStr = cleaned.replace(".", "")
                decimalStr = ""
            }
        } else {
            integerStr = cleaned
            decimalStr = ""
        }

        val integerPart = if (integerStr.isEmpty()) {
            0L
        } else {
            for (c in integerStr) {
                if (c !in '0'..'9') return null
            }
            if (integerStr.length > 18) return null // Guard against Long overflow
            var acc = 0L
            for (c in integerStr) {
                val digit = (c - '0').toLong()
                if (acc > (Long.MAX_VALUE - digit) / 10L) return null
                acc = acc * 10L + digit
            }
            acc
        }

        val centsPart = when (decimalStr.length) {
            0 -> 0L
            1 -> {
                val c = decimalStr[0]
                if (c !in '0'..'9') return null
                (c - '0').toLong() * 10L
            }
            2 -> {
                val c1 = decimalStr[0]
                val c2 = decimalStr[1]
                if (c1 !in '0'..'9' || c2 !in '0'..'9') return null
                (c1 - '0').toLong() * 10L + (c2 - '0').toLong()
            }
            else -> return null // More than 2 decimal digits not allowed for currency
        }

        if (integerPart > (Long.MAX_VALUE - centsPart) / 100L) {
            return null // Long overflow
        }

        val totalCents = integerPart * 100L + centsPart
        return if (isNegative) -totalCents else totalCents
    }
}
