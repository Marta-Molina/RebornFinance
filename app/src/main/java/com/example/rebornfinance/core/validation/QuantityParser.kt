package com.example.rebornfinance.core.validation

object QuantityParser {
    fun parseThousandths(input: String): Result<Long> {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) {
            return Result.failure(IllegalArgumentException("La cantidad no puede estar vacía."))
        }

        var separatorCount = 0
        var decimalDigitsCount = 0
        var hasDecimalPart = false
        var integerPart = 0L
        var fractionalPart = 0L

        for (i in trimmed.indices) {
            val c = trimmed[i]
            when {
                c == '.' || c == ',' -> {
                    separatorCount++
                    if (separatorCount > 1) {
                        return Result.failure(IllegalArgumentException("Formato de número inválido (múltiples separadores)."))
                    }
                    hasDecimalPart = true
                }
                c in '0'..'9' -> {
                    val digit = (c - '0').toLong()
                    if (!hasDecimalPart) {
                        if (integerPart > (Long.MAX_VALUE - digit) / 10) {
                            return Result.failure(IllegalArgumentException("Desbordamiento en la parte entera."))
                        }
                        integerPart = integerPart * 10 + digit
                    } else {
                        decimalDigitsCount++
                        if (decimalDigitsCount > 3) {
                            return Result.failure(IllegalArgumentException("Se admiten como máximo 3 decimales (milésimas)."))
                        }
                        if (fractionalPart > (Long.MAX_VALUE - digit) / 10) {
                            return Result.failure(IllegalArgumentException("Desbordamiento en la parte decimal."))
                        }
                        fractionalPart = fractionalPart * 10 + digit
                    }
                }
                else -> {
                    return Result.failure(IllegalArgumentException("Caracteres inválidos en la cantidad."))
                }
            }
        }

        if (separatorCount == 1 && decimalDigitsCount == 0 && (trimmed.last() == '.' || trimmed.last() == ',')) {
            return Result.failure(IllegalArgumentException("Formato decimal incompleto."))
        }

        val normalizedFractional = when (decimalDigitsCount) {
            0 -> 0L
            1 -> fractionalPart * 100L
            2 -> fractionalPart * 10L
            3 -> fractionalPart
            else -> return Result.failure(IllegalArgumentException("Máximo 3 decimales permitidos."))
        }

        if (integerPart > Long.MAX_VALUE / 1000L) {
            return Result.failure(IllegalArgumentException("Desbordamiento numérico al convertir a milésimas."))
        }
        val intScaled = integerPart * 1000L
        if (intScaled > Long.MAX_VALUE - normalizedFractional) {
            return Result.failure(IllegalArgumentException("Desbordamiento numérico al sumar la parte fraccionaria."))
        }

        val totalThousandths = intScaled + normalizedFractional
        if (totalThousandths <= 0L) {
            return Result.failure(IllegalArgumentException("La cantidad debe ser mayor que 0."))
        }

        return Result.success(totalThousandths)
    }

    fun parseInteger(input: String): Result<Long> {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) {
            return Result.failure(IllegalArgumentException("La cantidad no puede estar vacía."))
        }

        var value = 0L
        for (i in trimmed.indices) {
            val c = trimmed[i]
            if (c !in '0'..'9') {
                return Result.failure(IllegalArgumentException("Debe ser un número entero válido."))
            }
            val digit = (c - '0').toLong()
            if (value > (Long.MAX_VALUE - digit) / 10) {
                return Result.failure(IllegalArgumentException("Desbordamiento numérico en entero."))
            }
            value = value * 10 + digit
        }

        if (value <= 0L) {
            return Result.failure(IllegalArgumentException("La cantidad debe ser mayor que 0."))
        }

        return Result.success(value)
    }

    fun formatThousandths(value: Long): String {
        val isNegative = value < 0L
        val abs = if (isNegative) -value else value
        val integerPart = abs / 1000L
        val fracPart = abs % 1000L
        val sign = if (isNegative) "-" else ""

        if (fracPart == 0L) {
            return "$sign$integerPart"
        }

        val paddedFrac = when {
            fracPart < 10L -> "00$fracPart"
            fracPart < 100L -> "0$fracPart"
            else -> fracPart.toString()
        }.trimEnd('0')

        return "$sign$integerPart,$paddedFrac"
    }

    fun formatQuantity(quantity: Long, unit: String): String {
        return "${formatThousandths(quantity)} $unit"
    }
}
