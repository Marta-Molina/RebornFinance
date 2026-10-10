package com.example.rebornfinance.core.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import com.example.rebornfinance.core.money.MoneyUtils
import com.example.rebornfinance.domain.calculator.FinancialStatistics
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object PdfReportUtils {

    fun generateFinancialReportPdf(
        context: Context,
        stats: FinancialStatistics
    ): File? {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 size in points
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas
        val paint = Paint().apply {
            textSize = 14f
            color = android.graphics.Color.BLACK
        }

        val titlePaint = Paint().apply {
            textSize = 20f
            isFakeBoldText = true
            color = android.graphics.Color.argb(255, 138, 107, 116) // Primary Mauve
        }

        var y = 40f
        canvas.drawText("RebornFinance - Informe Financiero", 40f, y, titlePaint)
        y += 30f

        paint.textSize = 12f
        canvas.drawText("Resumen de Ingresos y Gastos del Periodo", 40f, y, paint)
        y += 25f

        canvas.drawText("• Total Ingresos: ${MoneyUtils.formatCents(stats.totalIncomeCents)}", 60f, y, paint)
        y += 20f
        canvas.drawText("• Total Gastos: ${MoneyUtils.formatCents(stats.totalExpenseCents)}", 60f, y, paint)
        y += 20f
        canvas.drawText("• Balance Neto: ${MoneyUtils.formatCents(stats.netBalanceCents)}", 60f, y, paint)
        y += 30f

        canvas.drawText("Desglose de Gastos por Categoría:", 40f, y, paint)
        y += 25f

        if (stats.expenseByCategory.isEmpty()) {
            canvas.drawText("  (No hay gastos registrados)", 60f, y, paint)
        } else {
            for ((category, amount) in stats.expenseByCategory) {
                canvas.drawText("• $category: ${MoneyUtils.formatCents(amount)}", 60f, y, paint)
                y += 20f
                if (y > 780f) break
            }
        }

        pdfDocument.finishPage(page)

        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "Informe_RebornFinance.pdf")
        try {
            pdfDocument.writeTo(FileOutputStream(file))
            pdfDocument.close()
            return file
        } catch (e: IOException) {
            e.printStackTrace()
            pdfDocument.close()
            return null
        }
    }
}
