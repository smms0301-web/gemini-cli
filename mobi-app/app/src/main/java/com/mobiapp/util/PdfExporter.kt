package com.mobiapp.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import com.mobiapp.data.entity.ProcessEntity
import com.mobiapp.data.entity.ProcessStepEntity
import java.io.File

object PdfExporter {

    fun export(
        context: Context,
        process: ProcessEntity,
        steps: List<ProcessStepEntity>
    ): File {
        val pageWidth = 595
        val pageHeight = 842
        val margin = 40f
        val amberColor = Color.parseColor("#FFB300")

        val document = PdfDocument()
        var pageNumber = 1
        var pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
        var page = document.startPage(pageInfo)
        var canvas: Canvas = page.canvas
        var y = 0f

        val headerPaint = Paint().apply { color = amberColor; style = Paint.Style.FILL }
        val titlePaint = Paint().apply {
            color = Color.BLACK; textSize = 22f; isFakeBoldText = true
        }
        val bodyPaint = Paint().apply { color = Color.DKGRAY; textSize = 14f }
        val stepTitlePaint = Paint().apply { color = Color.BLACK; textSize = 16f; isFakeBoldText = true }
        val stepBodyPaint = Paint().apply { color = Color.DKGRAY; textSize = 13f }

        fun newPage() {
            document.finishPage(page)
            pageNumber++
            pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create()
            page = document.startPage(pageInfo)
            canvas = page.canvas
            y = margin
        }

        fun checkY(needed: Float) {
            if (y + needed > pageHeight - margin) newPage()
        }

        canvas.drawRect(0f, 0f, pageWidth.toFloat(), 60f, headerPaint)
        titlePaint.color = Color.WHITE
        canvas.drawText("Mobi App - Process Report", margin, 40f, titlePaint)
        titlePaint.color = Color.BLACK
        y = 80f

        canvas.drawText(process.title, margin, y, titlePaint)
        y += 30f
        if (process.description.isNotBlank()) {
            canvas.drawText(process.description, margin, y, bodyPaint)
            y += 24f
        }
        if (process.category.isNotBlank()) {
            canvas.drawText("Category: ${process.category}", margin, y, bodyPaint)
            y += 24f
        }
        y += 16f

        for (step in steps) {
            checkY(80f)
            val stepPaint = Paint().apply { color = amberColor; style = Paint.Style.FILL }
            canvas.drawRect(margin, y, margin + 4f, y + 20f, stepPaint)
            canvas.drawText("Step ${step.stepNumber}: ${step.title}", margin + 12f, y + 16f, stepTitlePaint)
            y += 28f
            if (step.description.isNotBlank()) {
                val lines = step.description.split("\n")
                for (line in lines) {
                    checkY(20f)
                    canvas.drawText(line, margin + 12f, y, stepBodyPaint)
                    y += 20f
                }
            }
            y += 12f
        }

        document.finishPage(page)

        val dir = File(context.getExternalFilesDir(null), "PDFs").also { it.mkdirs() }
        val file = File(dir, "process_${process.id}_${System.currentTimeMillis()}.pdf")
        file.outputStream().use { document.writeTo(it) }
        document.close()
        return file
    }
}
