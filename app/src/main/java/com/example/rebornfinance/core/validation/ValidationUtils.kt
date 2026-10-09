package com.example.rebornfinance.core.validation

object ValidationUtils {
    fun isValidAmount(amountCents: Long?): Boolean {
        return amountCents != null && amountCents != 0L
    }

    fun isValidDescription(description: String): Boolean {
        return description.isNotBlank()
    }
}
