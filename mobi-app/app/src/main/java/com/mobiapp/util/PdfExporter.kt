package com.mobiapp.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.graphics.Color as AColor
import com.mobiapp.data.entity.ProcessEntity
import com.mobiapp.data.entity.ProcessStepEntity
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

data class PdfExportOptions(
    val includeNotes: Boolean = true,
    val includeStatus: Boolean = true,
    val includeTimestamps: Boolean = false,
    val includePhotos: Boolean = false
)

object PdfExporter {

    private const val PAGE_WIDTH = 595
    private const val PAGE_HEIGHT = 842
    private const val MARGIN = 48f
    private val DATE_FMT = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())

    fun export(
        context: Context,
        process: ProcessEntity,
        steps: List<ProcessStepEntity>,
        options: PdfExportOptions
    ): File? {
        return try {
            val doc = PdfDocument()
            val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
            var page = doc.startPage(pageInfo)
            var canvas = page.canvas
            var y = MARGIN

            val paintTitle = Paint().apply {
                color = AColor.rgb(0x1A, 0x1A, 0x1A)
                textSize = 22f
                typeface = Typeface.DEFAULT_BOLD
            }
            val paintHeader = Paint().apply {
                color = AColor.rgb(0xFF, 0xB3, 0x00)
                textSize = 14f
                typeface = Typeface.DEFAULT_BOLD
            }
            val paintBody = Paint().apply {
                color = AColor.rgb(0x22, 0x22, 0x22)
                textSize = 12f
            }
            val paintMeta = Paint().apply {
                color = AColor.rgb(0x66, 0x66, 0x66)
                textSize = 10f
            }
            val paintLine = Paint().apply {
                color = AColor.rgb(0xDD, 0xDD, 0xDD)
                strokeWidth = 1f
            }
            val contentWidth = PAGE_WIDTH - MARGIN * 2

            // Header bar
            val headerPaint = Paint().apply { color = AColor.rgb(0x1A, 0x1A, 0x1A) }
            canvas.drawRect(0f, 0f, PAGE_WIDTH.toFloat(), 56f, headerPaint)
            val paintWhite = Paint().apply {
                color = AColor.WHITE; textSize = 18f; typeface = Typeface.DEFAULT_BOLD
            }
            canvas.drawText("Mobi App", MARGIN, 36f, paintWhite)
            val paintGoldSmall = Paint().apply { color = AColor.rgb(0xFF, 0xB3, 0x00); textSize = 10f }
            canvas.drawText("Process Export", PAGE_WIDTH - MARGIN - 80f, 36f, paintGoldSmall)
            y = 72f

            // Process title
            canvas.drawText(process.title, MARGIN, y, paintTitle)
            y += 20f
            canvas.drawText("${process.category}  •  ${process.siteTag}", MARGIN, y, paintMeta)
            y += 8f
            if (options.includeTimestamps) {
                canvas.drawText("Created: ${DATE_FMT.format(Date(process.createdAt))}", MARGIN, y, paintMeta)
                y += 14f
            }
            canvas.drawLine(MARGIN, y, PAGE_WIDTH - MARGIN, y, paintLine)
            y += 16f

            steps.forEach { step ->
                if (y > PAGE_HEIGHT - 80f) {
                    doc.finishPage(page)
                    val nextInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, doc.pages.size + 1).create()
                    page = doc.startPage(nextInfo)
                    canvas = page.canvas
                    y = MARGIN
                }

                val statusDot = if (step.isDone) "✓" else "○"
                val stepLabel = "Step ${step.stepNumber}  $statusDot"
                canvas.drawText(stepLabel, MARGIN, y, paintHeader)
                y += 16f
                canvas.drawText(step.title, MARGIN + 8f, y, paintBody.apply { typeface = Typeface.DEFAULT_BOLD; textSize = 13f })
                y += 16f

                if (options.includeNotes && step.note.isNotBlank()) {
                    val noteLines = wrapText(step.note, paintBody.apply { typeface = Typeface.DEFAULT; textSize = 12f }, contentWidth - 16f)
                    noteLines.forEach { line ->
                        if (y > PAGE_HEIGHT - 60f) {
                            doc.finishPage(page)
                            val ni = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, doc.pages.size + 1).create()
                            page = doc.startPage(ni); canvas = page.canvas; y = MARGIN
                        }
                        canvas.drawText(line, MARGIN + 8f, y, paintBody)
                        y += 15f
                    }
                }
                if (options.includeTimestamps) {
                    canvas.drawText("Updated: ${DATE_FMT.format(Date(step.updatedAt))}", MARGIN + 8f, y, paintMeta)
                    y += 12f
                }
                y += 8f
                canvas.drawLine(MARGIN, y, PAGE_WIDTH - MARGIN, y, paintLine)
                y += 12f
            }

            // Footer
            val footerY = PAGE_HEIGHT - 24f
            canvas.drawText("Generated by Mobi App  •  ${DATE_FMT.format(Date())}", MARGIN, footerY, paintMeta)

            doc.finishPage(page)

            val dir = File(context.cacheDir, "exports").apply { mkdirs() }
            val file = File(dir, "process_${process.id}_${System.currentTimeMillis()}.pdf")
            FileOutputStream(file).use { doc.writeTo(it) }
            doc.close()
            file
        } catch (e: Exception) {
            null
        }
    }

    private fun wrapText(text: String, paint: Paint, maxWidth: Float): List<String> {
        val words = text.split(" ")
        val lines = mutableListOf<String>()
        var current = StringBuilder()
        words.forEach { word ->
            val test = if (current.isEmpty()) word else "$current $word"
            if (paint.measureText(test) <= maxWidth) {
                current = StringBuilder(test)
            } else {
                if (current.isNotEmpty()) lines.add(current.toString())
                current = StringBuilder(word)
            }
        }
        if (current.isNotEmpty()) lines.add(current.toString())
        return lines
    }
}
