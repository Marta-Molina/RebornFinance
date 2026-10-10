package com.example.rebornfinance.core.util

import com.example.rebornfinance.core.date.DateUtils
import com.example.rebornfinance.domain.model.Movement

object CsvExportUtils {

    fun generateMovementsCsv(movements: List<Movement>): String {
        val sb = StringBuilder()
        sb.append("ID;Fecha;Tipo;Importe (€);Categoría;Descripción;Notas\n")

        for (mov in movements) {
            val dateStr = DateUtils.formatDate(mov.date)
            val amountEur = String.format(java.util.Locale("es", "ES"), "%.2f", mov.amountCents / 100.0)
            val desc = mov.description.replace(";", ",")
            val notes = (mov.notes ?: "").replace(";", ",")

            sb.append("${mov.id};$dateStr;${mov.type.displayName};$amountEur;${mov.category};$desc;$notes\n")
        }

        return sb.toString()
    }
}
