package com.example.rebornfinance.domain.parser

import com.example.rebornfinance.core.date.DateUtils
import com.example.rebornfinance.core.money.MoneyUtils
import com.example.rebornfinance.domain.model.ParsedOperationProposal
import java.util.regex.Pattern

object LocalRuleBasedParser {

    fun parse(text: String): ParsedOperationProposal {
        val lower = text.lowercase().trim()
        val now = DateUtils.getCurrentTimestamp()

        val amountCents = extractAmountCents(lower)

        // 1. Eyes check
        if (lower.contains("ojos")) {
            val desc = extractDescriptionAfter(lower, "en") ?: "Ojos para reborn"
            return ParsedOperationProposal(
                operationType = "EXPENSE",
                amountCents = amountCents,
                description = desc.replaceFirstChar { it.uppercase() },
                category = "Ojos",
                date = now,
                materialName = "Ojos",
                explanation = "Reconocido como compra/gasto de ojos (Analizador local)."
            )
        }

        // 2. Hair check
        if (lower.contains("pelo") || lower.contains("mohair") || lower.contains("gramos")) {
            val desc = extractDescriptionAfter(lower, "en") ?: "Pelo para reborn"
            return ParsedOperationProposal(
                operationType = "EXPENSE",
                amountCents = amountCents,
                description = desc.replaceFirstChar { it.uppercase() },
                category = "Pelo",
                date = now,
                materialName = "Pelo",
                explanation = "Reconocido como compra/gasto de pelo (Analizador local)."
            )
        }

        // 3. Needles / Agujas check
        if (lower.contains("agujas") || lower.contains("aguja")) {
            return ParsedOperationProposal(
                operationType = "EXPENSE",
                amountCents = amountCents,
                description = "Agujas de rooting",
                category = "Herramientas",
                date = now,
                materialName = "Agujas",
                explanation = "Reconocido como compra de agujas (Analizador local)."
            )
        }

        // 4. Priming / Imprimación
        if (lower.contains("imprimación")) {
            return ParsedOperationProposal(
                operationType = "EXPENSE",
                amountCents = amountCents,
                description = "Imprimación",
                category = "Materiales",
                date = now,
                materialName = "Imprimación",
                explanation = "Reconocido como material de imprimación (Analizador local)."
            )
        }

        // 5. Paint / Pinturas
        if (lower.contains("pintura") || lower.contains("pinturas")) {
            val projectName = extractProjectName(lower)
            return ParsedOperationProposal(
                operationType = "EXPENSE",
                amountCents = amountCents,
                description = "Pinturas y barnices",
                category = "Pinturas y barnices",
                date = now,
                projectName = projectName,
                materialName = "Pintura",
                explanation = "Reconocido como compra de pinturas (Analizador local)."
            )
        }

        // 6. Varnish / Barniz
        if (lower.contains("barniz")) {
            return ParsedOperationProposal(
                operationType = "EXPENSE",
                amountCents = amountCents,
                description = "Barniz",
                category = "Pinturas y barnices",
                date = now,
                materialName = "Barniz",
                explanation = "Reconocido como compra de barniz (Analizador local)."
            )
        }

        // 7. General Expense check
        if (lower.contains("gastado") || lower.contains("gasto") || lower.contains("comprado")) {
            val desc = extractDescriptionAfter(lower, "en") ?: "Gasto general"
            val projectName = extractProjectName(lower)
            val category = if (projectName != null) "Materiales" else "Otros"

            return ParsedOperationProposal(
                operationType = "EXPENSE",
                amountCents = amountCents,
                description = desc.replaceFirstChar { it.uppercase() },
                category = category,
                date = now,
                projectName = projectName,
                explanation = "Analizado mediante reglas locales (Analizador determinista)."
            )
        }

        // 8. Sale / Income check
        if (lower.contains("vendí") || lower.contains("vendido")) {
            val projectName = extractProjectName(lower) ?: "Reborn"
            return ParsedOperationProposal(
                operationType = "INCOME",
                amountCents = amountCents,
                description = "Venta reborn $projectName",
                category = "Venta de reborns",
                date = now,
                projectName = projectName,
                explanation = "Analizado mediante reglas locales (Analizador determinista)."
            )
        }

        // 9. Refund check
        if (lower.contains("devuelto") || lower.contains("reembolsado")) {
            return ParsedOperationProposal(
                operationType = "REFUND",
                amountCents = amountCents,
                description = "Reembolso de pedido",
                category = "Reembolso de compras",
                date = now,
                explanation = "Analizado mediante reglas locales (Analizador determinista)."
            )
        }

        // 10. Envelope add
        if (lower.contains("reservado") || lower.contains("guardado")) {
            val envName = extractDescriptionAfter(lower, "para") ?: "Ahorro"
            return ParsedOperationProposal(
                operationType = "ENVELOPE_ADD",
                amountCents = amountCents,
                description = "Reserva en sobre",
                category = "Ahorro",
                date = now,
                envelopeName = envName.replaceFirstChar { it.uppercase() },
                explanation = "Analizado mediante reglas locales (Analizador determinista)."
            )
        }

        // Default fallback
        return ParsedOperationProposal(
            operationType = "EXPENSE",
            amountCents = amountCents,
            description = text.replaceFirstChar { it.uppercase() },
            category = "Otros",
            date = now,
            confidence = 0.5,
            pendingFields = listOf("categoría", "tipo"),
            explanation = "No se pudo clasificar con total seguridad. Por favor, revisa los datos."
        )
    }

    private fun extractAmountCents(text: String): Long? {
        val pattern = Pattern.compile("(\\d+([.,]\\d{1,2})?)\\s*(?:euros|€)")
        val matcher = pattern.matcher(text)
        if (matcher.find()) {
            val numStr = matcher.group(1) ?: return null
            return MoneyUtils.parseAmountToCents(numStr)
        }
        val generalPattern = Pattern.compile("(\\d+([.,]\\d{1,2})?)")
        val generalMatcher = generalPattern.matcher(text)
        if (generalMatcher.find()) {
            val numStr = generalMatcher.group(1) ?: return null
            return MoneyUtils.parseAmountToCents(numStr)
        }
        return null
    }

    private fun extractDescriptionAfter(text: String, keyword: String): String? {
        val idx = text.indexOf(keyword)
        if (idx == -1) return null
        val sub = text.substring(idx + keyword.length).trim()
        val stopWords = listOf(" por ", " para ", " el ", " ayer ")
        var endIdx = sub.length
        for (stop in stopWords) {
            val stopIdx = sub.indexOf(stop)
            if (stopIdx != -1 && stopIdx < endIdx) {
                endIdx = stopIdx
            }
        }
        return sub.substring(0, endIdx).trim().ifBlank { null }
    }

    private fun extractProjectName(text: String): String? {
        if (text.contains("proyecto")) {
            return extractDescriptionAfter(text, "proyecto")?.replaceFirstChar { it.uppercase() }
        }
        if (text.contains("reborn")) {
            return extractDescriptionAfter(text, "reborn")?.replaceFirstChar { it.uppercase() }
        }
        return null
    }
}
