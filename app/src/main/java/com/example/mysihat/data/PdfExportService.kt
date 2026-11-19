package com.example.mysihat.data

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object PdfExportService {

    fun generateAndSharePatientReport(context: Context): File? {
        val pdfDoc = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // Standard A4 (72 DPI)
        val page = pdfDoc.startPage(pageInfo)
        val canvas = page.canvas

        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val titlePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(13, 71, 161) // Deep Blue
            textSize = 14f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val headerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(15, 23, 42)
            textSize = 9.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(30, 41, 59)
            textSize = 8.5f
        }

        // Top Header Gradient Banner
        val headerRect = Rect(0, 0, 595, 75)
        val headerGrad = LinearGradient(
            0f, 0f, 595f, 0f,
            Color.rgb(13, 71, 161),
            Color.rgb(25, 118, 210),
            Shader.TileMode.CLAMP
        )
        val bannerPaint = Paint().apply { shader = headerGrad }
        canvas.drawRect(headerRect, bannerPaint)

        // Title on Header
        val whiteTitle = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 14f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        val whiteSub = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(227, 242, 253)
            textSize = 8.5f
        }
        canvas.drawText("MYSIHAT • SISTEM PEMANTAUAN KESIHATAN & DIABETES", 25f, 28f, whiteTitle)
        canvas.drawText("PERSONAL DIGITAL HEALTH & BLOOD PRESSURE MONITORING LOG", 25f, 46f, whiteSub)
        canvas.drawText("Tarikh Cetakan: ${SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date())}", 25f, 60f, whiteSub)

        var yPos = 90f

        // Patient Information Box
        val user = MockDataRepository.currentUser.value
        val boxRect = RectF(25f, yPos, 570f, yPos + 68f)
        paint.color = Color.rgb(240, 247, 255)
        canvas.drawRoundRect(boxRect, 8f, 8f, paint)
        paint.color = Color.rgb(187, 222, 251)
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1f
        canvas.drawRoundRect(boxRect, 8f, 8f, paint)
        paint.style = Paint.Style.FILL

        canvas.drawText("NAMA PESAKIT: ${user.name.uppercase()} (UMUR: ${user.age} TAHUN, ${user.gender.uppercase()})", 38f, yPos + 18f, headerPaint)
        canvas.drawText("NO. KAD PENGENALAN (MYKAD): ${user.myKad}   |   MYSIHAT ID: ${user.sihatId}", 38f, yPos + 32f, textPaint)
        canvas.drawText("DIAGNOSIS KLINIKAL: ${user.condition.uppercase()}   |   DARAH: ${user.bloodType}   |   TINGGI: ${user.heightCm} cm   |   BERAT: ${user.weightKg} kg", 38f, yPos + 46f, textPaint)
        canvas.drawText("WARIS KECEMASAN: ${user.emergencyContact.name} (${user.emergencyContact.relationship}) - ${user.emergencyContact.phone}", 38f, yPos + 58f, textPaint)

        yPos += 82f

        // Section 1: Blood Glucose Records Table (7-Day Trend)
        canvas.drawText("1. REKOD PEMANTAUAN GLUKOSA DARAH (7 HARI TERAKHIR)", 25f, yPos, titlePaint)
        yPos += 14f

        // Table Header
        val thRect = RectF(25f, yPos, 570f, yPos + 18f)
        paint.color = Color.rgb(25, 118, 210)
        canvas.drawRect(thRect, paint)

        val thText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            textSize = 8.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText("TARIKH & MASA", 32f, yPos + 12f, thText)
        canvas.drawText("WAKTU MAKAN", 135f, yPos + 12f, thText)
        canvas.drawText("BACAAN (2 PERPULUHAN)", 270f, yPos + 12f, thText)
        canvas.drawText("STATUS CPG", 380f, yPos + 12f, thText)
        canvas.drawText("CATATAN KLINIKAL", 460f, yPos + 12f, thText)

        yPos += 18f

        // Rows
        val glucoseList = MockDataRepository.glucoseRecords.take(7)
        glucoseList.forEachIndexed { idx, rec ->
            val rowBg = if (idx % 2 == 0) Color.rgb(248, 250, 252) else Color.WHITE
            paint.color = rowBg
            canvas.drawRect(25f, yPos, 570f, yPos + 18f, paint)

            canvas.drawText("${rec.dateLabel}, ${rec.timeLabel}", 32f, yPos + 12f, textPaint)
            canvas.drawText(rec.timing.bmLabel, 135f, yPos + 12f, textPaint)

            // Highlight High (>=10) or Low (<4)
            val valPaint = Paint(textPaint).apply {
                if (rec.valueMmol >= 10.0) {
                    color = Color.rgb(220, 38, 38)
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                } else if (rec.valueMmol < 4.0) {
                    color = Color.rgb(126, 34, 206)
                    typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                }
            }
            canvas.drawText(String.format(Locale.US, "%.2f mmol/L", rec.valueMmol), 270f, yPos + 12f, valPaint)

            // Status chip text (3 Indicators only: LOW, NORMAL, HIGH)
            val stColor = when (rec.severity) {
                HealthSeverity.LOW -> Color.rgb(126, 34, 206)
                HealthSeverity.NORMAL -> Color.rgb(5, 150, 105)
                HealthSeverity.HIGH -> Color.rgb(220, 38, 38)
            }
            val stPaint = Paint(textPaint).apply {
                color = stColor
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            canvas.drawText(rec.severity.bmLabel, 380f, yPos + 12f, stPaint)

            val noteStr = if (rec.note.length > 22) rec.note.substring(0, 20) + "..." else rec.note
            canvas.drawText(noteStr, 460f, yPos + 12f, textPaint)

            yPos += 18f
        }

        yPos += 16f

        // Section 2: Blood Pressure Records Table
        canvas.drawText("2. REKOD TEKANAN DARAH (SYSTOLIK, DIASTOLIK) & NADI (PULSE)", 25f, yPos, titlePaint)
        yPos += 14f

        val bpThRect = RectF(25f, yPos, 570f, yPos + 18f)
        paint.color = Color.rgb(13, 71, 161)
        canvas.drawRect(bpThRect, paint)

        canvas.drawText("TARIKH & MASA", 32f, yPos + 12f, thText)
        canvas.drawText("SYSTOLIK (mmHg)", 140f, yPos + 12f, thText)
        canvas.drawText("DIASTOLIK (mmHg)", 245f, yPos + 12f, thText)
        canvas.drawText("NADI (bpm)", 355f, yPos + 12f, thText)
        canvas.drawText("KLASIFIKASI CPG", 440f, yPos + 12f, thText)

        yPos += 18f

        val bpList = MockDataRepository.bpRecords.take(7)
        bpList.forEachIndexed { idx, bp ->
            val rowBg = if (idx % 2 == 0) Color.rgb(248, 250, 252) else Color.WHITE
            paint.color = rowBg
            canvas.drawRect(25f, yPos, 570f, yPos + 18f, paint)

            canvas.drawText("${bp.dateLabel}, ${bp.timeLabel}", 32f, yPos + 12f, textPaint)
            canvas.drawText("${bp.systolic} mmHg", 140f, yPos + 12f, textPaint)
            canvas.drawText("${bp.diastolic} mmHg", 245f, yPos + 12f, textPaint)
            canvas.drawText("${bp.pulse} bpm", 355f, yPos + 12f, textPaint)

            val bpStPaint = Paint(textPaint).apply {
                color = when (bp.severity) {
                    HealthSeverity.LOW -> Color.rgb(126, 34, 206)
                    HealthSeverity.NORMAL -> Color.rgb(5, 150, 105)
                    HealthSeverity.HIGH -> Color.rgb(220, 38, 38)
                }
                typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            }
            canvas.drawText(bp.category, 440f, yPos + 12f, bpStPaint)

            yPos += 18f
        }

        yPos += 22f

        // Footer Verification Box
        val footRect = RectF(25f, yPos, 570f, yPos + 46f)
        paint.color = Color.rgb(241, 245, 249)
        canvas.drawRoundRect(footRect, 6f, 6f, paint)

        val footerText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(100, 116, 139)
            textSize = 7.5f
        }
        canvas.drawText("PENGESAHAN SISTEM: Laporan digital ini dijanakan oleh aplikasi MySihat bagi tujuan pemantauan kendiri pesakit.", 35f, yPos + 16f, footerText)
        canvas.drawText("Bawa laporan ini semasa temujanji di fasiliti kesihatan bagi semakan rekod dan konsultasi perubatan.", 35f, yPos + 30f, footerText)

        pdfDoc.finishPage(page)

        val isEn = LanguageManager.isEnglish
        // Save PDF to App-Specific Cache & Device Downloads
        return try {
            val reportsDir = File(context.cacheDir, "reports")
            if (!reportsDir.exists()) reportsDir.mkdirs()
            val fileName = "Laporan_MySihat_${user.myKad.replace("-", "")}.pdf"
            val pdfFile = File(reportsDir, fileName)
            val outputStream = FileOutputStream(pdfFile)
            pdfDoc.writeTo(outputStream)
            outputStream.flush()
            outputStream.close()
            pdfDoc.close()

            // Also save directly into public Downloads folder on Android 10+ (API 29+)
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val resolver = context.contentResolver
                    val contentValues = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                        put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                    }
                    val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                    if (uri != null) {
                        resolver.openOutputStream(uri)?.use { out ->
                            pdfFile.inputStream().use { input -> input.copyTo(out) }
                        }
                    }
                }
            } catch (_: Exception) {}

            NotificationService.showSuccess(
                if (isEn) "Download PDF Successful! '$fileName' saved to Downloads & ready to share."
                else "Muat Turun PDF Berjaya! Fail '$fileName' disimpan ke Downloads & sedia dikongsi."
            )
            pdfFile
        } catch (e: Exception) {
            e.printStackTrace()
            try { pdfDoc.close() } catch (_: Exception) {}
            NotificationService.showError(
                if (isEn) "Download PDF Failed: Could not generate patient health report!"
                else "Muat Turun PDF Tidak Berjaya: Gagal menjana laporan kesihatan pesakit!"
            )
            null
        }
    }

    fun sharePdf(context: Context, file: File) {
        val isEn = LanguageManager.isEnglish
        try {
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Laporan Pemantauan Kesihatan MySihat - ${MockDataRepository.currentUser.value.name}")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            val chooser = Intent.createChooser(sendIntent, "Kongsi Laporan Kesihatan PDF")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
        } catch (e: Exception) {
            NotificationService.showError(
                if (isEn) "Share PDF Failed: ${e.localizedMessage ?: "Unable to open PDF viewer"}"
                else "Kongsi PDF Tidak Berjaya: ${e.localizedMessage ?: "Gagal membuka pembaca PDF"}"
            )
        }
    }
}
