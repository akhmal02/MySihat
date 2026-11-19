package com.example.mysihat.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mysihat.data.*
import com.example.mysihat.ui.components.*
import com.example.mysihat.ui.theme.*
import java.util.Locale

@Composable
fun DashboardScreen(
    onNavigateToTab: (Int) -> Unit,
    onShowQrDialog: () -> Unit,
    onShowQuickLogDialog: () -> Unit
) {
    val context = LocalContext.current
    val isEn = LanguageManager.isEnglish
    val user = MockDataRepository.currentUser.value
    val latestGlucose = MockDataRepository.glucoseRecords.firstOrNull()
    val latestBp = MockDataRepository.bpRecords.firstOrNull()
    val nextAppointment = MockDataRepository.nextAppointment.value

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Safe Top Bar Below Status Bar
        item {
            MySihatTopBar(
                screenTitle = if (isEn) "Home" else "Utama",
                subtitle = if (isEn) "Personal Digital Health Portal" else "Portal Pemantauan Kesihatan & Diabetes",
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

        // Flagship MySejahtera Digital Pass Hero Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                MySejahteraHealthCard(
                    user = user,
                    onQrClick = onShowQrDialog
                )
            }
        }

        // Quick Log Reading Floating Banner (No "+" prefix!)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(6.dp, RoundedCornerShape(18.dp), spotColor = BluePrimary.copy(alpha = 0.2f))
                        .clip(RoundedCornerShape(18.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF0D47A1), Color(0xFF1976D2), Color(0xFF00B4D8))
                            )
                        )
                        .bounceClick { onShowQuickLogDialog() }
                        .padding(horizontal = 18.dp, vertical = 13.dp)
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
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.22f)),
                                contentAlignment = Alignment.Center
                            ) {
                                ModernGlucoseDropletIcon(size = 20.dp, tint = Color.White)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (isEn) "Record Glucose & BP Reading" else "Catat Bacaan Glukosa & BP",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                                Text(
                                    text = if (isEn) "Record pre-meal or 2 hours post-meal reading" else "Catat bacaan sebelum atau selepas 2 jam makan",
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 11.sp,
                                    maxLines = 1
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isEn) "Log →" else "Catat →",
                            color = Color(0xFFBBDEFB),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            softWrap = false
                        )
                    }
                }
            }
        }

        // Standard Range Legend Box (3 Indicators Only)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
            ) {
                RangeLegendBox()
            }
        }

        // Latest Vitals Snapshot Row
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isEn) "Health Vitals (Today)" else "Status Kesihatan (Hari Ini)",
                        color = Color(0xFF0F172A), // Black Font
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Text(
                        text = if (isEn) "Open Vitals Hub →" else "Buka Hab Vitals →",
                        color = BluePrimary,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.bounceClick { onNavigateToTab(1) }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Blood Glucose Card (formatted with 2 decimal places)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .shadow(3.dp, RoundedCornerShape(18.dp), spotColor = BlueDark.copy(alpha = 0.06f))
                            .clip(RoundedCornerShape(18.dp))
                            .background(SurfaceLight)
                            .border(1.dp, CardBorder, RoundedCornerShape(18.dp))
                            .bounceClick { onNavigateToTab(1) }
                            .padding(14.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ModernGlucoseDropletIcon(size = 20.dp, tint = BluePrimary)
                                if (latestGlucose != null) {
                                    ClinicalSeverityBadge(severity = latestGlucose.severity)
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = if (latestGlucose != null) String.format(Locale.US, "%.2f", latestGlucose.valueMmol) else "--.--",
                                color = if (latestGlucose?.severity == HealthSeverity.HIGH) StatusAlert else Color(0xFF0F172A),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = if (latestGlucose != null) {
                                    "mmol/L • ${if (isEn) latestGlucose.timing.enLabel else latestGlucose.timing.bmLabel}"
                                } else {
                                    if (isEn) "No record yet • Tap to add" else "Tiada rekod • Sentuh tambah"
                                },
                                color = Color(0xFF475569),
                                fontSize = 10.5.sp,
                                maxLines = 1
                            )
                        }
                    }

                    // Blood Pressure Card with EXACT Systolic, Diastolic & Pulse
                    Box(
                        modifier = Modifier
                            .weight(1.3f)
                            .shadow(3.dp, RoundedCornerShape(18.dp), spotColor = BlueDark.copy(alpha = 0.06f))
                            .clip(RoundedCornerShape(18.dp))
                            .background(SurfaceLight)
                            .border(1.dp, CardBorder, RoundedCornerShape(18.dp))
                            .bounceClick { onNavigateToTab(1) }
                            .padding(14.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                ModernPulseHeartIcon(size = 20.dp, tint = Color(0xFFDC2626))
                                if (latestBp != null) {
                                    ClinicalSeverityBadge(severity = latestBp.severity)
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = if (latestBp != null) "${latestBp.systolic}/${latestBp.diastolic}" else "--/--",
                                    color = Color(0xFF0F172A),
                                    fontSize = 20.sp,
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
                                text = if (latestBp != null) {
                                    "${if (isEn) "Pulse: " else "Nadi: "}${latestBp.pulse} bpm (${latestBp.category})"
                                } else {
                                    if (isEn) "No BP record yet • Tap to add" else "Tiada rekod tekanan darah"
                                },
                                color = if (latestBp != null) StatusLowRisk else Color(0xFF64748B),
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        // Next Appointment Reminder with KKM Government Facility
        item {
            val hasApt = MockDataRepository.hasScheduledAppointment.value
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(18.dp), spotColor = BlueDark.copy(alpha = 0.08f))
                        .clip(RoundedCornerShape(18.dp))
                        .background(BlueSurface)
                        .border(1.dp, Color(0xFFBFDBFE), RoundedCornerShape(18.dp))
                        .bounceClick { onNavigateToTab(3) }
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ModernCalendarCheckIcon(size = 18.dp, tint = BlueDark)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isEn) "NEXT CLINIC APPOINTMENT" else "TEMUJANJI RAWATAN SETERUSNYA",
                                color = BlueDark,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (hasApt) {
                            Text(
                                text = nextAppointment.clinicType,
                                color = Color(0xFF0F172A),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${nextAppointment.facilityName} • ${nextAppointment.dateDisplay} (${nextAppointment.sessionLabel})",
                                color = Color(0xFF475569),
                                fontSize = 11.5.sp
                            )
                        } else {
                            Text(
                                text = if (isEn) "No Appointment Scheduled Yet" else "Tiada Temujanji Dijadualkan",
                                color = Color(0xFF0F172A),
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isEn) "Tap here to schedule your clinic or hospital appointment →" else "Sentuh di sini untuk jadualkan temujanji klinik atau hospital →",
                                color = BluePrimary,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        // Quick Navigation Services Grid
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Text(
                    text = if (isEn) "MySihat Key Services" else "Perkhidmatan MySihat",
                    color = Color(0xFF0F172A), // Black Font
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickServiceItem(
                        title = if (isEn) "Clinics & Map" else "Peta Klinik & Hospital",
                        subtitle = if (isEn) "GPS & Directions" else "Peta GPS & Pandu Arah",
                        icon = { ModernRadarMapIcon(size = 20.dp, tint = BluePrimary) },
                        onClick = { onNavigateToTab(2) },
                        modifier = Modifier.weight(1f)
                    )
                    QuickServiceItem(
                        title = if (isEn) "Appointments" else "Temujanji Rawatan",
                        subtitle = if (isEn) "Countdown & QR Scan" else "Countdown & imbasan QR",
                        icon = { ModernCalendarCheckIcon(size = 20.dp, tint = Color(0xFF0284C7)) },
                        onClick = { onNavigateToTab(3) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickServiceItem(
                        title = if (isEn) "Patient Vitals Hub" else "Hab Vitals Pesakit",
                        subtitle = if (isEn) "9 Timings & BP Log" else "Glukosa 9 masa & BP",
                        icon = { ModernGlucoseDropletIcon(size = 20.dp, tint = Color(0xFF00B4D8)) },
                        onClick = { onNavigateToTab(1) },
                        modifier = Modifier.weight(1f)
                    )
                    QuickServiceItem(
                        title = if (isEn) "Profile & Kin" else "Profil & Waris",
                        subtitle = if (isEn) "Biometrics & Emergency" else "Maklumat & no. kecemasan",
                        icon = { ModernShieldVerifiedIcon(size = 20.dp, tint = StatusLowRisk) },
                        onClick = { onNavigateToTab(4) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}
