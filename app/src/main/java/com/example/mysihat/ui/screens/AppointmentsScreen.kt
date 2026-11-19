package com.example.mysihat.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.mysihat.data.*
import com.example.mysihat.ui.components.*
import com.example.mysihat.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun AppointmentsScreen() {
    val context = LocalContext.current
    val isEn = LanguageManager.isEnglish
    var showSetModal by remember { mutableStateOf(false) }
    var showQrScannerModal by remember { mutableStateOf(false) }

    val appointment = MockDataRepository.nextAppointment.value

    // Form inputs for Reschedule / Set KKM Appointment (No doctor input!)
    var facilityInput by remember { mutableStateOf(appointment.facilityName) }
    var clinicTypeInput by remember { mutableStateOf(appointment.clinicType) }
    var dateInput by remember { mutableStateOf(appointment.dateDisplay) }
    var timeSlotInput by remember { mutableStateOf(appointment.timeDisplay.ifBlank { "09:00 AM" }) }
    var sessionInput by remember { mutableStateOf(appointment.sessionLabel) }

    LaunchedEffect(showSetModal) {
        if (showSetModal) {
            facilityInput = appointment.facilityName
            clinicTypeInput = appointment.clinicType
            dateInput = appointment.dateDisplay
            timeSlotInput = appointment.timeDisplay.ifBlank { "09:00 AM" }
            sessionInput = appointment.sessionLabel
        }
    }

    // Real-time live countdown ticker
    var currentTimeMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(Unit) {
        while (true) {
            currentTimeMillis = System.currentTimeMillis()
            delay(1000)
        }
    }

    val remainingMillis = (appointment.targetTimeMillis - currentTimeMillis).coerceAtLeast(0L)
    val remDays = remainingMillis / (24 * 3600 * 1000)
    val remHours = (remainingMillis % (24 * 3600 * 1000)) / (3600 * 1000)
    val remMins = (remainingMillis % (3600 * 1000)) / (60 * 1000)
    val remSecs = (remainingMillis % (60 * 1000)) / 1000

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Safe Top Bar Below Status Bar
        item {
            MySihatTopBar(
                screenTitle = if (isEn) "Appointments" else "Temujanji Rawatan",
                subtitle = if (isEn) "Clinic Treatment Schedule & Visit Check-in" else "Jadual Temujanji Rawatan & Semakan Kehadiran",
                action = {
                    // Schedule / Update Appointment Button (No "+" prefix!)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White)
                            .bounceClick { showSetModal = true }
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = if (isEn) "Schedule" else "Jadualkan",
                            color = BlueDark,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            softWrap = false,
                            maxLines = 1
                        )
                    }
                }
            )
        }

        // Live Countdown Card (Next KKM Government Healthcare Appointment)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .shadow(10.dp, RoundedCornerShape(22.dp), spotColor = BlueDark.copy(alpha = 0.2f))
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF0D47A1), Color(0xFF1976D2), Color(0xFF1E88E5))
                        )
                    )
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            ModernCalendarCheckIcon(size = 18.dp, tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isEn) "UPCOMING CLINIC APPOINTMENT" else "TEMUJANJI RAWATAN KLINIK",
                            color = Color.White,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                            maxLines = 1
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Real-Time Countdown Timer Block
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Black.copy(alpha = 0.25f))
                            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                            .padding(vertical = 12.dp, horizontal = 16.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isEn) "TIME UNTIL CLINIC VISIT (COUNTDOWN):" else "KIRA DETIK TEMUJANJI RAWATAN:",
                                    color = Color(0xFFBBDEFB),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "🔴 LIVE TICKER",
                                    color = Color(0xFF69F0AE),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            val hasApt = MockDataRepository.hasScheduledAppointment.value
                            if (!hasApt) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color.White.copy(alpha = 0.14f))
                                        .clickable { showSetModal = true }
                                        .padding(vertical = 12.dp, horizontal = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (isEn) "No Appointment Scheduled Yet • Tap 'Schedule' to set date & clinic slot" else "Tiada Temujanji Dijadualkan • Sentuh di sini atau 'Jadualkan' untuk pilih tarikh & slot",
                                        color = Color.White,
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            } else if (remainingMillis <= 0L) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFF059669))
                                        .padding(vertical = 10.dp, horizontal = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = if (isEn) "🎉 TODAY IS YOUR APPOINTMENT! Please scan QR at counter" else "🎉 HARI INI TEMUJANJI ANDA! Sila imbas Kod QR di Kaunter Fasiliti",
                                        color = Color.White,
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            } else {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceAround,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    listOf(
                                        Pair("%02d".format(remDays), if (isEn) "DAYS" else "HARI"),
                                        Pair("%02d".format(remHours), if (isEn) "HOURS" else "JAM"),
                                        Pair("%02d".format(remMins), if (isEn) "MINS" else "MINIT"),
                                        Pair("%02d".format(remSecs), if (isEn) "SECS" else "SAAT")
                                    ).forEachIndexed { index, (value, unit) ->
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = value,
                                                color = Color.White,
                                                fontSize = 24.sp,
                                                fontWeight = FontWeight.ExtraBold
                                            )
                                            Text(
                                                text = unit,
                                                color = Color(0xFF90CAF9),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        if (index < 3) {
                                            Text(
                                                text = ":",
                                                color = Color.White.copy(alpha = 0.6f),
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(bottom = 12.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    val hasAptInfo = MockDataRepository.hasScheduledAppointment.value
                    // KKM Healthcare Details (No Doctor Name)
                    Text(
                        text = if (hasAptInfo) appointment.clinicType else (if (isEn) "No Active Clinic Appointment" else "Tiada Temujanji Rawatan Aktif"),
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = if (hasAptInfo) appointment.facilityName else (if (isEn) "Tap 'Schedule' at top right to select your clinic & date" else "Tekan butang 'Jadualkan' di penjuru kanan atas untuk memilih klinik & tarikh"),
                        color = Color(0xFFE3F2FD),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.White.copy(alpha = 0.22f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${appointment.dateDisplay}  •  ${appointment.sessionLabel}",
                                color = Color(0xFFFFE082),
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // QR Check-In Action Button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.White)
                            .bounceClick { showQrScannerModal = true }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            ModernQrPassIcon(size = 18.dp, tint = BlueDark)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (appointment.isCheckedIn) {
                                    if (isEn) "Show Clinic Attendance Slip" else "Papar Slip Kehadiran Klinik"
                                } else {
                                    if (isEn) "Check-in / Scan at Clinic Counter" else "Daftar Masuk / Imbas di Kaunter Klinik"
                                },
                                color = BlueDark,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }
        }

        // CPG Clinical Instructions Before Government Clinic Visit
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .shadow(4.dp, RoundedCornerShape(18.dp), spotColor = BlueDark.copy(alpha = 0.06f))
                    .clip(RoundedCornerShape(18.dp))
                    .background(SurfaceLight)
                    .border(1.dp, CardBorder, RoundedCornerShape(18.dp))
                    .padding(18.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        ModernShieldVerifiedIcon(size = 18.dp, tint = StatusLowRisk)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isEn) "CLINIC PRE-VISIT GUIDELINES" else "PANDUAN SEBELUM RAWATAN KLINIK",
                            color = Color(0xFF0F172A),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val instructions = if (isEn) listOf(
                        "1. Present your 7-day glucose & BP digital log or generated PDF report at registration.",
                        "2. Fast for a minimum of 8 hours if scheduled for fasting blood glucose or lipid panel.",
                        "3. Arrive 15 minutes prior to session start and scan the clinic QR code at the counter."
                    ) else listOf(
                        "1. Tunjukkan rekod pemantauan glukosa 7 hari atau laporan PDF MySihat semasa pendaftaran.",
                        "2. Berpuasa sekurang-kurangnya 8 jam jika dijadualkan ujian darah puasa atau ujian kolesterol.",
                        "3. Hadir 15 minit lebih awal dan imbas Kod QR MySejahtera di pintu masuk kaunter saringan."
                    )

                    instructions.forEach { line ->
                        Text(
                            text = line,
                            color = Color(0xFF1E293B), // Black/Dark Slate Font
                            fontSize = 12.sp,
                            modifier = Modifier.padding(vertical = 3.dp)
                        )
                    }
                }
            }
        }
    }

    // ==================== SCHEDULE KKM APPOINTMENT MODAL ====================
    if (showSetModal) {
        Dialog(
            onDismissRequest = { showSetModal = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .fillMaxHeight(0.90f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(SurfaceLight)
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Modal Header
                    Column {
                        Text(
                            text = if (isEn) "Schedule Clinic Appointment" else "Jadualkan Temujanji Rawatan",
                            color = Color(0xFF0F172A),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isEn) "Select public healthcare facility, date & clinic time slot." else "Pilih fasiliti kesihatan awam, tarikh & slot masa klinik.",
                            color = Color(0xFF64748B),
                            fontSize = 11.5.sp
                        )
                    }

                    // 1. Facility Input with Quick Suggestion Chips
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = if (isEn) "Public Healthcare Facility:" else "Fasiliti Kesihatan Awam:",
                            color = Color(0xFF0F172A),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        OutlinedTextField(
                            value = facilityInput,
                            onValueChange = { facilityInput = it },
                            placeholder = { Text("cth: Hospital Sultan Abdul Halim (HSAH)", color = Color(0xFF94A3B8), fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BluePrimary,
                                focusedTextColor = Color(0xFF0F172A),
                                unfocusedTextColor = Color(0xFF0F172A),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color(0xFFF8FAFC)
                            )
                        )

                        // Quick Facility Shortcut Chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                "HSAH Sungai Petani" to "Hospital Sultan Abdul Halim (HSAH Sungai Petani)",
                                "KK Bandar SP" to "Klinik Kesihatan Bandar Sungai Petani",
                                "Hospital Tapah" to "Hospital Tapah",
                                "HKL" to "Hospital Kuala Lumpur (HKL)"
                            ).forEach { (shortLabel, fullFacility) ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (facilityInput == fullFacility) BlueVeryLight else Color(0xFFF1F5F9))
                                        .border(1.dp, if (facilityInput == fullFacility) BluePrimary else CardBorder, RoundedCornerShape(8.dp))
                                        .clickable { facilityInput = fullFacility }
                                        .padding(horizontal = 4.dp, vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = shortLabel,
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (facilityInput == fullFacility) BlueDark else Color(0xFF334155),
                                        maxLines = 1,
                                        softWrap = false
                                    )
                                }
                            }
                        }
                    }

                    // 2. Treatment Type Options
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = if (isEn) "Treatment Service Type:" else "Jenis Perkhidmatan Rawatan:",
                            color = Color(0xFF0F172A),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        listOf(
                            "Klinik Rawatan Diabetes & NCD",
                            "Klinik Pakar Endokrinologi & Insulin",
                            "Saringan Retinopati & Mata Diabetik",
                            "Saringan Penjagaan Kaki Diabetik & Luka",
                            "Ujian Makmal Darah & Profil Lipid Awam"
                        ).forEach { clinicType ->
                            val isSel = clinicTypeInput == clinicType
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSel) BlueVeryLight else Color(0xFFF8FAFC))
                                    .border(1.dp, if (isSel) BluePrimary else CardBorder, RoundedCornerShape(10.dp))
                                    .clickable { clinicTypeInput = clinicType }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = clinicType,
                                    color = if (isSel) BlueDark else Color(0xFF0F172A),
                                    fontSize = 11.5.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    // 3. Editable Date Input with Calendar Picker Icon + Quick Date Presets
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = if (isEn) "Appointment Date:" else "Tarikh Temujanji:",
                            color = Color(0xFF0F172A),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )

                        OutlinedTextField(
                            value = dateInput,
                            onValueChange = { dateInput = it },
                            placeholder = { Text("19/11 atau 19 Nov 2026", color = Color(0xFF94A3B8), fontSize = 12.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            trailingIcon = {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(BlueVeryLight)
                                        .clickable {
                                            showAndroidDatePicker(context, dateInput) { selectedDate ->
                                                dateInput = selectedDate
                                            }
                                        }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(text = "📅 Kalendar", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BlueDark)
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BluePrimary,
                                focusedTextColor = Color(0xFF0F172A),
                                unfocusedTextColor = Color(0xFF0F172A),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color(0xFFF8FAFC)
                            )
                        )

                        // Quick Date Preset Chips (Auto Countdown)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                "Esok (19/11)" to "19/11",
                                "Lusa (20/11)" to "20/11",
                                "3 Hari Lagi" to "21/11",
                                "1 Minggu" to "25/11"
                            ).forEach { (label, presetVal) ->
                                val isPresetSel = dateInput.startsWith(presetVal) || dateInput.contains(presetVal)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isPresetSel) BlueDark else Color(0xFFF1F5F9))
                                        .border(1.dp, if (isPresetSel) BlueDark else CardBorder, RoundedCornerShape(8.dp))
                                        .clickable { dateInput = presetVal }
                                        .padding(vertical = 7.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isPresetSel) Color.White else Color(0xFF0F172A),
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }

                    // 4. Slot Klinik (8, 9, 10, 11, 12, 14, 15, 16 am/pm)
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isEn) "Clinic Time Slot (Hourly):" else "Slot Masa Klinik (Jam):",
                                color = Color(0xFF0F172A),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = timeSlotInput,
                                color = BlueDark,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        // Sesi Pagi Slots (8:00 AM, 9:00 AM, 10:00 AM, 11:00 AM)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("08:00 AM", "09:00 AM", "10:00 AM", "11:00 AM").forEach { slot ->
                                val isSlotSel = timeSlotInput.equals(slot, ignoreCase = true)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSlotSel) BlueDark else Color(0xFFF1F5F9))
                                        .border(1.dp, if (isSlotSel) BlueDark else CardBorder, RoundedCornerShape(8.dp))
                                        .clickable {
                                            timeSlotInput = slot
                                            sessionInput = "Sesi Pagi ($slot)"
                                        }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = slot,
                                        fontSize = 10.5.sp,
                                        fontWeight = if (isSlotSel) FontWeight.ExtraBold else FontWeight.SemiBold,
                                        color = if (isSlotSel) Color.White else Color(0xFF0F172A),
                                        maxLines = 1
                                    )
                                }
                            }
                        }

                        // Sesi Tengah Hari / Petang Slots (12:00 PM, 02:00 PM, 03:00 PM, 04:00 PM)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("12:00 PM", "02:00 PM", "03:00 PM", "04:00 PM").forEach { slot ->
                                val isSlotSel = timeSlotInput.equals(slot, ignoreCase = true)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSlotSel) BlueDark else Color(0xFFF1F5F9))
                                        .border(1.dp, if (isSlotSel) BlueDark else CardBorder, RoundedCornerShape(8.dp))
                                        .clickable {
                                            timeSlotInput = slot
                                            sessionInput = if (slot.startsWith("12")) "Sesi Tengah Hari ($slot)" else "Sesi Petang ($slot)"
                                        }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = slot,
                                        fontSize = 10.5.sp,
                                        fontWeight = if (isSlotSel) FontWeight.ExtraBold else FontWeight.SemiBold,
                                        color = if (isSlotSel) Color.White else Color(0xFF0F172A),
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Dialog Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFF1F5F9))
                                .clickable { showSetModal = false }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(if (isEn) "Cancel" else "Batal", color = Color(0xFF64748B), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        Box(
                            modifier = Modifier
                                .weight(1.6f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFF0D47A1), Color(0xFF1976D2))
                                    )
                                )
                                .clickable {
                                    MockDataRepository.updateAppointment(
                                        facility = facilityInput,
                                        clinicType = clinicTypeInput,
                                        date = dateInput,
                                        time = timeSlotInput,
                                        session = if (sessionInput.isNotBlank()) sessionInput else "Slot $timeSlotInput"
                                    )
                                    showSetModal = false
                                }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isEn) "Save & Start Countdown" else "Simpan & Kira Detik",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }

    // ==================== QR CHECK-IN SCANNER SIMULATION MODAL ====================
    if (showQrScannerModal) {
        Dialog(onDismissRequest = { showQrScannerModal = false }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(26.dp))
                    .background(Color(0xFF0F172A))
                    .padding(24.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (isEn) "Clinic QR Check-in" else "Imbasan QR Kehadiran Kaunter",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isEn) "Point camera to the MySejahtera counter QR code" else "Arahkan kamera ke Kod QR MySejahtera di Kaunter",
                        color = Color(0xFF94A3B8),
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Viewfinder with animated laser line
                    Box(
                        modifier = Modifier
                            .size(200.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.Black)
                            .border(2.dp, Color(0xFF00B4D8), RoundedCornerShape(20.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        ModernQrPassIcon(size = 120.dp, tint = Color.White.copy(alpha = 0.4f))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.85f)
                                .height(2.5.dp)
                                .background(Color(0xFF00E5FF))
                        )
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(Color(0xFF0077B6), Color(0xFF00B4D8))
                                )
                            )
                            .bounceClick {
                                MockDataRepository.markCheckInSuccess()
                                showQrScannerModal = false
                            }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isEn) "Confirm Attendance Now" else "Sahkan Kehadiran Sekarang",
                            color = Color.White,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
