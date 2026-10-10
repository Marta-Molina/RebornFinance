package com.example.rebornfinance.domain.parser

import com.example.rebornfinance.core.date.DateUtils
import com.example.rebornfinance.core.money.MoneyUtils
import com.example.rebornfinance.domain.model.ParsedOperationProposal
import java.util.regex.Pattern

object LocalRuleBasedParser {

    fun parse(text: String): ParsedOperationProposal {
        val lower = text.lowercase().trim()
        val now = DateUtils.getCurrentTimestamp()

        // 1. Expense check: "gastado [amount] euros en [desc]" or "gastado [amount] euros en [desc] para el proyecto [project]"
        if (lower.contains("gastado") || lower.contains("gasto")) {
            val amountCents = extractAmountCents(lower)
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

        // 2. Sale / Income check: "vendí el reborn [name] por [amount]"
        if (lower.contains("vendí") || lower.contains("vendido")) {
            val amountCents = extractAmountCents(lower)
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

        // 3. Refund check: "devuelto [amount] euros"
        if (lower.contains("devuelto") || lower.contains("reembolsado")) {
            val amountCents = extractAmountCents(lower)
            return ParsedOperationProposal(
                operationType = "REFUND",
                amountCents = amountCents,
                description = "Reembolso de pedido",
                category = "Reembolso de compras",
                date = now,
                explanation = "Analizado mediante reglas locales (Analizador determinista)."
            )
        }

        // 4. Envelope add: "reservado [amount] euros" or "reservado [amount] euros para [envelope]"
        if (lower.contains("reservado") || lower.contains("guardado")) {
            val amountCents = extractAmountCents(lower)
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

        // 5. Envelope withdraw: "retirado [amount] euros del sobre [envelope]"
        if (lower.contains("retirado") || lower.contains("sacado")) {
            val amountCents = extractAmountCents(lower)
            val envName = extractDescriptionAfter(lower, "sobre") ?: "Ahorro"
            return ParsedOperationProposal(
                operationType = "ENVELOPE_WITHDRAW",
                amountCents = amountCents,
                description = "Retirada de sobre",
                category = "Ahorro",
                date = now,
                envelopeName = envName.replaceFirstChar { it.uppercase() },
                explanation = "Analizado mediante reglas locales (Analizador determinista)."
            )
        }

        // Default fallback
        val amountCents = extractAmountCents(lower)
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
        // Matches numbers like 35, 12,50, 450, etc. before "euros" or €
        val pattern = Pattern.compile("(\\d+([.,]\\d{1,2})?)\\s*(?:euros|€)")
        val matcher = pattern.matcher(text)
        if (matcher.find()) {
            val numStr = matcher.group(1) ?: return null
            return MoneyUtils.parseAmountToCents(numStr)
        }
        // Fallback to any number
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
        // Cut off at "por", "para", "el", etc. if present
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
