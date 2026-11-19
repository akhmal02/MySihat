package com.example.mysihat.ui.screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.mysihat.data.*
import com.example.mysihat.ui.components.*
import com.example.mysihat.ui.theme.*
import java.util.Locale

@Composable
fun VitalsScreen() {
    val context = LocalContext.current
    val isEn = LanguageManager.isEnglish
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Blood Glucose, 1: Blood Pressure

    // Glucose Input States with real Malaysian date & time defaults
    var glucoseTextInput by remember { mutableStateOf("") }
    var selectedTiming by remember { mutableStateOf(GlucoseTiming.FASTING) }
    var glucoseDateInput by remember { mutableStateOf(MockDataRepository.getTodayDateString()) }
    var glucoseTimeInput by remember { mutableStateOf(MockDataRepository.getNowTimeString()) }
    var glucoseNote by remember { mutableStateOf("") }

    // BP Input States with real Malaysian date & time defaults
    var sysInput by remember { mutableStateOf("") }
    var diaInput by remember { mutableStateOf("") }
    var pulseInput by remember { mutableStateOf("") }
    var bpDateInput by remember { mutableStateOf(MockDataRepository.getTodayDateString()) }
    var bpTimeInput by remember { mutableStateOf(MockDataRepository.getNowTimeString()) }

    // Interactive Chart Touch State
    var selectedPointIndex by remember { mutableIntStateOf(-1) }

    // Edit Modal States for Update Record Notifications
    var editingGlucoseRecord by remember { mutableStateOf<GlucoseRecord?>(null) }
    var editingBpRecord by remember { mutableStateOf<BloodPressureRecord?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Safe Top Bar Below Status Bar
        item {
            MySihatTopBar(
                screenTitle = if (selectedTab == 0) {
                    if (isEn) "Blood Glucose" else "Glukosa Darah"
                } else {
                    if (isEn) "Blood Pressure" else "Tekanan Darah"
                },
                subtitle = if (isEn) "Malaysian CPG Guidelines • Health Monitoring" else "Piawaian Garis Panduan CPG • Pemantauan Kendiri",
                action = {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White.copy(alpha = 0.22f))
                            .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            .bounceClick {
                                val file = PdfExportService.generateAndSharePatientReport(context)
                                if (file != null) {
                                    PdfExportService.sharePdf(context, file)
                                }
                            }
                            .padding(horizontal = 9.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (isEn) "PDF Report 📄" else "Laporan PDF 📄",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            softWrap = false,
                            maxLines = 1
                        )
                    }
                }
            )
        }

        // Tab Selector (Switch between Glucose and Blood Pressure)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(16.dp), spotColor = BlueDark.copy(alpha = 0.1f))
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceLight)
                        .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                        .padding(4.dp)
                ) {
                    // Glucose Tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (selectedTab == 0) BluePrimary else Color.Transparent)
                            .bounceClick {
                                selectedTab = 0
                                selectedPointIndex = -1
                            }
                            .padding(vertical = 11.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            ModernGlucoseDropletIcon(
                                size = 18.dp,
                                tint = if (selectedTab == 0) Color.White else BluePrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isEn) "Blood Glucose" else "Glukosa Darah",
                                color = if (selectedTab == 0) Color.White else Color(0xFF0F172A),
                                fontSize = 12.5.sp,
                                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }

                    // Blood Pressure Tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (selectedTab == 1) BluePrimary else Color.Transparent)
                            .bounceClick {
                                selectedTab = 1
                                selectedPointIndex = -1
                            }
                            .padding(vertical = 11.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            ModernPulseHeartIcon(
                                size = 18.dp,
                                tint = if (selectedTab == 1) Color.White else Color(0xFFDC2626)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isEn) "Blood Pressure" else "Tekanan Darah",
                                color = if (selectedTab == 1) Color.White else Color(0xFF0F172A),
                                fontSize = 12.5.sp,
                                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        // Standard Range Legend Box (3 Indicators Only: Low, Normal, High)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 2.dp)
            ) {
                if (selectedTab == 0) {
                    RangeLegendBox()
                } else {
                    BpRangeLegendBox()
                }
            }
        }

        // 7-DAY INTERACTIVE CHART VISUALIZATION
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .shadow(6.dp, RoundedCornerShape(20.dp), spotColor = BlueDark.copy(alpha = 0.08f))
                    .clip(RoundedCornerShape(20.dp))
                    .background(SurfaceLight)
                    .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
                    .padding(18.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (selectedTab == 0) {
                                    if (isEn) "7-Day Glucose Trend (Interactive Touch)" else "Trend Glukosa 7 Hari (Sentuhan Interaktif)"
                                } else {
                                    if (isEn) "7-Day BP Dual-Line Trend (Systolic & Diastolic)" else "Trend BP 2 Garisan 7 Hari (Sistolik & Diastolik)"
                                },
                                color = Color(0xFF0F172A), // Black Font
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = if (selectedTab == 0) {
                                    if (isEn) "Touch graph point to see date, time & 3-indicator status" else "Sentuh graf untuk melihat tarikh, masa & 3 penunjuk status"
                                } else {
                                    if (isEn) "Red: Systolic | Blue: Diastolic | Target: Normal" else "Merah: Sistolik | Biru: Diastolik | Sasaran: Normal"
                                },
                                color = Color(0xFF64748B),
                                fontSize = 10.5.sp
                            )
                        }

                        // Status Badge Callout for Selected Point
                        val gChartList = MockDataRepository.glucoseRecords.take(7).reversed()
                        val bpChartList = MockDataRepository.bpRecords.take(7).reversed()
                        if (selectedTab == 0 && selectedPointIndex in gChartList.indices) {
                            val activeRec = gChartList[selectedPointIndex]
                            ClinicalSeverityBadge(severity = activeRec.severity)
                        } else if (selectedTab == 1 && selectedPointIndex in bpChartList.indices) {
                            val activeBp = bpChartList[selectedPointIndex]
                            ClinicalSeverityBadge(severity = activeBp.severity)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (selectedTab == 0) {
                        // ==================== GLUCOSE 7-DAY LINE CHART ====================
                        val chronGlucose = MockDataRepository.glucoseRecords.take(7).reversed()

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                        ) {
                            Canvas(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .pointerInput(chronGlucose.size) {
                                        detectTapGestures { offset ->
                                            if (chronGlucose.isNotEmpty()) {
                                                val stepX = size.width / chronGlucose.size
                                                val idx = (offset.x / stepX).toInt().coerceIn(0, chronGlucose.lastIndex)
                                                selectedPointIndex = idx
                                            }
                                        }
                                    }
                            ) {
                                val w = size.width
                                val h = size.height

                                val minY = 2.0
                                val maxY = 15.0
                                fun toY(v: Double): Float {
                                    val norm = ((maxY - v.coerceIn(minY, maxY)) / (maxY - minY)).toFloat()
                                    return norm * (h * 0.85f) + h * 0.08f
                                }

                                // Normal band (4.0 to 10.0 mmol/L)
                                val optTop = toY(10.0)
                                val optBottom = toY(4.0)
                                drawRect(
                                    color = Color(0xFFD1FAE5).copy(alpha = 0.6f),
                                    topLeft = Offset(0f, optTop),
                                    size = Size(w, (optBottom - optTop).coerceAtLeast(4f))
                                )

                                // High Alert line (10.0 mmol/L)
                                val alertY = toY(10.0)
                                drawLine(
                                    color = Color(0xFFFCA5A5),
                                    start = Offset(0f, alertY),
                                    end = Offset(w, alertY),
                                    strokeWidth = 1.5.dp.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f))
                                )

                                if (chronGlucose.isNotEmpty()) {
                                    val stepX = w / chronGlucose.size
                                    val points = chronGlucose.mapIndexed { idx, rec ->
                                        val x = (idx * stepX) + (stepX / 2f)
                                        val y = toY(rec.valueMmol)
                                        Offset(x, y)
                                    }

                                    // Curve
                                    val path = Path().apply {
                                        moveTo(points[0].x, points[0].y)
                                        for (i in 1 until points.size) {
                                            val prev = points[i - 1]
                                            val curr = points[i]
                                            cubicTo((prev.x + curr.x) / 2f, prev.y, (prev.x + curr.x) / 2f, curr.y, curr.x, curr.y)
                                        }
                                    }

                                    drawPath(
                                        path = path,
                                        color = BluePrimary,
                                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                                    )

                                    // 3 Indicator Colors: LOW (Purple), NORMAL (Green), HIGH (Red)
                                    points.forEachIndexed { i, pt ->
                                        val rec = chronGlucose[i]
                                        val isSelected = selectedPointIndex == i
                                        val ptColor = when (rec.severity) {
                                            HealthSeverity.LOW -> Color(0xFF7E22CE)
                                            HealthSeverity.NORMAL -> StatusLowRisk
                                            HealthSeverity.HIGH -> Color(0xFFDC2626)
                                        }

                                        if (isSelected) {
                                            drawCircle(color = ptColor.copy(alpha = 0.35f), radius = 11.dp.toPx(), center = pt)
                                        }
                                        drawCircle(color = Color.White, radius = 5.5.dp.toPx(), center = pt)
                                        drawCircle(color = ptColor, radius = 4.dp.toPx(), center = pt)
                                    }
                                }
                            }
                        }

                        // X-Axis Date Labels for Glucose
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 6.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            chronGlucose.forEach { r ->
                                val dateShort = r.dateLabel.split(" ").take(2).joinToString(" ")
                                Text(
                                    text = dateShort,
                                    color = Color(0xFF475569),
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    } else {
                        // ==================== BLOOD PRESSURE 2-LINE CHART ====================
                        val chronBp = MockDataRepository.bpRecords.take(7).reversed()

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                        ) {
                            Canvas(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .pointerInput(chronBp.size) {
                                        detectTapGestures { offset ->
                                            if (chronBp.isNotEmpty()) {
                                                val stepX = size.width / chronBp.size
                                                val idx = (offset.x / stepX).toInt().coerceIn(0, chronBp.lastIndex)
                                                selectedPointIndex = idx
                                            }
                                        }
                                    }
                            ) {
                                val w = size.width
                                val h = size.height

                                val minBp = 50.0
                                val maxBp = 180.0
                                fun toBpY(v: Int): Float {
                                    val norm = ((maxBp - v.coerceIn(minBp.toInt(), maxBp.toInt())) / (maxBp - minBp)).toFloat()
                                    return norm * (h * 0.85f) + h * 0.08f
                                }

                                // Target Zone: 60 to 130
                                val optTop = toBpY(130)
                                val optBottom = toBpY(70)
                                drawRect(
                                    color = Color(0xFFD1FAE5).copy(alpha = 0.5f),
                                    topLeft = Offset(0f, optTop),
                                    size = Size(w, (optBottom - optTop).coerceAtLeast(4f))
                                )

                                // Stage High Alert Line (>= 140 mmHg)
                                val alertY = toBpY(140)
                                drawLine(
                                    color = Color(0xFFFCA5A5),
                                    start = Offset(0f, alertY),
                                    end = Offset(w, alertY),
                                    strokeWidth = 1.5.dp.toPx(),
                                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f))
                                )

                                if (chronBp.isNotEmpty()) {
                                    val stepX = w / chronBp.size

                                    // Line 1: Systolic (Red)
                                    val sysPoints = chronBp.mapIndexed { idx, rec ->
                                        val x = (idx * stepX) + (stepX / 2f)
                                        val y = toBpY(rec.systolic)
                                        Offset(x, y)
                                    }
                                    val sysPath = Path().apply {
                                        moveTo(sysPoints[0].x, sysPoints[0].y)
                                        for (i in 1 until sysPoints.size) {
                                            val prev = sysPoints[i - 1]
                                            val curr = sysPoints[i]
                                            cubicTo((prev.x + curr.x) / 2f, prev.y, (prev.x + curr.x) / 2f, curr.y, curr.x, curr.y)
                                        }
                                    }
                                    drawPath(
                                        path = sysPath,
                                        color = Color(0xFFDC2626), // Systolic Red
                                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                                    )

                                    // Line 2: Diastolic (Blue)
                                    val diaPoints = chronBp.mapIndexed { idx, rec ->
                                        val x = (idx * stepX) + (stepX / 2f)
                                        val y = toBpY(rec.diastolic)
                                        Offset(x, y)
                                    }
                                    val diaPath = Path().apply {
                                        moveTo(diaPoints[0].x, diaPoints[0].y)
                                        for (i in 1 until diaPoints.size) {
                                            val prev = diaPoints[i - 1]
                                            val curr = diaPoints[i]
                                            cubicTo((prev.x + curr.x) / 2f, prev.y, (prev.x + curr.x) / 2f, curr.y, curr.x, curr.y)
                                        }
                                    }
                                    drawPath(
                                        path = diaPath,
                                        color = BluePrimary, // Diastolic Blue
                                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                                    )

                                    // Points
                                    sysPoints.forEachIndexed { i, pt ->
                                        val isSelected = selectedPointIndex == i
                                        if (isSelected) drawCircle(color = Color(0xFFDC2626).copy(alpha = 0.35f), radius = 10.dp.toPx(), center = pt)
                                        drawCircle(color = Color.White, radius = 5.dp.toPx(), center = pt)
                                        drawCircle(color = Color(0xFFDC2626), radius = 3.5.dp.toPx(), center = pt)
                                    }
                                    diaPoints.forEachIndexed { i, pt ->
                                        val isSelected = selectedPointIndex == i
                                        if (isSelected) drawCircle(color = BluePrimary.copy(alpha = 0.35f), radius = 10.dp.toPx(), center = pt)
                                        drawCircle(color = Color.White, radius = 5.dp.toPx(), center = pt)
                                        drawCircle(color = BluePrimary, radius = 3.5.dp.toPx(), center = pt)
                                    }
                                }
                            }
                        }

                        // X-Axis Date Labels for BP
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 6.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            chronBp.forEach { r ->
                                val dateShort = r.dateLabel.split(" ").take(2).joinToString(" ")
                                Text(
                                    text = dateShort,
                                    color = Color(0xFF475569),
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // INTERACTIVE TOUCH TOOLTIP CARD
                    if (selectedPointIndex != -1) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(BlueSurface)
                                .border(1.dp, Color(0xFFBFDBFE), RoundedCornerShape(12.dp))
                                .padding(10.dp)
                        ) {
                            if (selectedTab == 0) {
                                val chronGlucose = MockDataRepository.glucoseRecords.take(7).reversed()
                                if (selectedPointIndex in chronGlucose.indices) {
                                    val r = chronGlucose[selectedPointIndex]
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = "${r.dateLabel} • ${r.timeLabel}",
                                                color = Color(0xFF0F172A),
                                                fontSize = 11.5.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = if (isEn) r.timing.enLabel else r.timing.bmLabel,
                                                color = Color(0xFF475569),
                                                fontSize = 10.sp
                                            )
                                        }
                                        Row(verticalAlignment = Alignment.Bottom) {
                                            Text(
                                                text = String.format(Locale.US, "%.2f", r.valueMmol),
                                                color = if (r.severity == HealthSeverity.HIGH) StatusAlert else Color(0xFF0F172A),
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.ExtraBold
                                            )
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(
                                                text = "mmol/L",
                                                color = Color(0xFF64748B),
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            } else {
                                val chronBp = MockDataRepository.bpRecords.take(7).reversed()
                                if (selectedPointIndex in chronBp.indices) {
                                    val bp = chronBp[selectedPointIndex]
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = "${bp.dateLabel} • ${bp.timeLabel}",
                                                color = Color(0xFF0F172A),
                                                fontSize = 11.5.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = bp.category,
                                                color = Color(0xFF475569),
                                                fontSize = 10.sp
                                            )
                                        }
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(
                                                text = "${bp.systolic} / ${bp.diastolic} mmHg",
                                                color = Color(0xFF0F172A),
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.ExtraBold
                                            )
                                            Text(
                                                text = "${if (isEn) "Pulse: " else "Nadi: "}${bp.pulse} bpm",
                                                color = Color(0xFFDC2626),
                                                fontSize = 10.5.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // ==================== RECORD ENTRY FORM (NO "+" PREFIX, HELPFUL HINTS) ====================
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .shadow(6.dp, RoundedCornerShape(20.dp), spotColor = BlueDark.copy(alpha = 0.08f))
                    .clip(RoundedCornerShape(20.dp))
                    .background(SurfaceLight)
                    .border(1.dp, CardBorder, RoundedCornerShape(20.dp))
                    .padding(18.dp)
            ) {
                Column {
                    Text(
                        text = if (selectedTab == 0) {
                            if (isEn) "Record Blood Glucose" else "Catat Bacaan Glukosa"
                        } else {
                            if (isEn) "Record Blood Pressure & Pulse" else "Catat Tekanan Darah & Nadi"
                        },
                        color = Color(0xFF0F172A), // Pure Black Font
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    if (selectedTab == 0) {
                        // Numerical Input for Glucose (with 2 decimal places & helpful hint)
                        Text(
                            text = if (isEn) "Enter Glucose Value (mmol/L):" else "Masukkan Nilai Glukosa (mmol/L):",
                            color = Color(0xFF1E293B),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(BlueVeryLight)
                                    .bounceClick {
                                        val v = (glucoseTextInput.toDoubleOrNull() ?: 5.0) - 0.1
                                        if (v >= 1.0) glucoseTextInput = String.format(Locale.US, "%.2f", v)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "−", color = BlueDark, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            OutlinedTextField(
                                value = glucoseTextInput,
                                onValueChange = { glucoseTextInput = it },
                                placeholder = { Text("5.40", color = Color(0xFF94A3B8)) },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                trailingIcon = {
                                    Text(
                                        text = "mmol/L",
                                        color = Color(0xFF64748B),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(end = 10.dp)
                                    )
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BluePrimary,
                                    unfocusedBorderColor = CardBorder,
                                    focusedTextColor = Color(0xFF0F172A),
                                    unfocusedTextColor = Color(0xFF0F172A)
                                )
                            )

                            Spacer(modifier = Modifier.width(10.dp))

                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(BlueVeryLight)
                                    .bounceClick {
                                        val v = (glucoseTextInput.toDoubleOrNull() ?: 5.0) + 0.1
                                        if (v <= 30.0) glucoseTextInput = String.format(Locale.US, "%.2f", v)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "+", color = BlueDark, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Native Android Date & Time Picker Properties (No Manual Typing!)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            ModernDateTimePickerField(
                                label = if (isEn) "Date (Tap to Pick)" else "Tarikh (Sentuh Pilih)",
                                value = glucoseDateInput,
                                icon = "📅",
                                onClick = {
                                    showAndroidDatePicker(context, glucoseDateInput) { selectedDate ->
                                        glucoseDateInput = selectedDate
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )
                            ModernDateTimePickerField(
                                label = if (isEn) "Time (Tap to Pick)" else "Masa (Sentuh Pilih)",
                                value = glucoseTimeInput,
                                icon = "⏰",
                                onClick = {
                                    showAndroidTimePicker(context, glucoseTimeInput) { selectedTime ->
                                        glucoseTimeInput = selectedTime
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Full Clinical Timings Selector (9 Official Timings with 2h note)
                        Text(
                            text = if (isEn) "Meal Timing (Clinical CPG Guidelines):" else "Waktu Pemeriksaan (Garis Panduan Klinikal CPG):",
                            color = Color(0xFF0F172A),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            GlucoseTiming.values().forEach { timing ->
                                val isSelected = selectedTiming == timing
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) BlueVeryLight else BlueSurface)
                                        .border(1.dp, if (isSelected) BluePrimary else CardBorder, RoundedCornerShape(10.dp))
                                        .bounceClick { selectedTiming = timing }
                                        .padding(horizontal = 12.dp, vertical = 9.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = if (isEn) timing.enLabel else timing.bmLabel,
                                            color = if (isSelected) BlueDark else Color(0xFF0F172A),
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )

                                        if (timing.isPostMeal) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(6.dp))
                                                    .background(Color(0xFFFEF3C7))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = if (isEn) "⏱ Check After 2 Hours" else "⏱ Semak Selepas 2 Jam",
                                                    color = Color(0xFFD97706),
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    softWrap = false
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Catatan (Notes / Clinical Remarks Attribute)
                        Text(
                            text = if (isEn) "Clinical Notes / Remarks (Optional):" else "Catatan Tambahan Pesakit (Pilihan):",
                            color = Color(0xFF0F172A),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = glucoseNote,
                            onValueChange = { glucoseNote = it },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = {
                                Text(
                                    text = if (isEn) "e.g. Mild dizziness after meal, took insulin 10 mins before, brisk walk 20m"
                                           else "Cth: Rasa sedikit pening, ambil insulin 10 minit sebelum makan, bersenam 20 min",
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.5.sp
                                )
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BluePrimary,
                                unfocusedBorderColor = CardBorder,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color(0xFFF8FAFC)
                            ),
                            textStyle = TextStyle(color = Color(0xFF0F172A), fontSize = 12.5.sp),
                            maxLines = 2
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Save Glucose Button (No stupid tick, modern clean save)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(6.dp, RoundedCornerShape(14.dp), spotColor = BluePrimary.copy(alpha = 0.3f))
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFF0D47A1), Color(0xFF1976D2), Color(0xFF00B4D8))
                                    )
                                )
                                .bounceClick {
                                    val valParsed = glucoseTextInput.toDoubleOrNull()
                                    if (valParsed == null || valParsed !in 0.6..45.0) {
                                        NotificationService.showError(if (isEn) "Add Record Failed: Please enter a valid glucose number (0.6 – 45.0 mmol/L)!" else "Tambah Rekod Tidak Berjaya: Sila masukkan nilai glukosa yang sah (0.6 – 45.0 mmol/L)!")
                                    } else {
                                        MockDataRepository.addGlucoseReading(
                                            value = valParsed,
                                            timing = selectedTiming,
                                            date = glucoseDateInput.ifBlank { MockDataRepository.getTodayDateString() },
                                            time = glucoseTimeInput.ifBlank { MockDataRepository.getNowTimeString() },
                                            note = glucoseNote
                                        )
                                        glucoseTextInput = ""
                                        glucoseNote = ""
                                    }
                                }
                                .padding(vertical = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                ModernSaveDiskIcon(size = 18.dp, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isEn) "Save Glucose Record" else "Simpan Rekod Glukosa",
                                    color = Color.White,
                                    fontSize = 14.5.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    } else {
                        // Direct Custom Numerical Inputs for BP with Hints
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = sysInput,
                                onValueChange = { sysInput = it },
                                label = { Text(if (isEn) "Systolic" else "Sistolik", fontSize = 10.sp) },
                                placeholder = { Text("120", color = Color(0xFF94A3B8)) },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                trailingIcon = { Text("mmHg", fontSize = 8.5.sp, color = Color(0xFF64748B)) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFFDC2626),
                                    focusedTextColor = Color(0xFF0F172A),
                                    unfocusedTextColor = Color(0xFF0F172A)
                                )
                            )
                            OutlinedTextField(
                                value = diaInput,
                                onValueChange = { diaInput = it },
                                label = { Text(if (isEn) "Diastolic" else "Diastolik", fontSize = 10.sp) },
                                placeholder = { Text("80", color = Color(0xFF94A3B8)) },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                trailingIcon = { Text("mmHg", fontSize = 8.5.sp, color = Color(0xFF64748B)) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BluePrimary,
                                    focusedTextColor = Color(0xFF0F172A),
                                    unfocusedTextColor = Color(0xFF0F172A)
                                )
                            )
                            OutlinedTextField(
                                value = pulseInput,
                                onValueChange = { pulseInput = it },
                                label = { Text(if (isEn) "Pulse" else "Nadi", fontSize = 10.sp) },
                                placeholder = { Text("72", color = Color(0xFF94A3B8)) },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                trailingIcon = { Text("bpm", fontSize = 8.5.sp, color = Color(0xFF64748B)) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = Color(0xFF059669),
                                    focusedTextColor = Color(0xFF0F172A),
                                    unfocusedTextColor = Color(0xFF0F172A)
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Native Android Date & Time Picker Properties for BP (No Manual Typing!)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            ModernDateTimePickerField(
                                label = if (isEn) "Date (Tap to Pick)" else "Tarikh (Sentuh Pilih)",
                                value = bpDateInput,
                                icon = "📅",
                                onClick = {
                                    showAndroidDatePicker(context, bpDateInput) { selectedDate ->
                                        bpDateInput = selectedDate
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )
                            ModernDateTimePickerField(
                                label = if (isEn) "Time (Tap to Pick)" else "Masa (Sentuh Pilih)",
                                value = bpTimeInput,
                                icon = "⏰",
                                onClick = {
                                    showAndroidTimePicker(context, bpTimeInput) { selectedTime ->
                                        bpTimeInput = selectedTime
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Save BP Button (No tick, clean save)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(6.dp, RoundedCornerShape(14.dp), spotColor = BluePrimary.copy(alpha = 0.3f))
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFF0D47A1), Color(0xFF1976D2), Color(0xFF00B4D8))
                                    )
                                )
                                .bounceClick {
                                    val sys = sysInput.toIntOrNull()
                                    val dia = diaInput.toIntOrNull()
                                    val pulse = pulseInput.toIntOrNull() ?: 72
                                    if (sys == null || dia == null || sys !in 50..280 || dia !in 30..180 || sys <= dia) {
                                        NotificationService.showError(if (isEn) "Add Record Failed: Please enter valid numbers for Systolic & Diastolic (Systolic > Diastolic)!" else "Tambah Rekod Tidak Berjaya: Sila masukkan nilai Sistolik & Diastolik yang sah (Sistolik > Diastolik)!")
                                    } else {
                                        MockDataRepository.addBpReading(
                                            sys = sys,
                                            dia = dia,
                                            pulse = pulse,
                                            date = bpDateInput.ifBlank { MockDataRepository.getTodayDateString() },
                                            time = bpTimeInput.ifBlank { MockDataRepository.getNowTimeString() }
                                        )
                                        sysInput = ""
                                        diaInput = ""
                                        pulseInput = ""
                                    }
                                }
                                .padding(vertical = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                ModernSaveDiskIcon(size = 18.dp, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isEn) "Save Blood Pressure Record" else "Simpan Rekod Tekanan Darah",
                                    color = Color.White,
                                    fontSize = 14.5.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }
                }
            }
        }

        // ==================== RECENT 7-DAY HISTORY LIST ====================
        item {
            Text(
                text = if (selectedTab == 0) {
                    if (isEn) "Recent Glucose Log (${MockDataRepository.glucoseRecords.size} Records)" else "Rekod Sejarah Glukosa (${MockDataRepository.glucoseRecords.size} Rekod)"
                } else {
                    if (isEn) "Recent Blood Pressure Log (${MockDataRepository.bpRecords.size} Records)" else "Rekod Sejarah Tekanan Darah (${MockDataRepository.bpRecords.size} Rekod)"
                },
                color = Color(0xFF0F172A),
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
            )
        }

        if (selectedTab == 0) {
            if (MockDataRepository.glucoseRecords.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 6.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(BlueSurface)
                            .border(1.dp, Color(0xFFBFDBFE), RoundedCornerShape(16.dp))
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (isEn) "No Glucose Records Yet" else "Tiada Rekod Glukosa Lagi",
                                color = Color(0xFF0F172A),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isEn) "Your newly created account starts empty. Enter a glucose reading above and tap 'Save Glucose Record'." else "Akaun baharu anda bermula kosong. Masukkan nilai bacaan glukosa di atas dan tekan 'Simpan Rekod Glukosa'.",
                                color = Color(0xFF475569),
                                fontSize = 11.5.sp
                            )
                        }
                    }
                }
            }
            items(MockDataRepository.glucoseRecords) { rec ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 5.dp)
                        .shadow(2.dp, RoundedCornerShape(16.dp), spotColor = BlueDark.copy(alpha = 0.05f))
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceLight)
                        .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val iconBg = when (rec.severity) {
                                HealthSeverity.HIGH -> StatusAlertBg
                                HealthSeverity.LOW -> Color(0xFFF3E8FF)
                                HealthSeverity.NORMAL -> BlueVeryLight
                            }
                            val iconColor = when (rec.severity) {
                                HealthSeverity.HIGH -> StatusAlert
                                HealthSeverity.LOW -> Color(0xFF7E22CE)
                                HealthSeverity.NORMAL -> BluePrimary
                            }

                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(iconBg),
                                contentAlignment = Alignment.Center
                            ) {
                                ModernGlucoseDropletIcon(size = 20.dp, tint = iconColor)
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = String.format(Locale.US, "%.2f", rec.valueMmol),
                                        color = if (rec.severity == HealthSeverity.HIGH) StatusAlert else Color(0xFF0F172A),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "mmol/L",
                                        color = Color(0xFF64748B),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(bottom = 2.dp)
                                    )
                                }
                                Text(
                                    text = "${if (isEn) rec.timing.enLabel else rec.timing.bmLabel} • ${rec.dateLabel}, ${rec.timeLabel}",
                                    color = Color(0xFF475569),
                                    fontSize = 11.sp,
                                    maxLines = 1
                                )
                                if (rec.note.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Catatan: ${rec.note}",
                                        color = BlueDark,
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Medium,
                                        maxLines = 2
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Column(horizontalAlignment = Alignment.End) {
                            ClinicalSeverityBadge(severity = rec.severity)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(BlueVeryLight)
                                        .clickable { editingGlucoseRecord = rec }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = if (isEn) "✎ Edit" else "✎ Kemaskini",
                                        color = BlueDark,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFFEE2E2))
                                        .clickable { MockDataRepository.deleteGlucoseRecord(rec.id) }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = if (isEn) "🗑 Delete" else "🗑 Padam",
                                        color = Color(0xFFDC2626),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } else {
            if (MockDataRepository.bpRecords.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 6.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(BlueSurface)
                            .border(1.dp, Color(0xFFBFDBFE), RoundedCornerShape(16.dp))
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (isEn) "No Blood Pressure Records Yet" else "Tiada Rekod Tekanan Darah Lagi",
                                color = Color(0xFF0F172A),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isEn) "Your newly created account starts empty. Enter a blood pressure reading above and tap 'Save Blood Pressure Record'." else "Akaun baharu anda bermula kosong. Masukkan bacaan tekanan darah di atas dan tekan 'Simpan Rekod Tekanan Darah'.",
                                color = Color(0xFF475569),
                                fontSize = 11.5.sp
                            )
                        }
                    }
                }
            }
            items(MockDataRepository.bpRecords) { bp ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 5.dp)
                        .shadow(2.dp, RoundedCornerShape(16.dp), spotColor = BlueDark.copy(alpha = 0.05f))
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceLight)
                        .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (bp.severity == HealthSeverity.HIGH) StatusAlertBg else Color(0xFFFEE2E2)),
                                contentAlignment = Alignment.Center
                            ) {
                                ModernPulseHeartIcon(size = 20.dp, tint = Color(0xFFDC2626))
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = "${bp.systolic} / ${bp.diastolic}",
                                        color = Color(0xFF0F172A),
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "mmHg",
                                        color = Color(0xFF64748B),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(bottom = 2.dp)
                                    )
                                }
                                Text(
                                    text = "${if (isEn) "Pulse: " else "Nadi: "}${bp.pulse} bpm • ${bp.dateLabel}, ${bp.timeLabel}",
                                    color = Color(0xFF475569),
                                    fontSize = 11.sp,
                                    maxLines = 1
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Column(horizontalAlignment = Alignment.End) {
                            ClinicalSeverityBadge(severity = bp.severity)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(BlueVeryLight)
                                        .clickable { editingBpRecord = bp }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = if (isEn) "✎ Edit" else "✎ Kemaskini",
                                        color = BlueDark,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFFFEE2E2))
                                        .clickable { MockDataRepository.deleteBpRecord(bp.id) }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = if (isEn) "🗑 Delete" else "🗑 Padam",
                                        color = Color(0xFFDC2626),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // ==================== EDIT GLUCOSE RECORD MODAL ====================
    editingGlucoseRecord?.let { rec ->
        var editVal by remember(rec.id) { mutableStateOf(String.format(Locale.US, "%.2f", rec.valueMmol)) }
        var editTiming by remember(rec.id) { mutableStateOf(rec.timing) }
        var editNote by remember(rec.id) { mutableStateOf(rec.note) }

        Dialog(onDismissRequest = { editingGlucoseRecord = null }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(SurfaceLight)
                    .padding(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = if (isEn) "Update Glucose Record" else "Kemaskini Rekod Glukosa",
                        color = Color(0xFF0F172A),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    OutlinedTextField(
                        value = editVal,
                        onValueChange = { editVal = it },
                        label = { Text(if (isEn) "Glucose Value (mmol/L)" else "Nilai Glukosa (mmol/L)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = editNote,
                        onValueChange = { editNote = it },
                        label = { Text(if (isEn) "Notes / Catatan" else "Catatan Tambahan") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF1F5F9))
                                .clickable { editingGlucoseRecord = null }
                                .padding(vertical = 11.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(if (isEn) "Cancel" else "Batal", color = Color(0xFF64748B), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Box(
                            modifier = Modifier
                                .weight(1.4f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(BluePrimary)
                                .clickable {
                                    val parsed = editVal.toDoubleOrNull()
                                    if (parsed == null) {
                                        NotificationService.showError(
                                            if (isEn) "Update Failed: Invalid glucose value!"
                                            else "Kemaskini Tidak Berjaya: Nilai glukosa tidak sah!"
                                        )
                                    } else {
                                        if (MockDataRepository.updateGlucoseRecord(rec.id, parsed, editTiming, editNote)) {
                                            editingGlucoseRecord = null
                                        }
                                    }
                                }
                                .padding(vertical = 11.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(if (isEn) "Save Update" else "Simpan Kemaskini", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // ==================== EDIT BLOOD PRESSURE RECORD MODAL ====================
    editingBpRecord?.let { bp ->
        var editSys by remember(bp.id) { mutableStateOf(bp.systolic.toString()) }
        var editDia by remember(bp.id) { mutableStateOf(bp.diastolic.toString()) }
        var editPulse by remember(bp.id) { mutableStateOf(bp.pulse.toString()) }

        Dialog(onDismissRequest = { editingBpRecord = null }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(SurfaceLight)
                    .padding(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = if (isEn) "Update Blood Pressure Record" else "Kemaskini Rekod Tekanan Darah",
                        color = Color(0xFF0F172A),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = editSys,
                            onValueChange = { editSys = it },
                            label = { Text(if (isEn) "Systolic" else "Sistolik") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = editDia,
                            onValueChange = { editDia = it },
                            label = { Text(if (isEn) "Diastolic" else "Diastolik") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                    OutlinedTextField(
                        value = editPulse,
                        onValueChange = { editPulse = it },
                        label = { Text(if (isEn) "Pulse (bpm)" else "Nadi (bpm)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF1F5F9))
                                .clickable { editingBpRecord = null }
                                .padding(vertical = 11.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(if (isEn) "Cancel" else "Batal", color = Color(0xFF64748B), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Box(
                            modifier = Modifier
                                .weight(1.4f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(BluePrimary)
                                .clickable {
                                    val s = editSys.toIntOrNull()
                                    val d = editDia.toIntOrNull()
                                    val p = editPulse.toIntOrNull() ?: 72
                                    if (s == null || d == null) {
                                        NotificationService.showError(
                                            if (isEn) "Update Failed: Please enter valid numbers!"
                                            else "Kemaskini Tidak Berjaya: Sila masukkan nombor yang sah!"
                                        )
                                    } else {
                                        if (MockDataRepository.updateBpRecord(bp.id, s, d, p)) {
                                            editingBpRecord = null
                                        }
                                    }
                                }
                                .padding(vertical = 11.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(if (isEn) "Save Update" else "Simpan Kemaskini", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
