package com.example.rebornfinance.core.date

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("es", "ES"))

    fun formatDate(timestamp: Long): String {
        return dateFormat.format(Date(timestamp))
    }

    fun parseDate(dateStr: String): Long? {
        return try {
            dateFormat.parse(dateStr)?.time
        } catch (e: Exception) {
            null
        }
    }

    fun getCurrentTimestamp(): Long {
        return System.currentTimeMillis()
    }
}
