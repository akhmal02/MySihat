package com.example.mysihat.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mysihat.data.MockDataRepository
import com.example.mysihat.ui.components.ModernPillCapsuleIcon
import com.example.mysihat.ui.components.ModernShieldVerifiedIcon
import com.example.mysihat.ui.components.MySihatTopBar
import com.example.mysihat.ui.theme.*

@Composable
fun RecordsScreen() {
    var selectedSection by remember { mutableIntStateOf(0) } // 0: Prescriptions, 1: Lab Reports

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Safe Top Bar Below Status Bar
        item {
            MySihatTopBar(
                screenTitle = "Preskripsi & Rekod",
                subtitle = "Sistem Rekod Perubatan & Preskripsi Digital"
            )
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                // Tab selector
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(16.dp), spotColor = BlueDark.copy(alpha = 0.08f))
                        .clip(RoundedCornerShape(16.dp))
                        .background(SurfaceLight)
                        .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
                        .padding(4.dp)
                ) {
                    listOf("Jadual Ubat Harian", "Keputusan Ujian Makmal").forEachIndexed { index, title ->
                        val isSelected = selectedSection == index
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) BluePrimary else Color.Transparent)
                                .clickable { selectedSection = index }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = title,
                                color = if (isSelected) Color.White else TextDark,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        if (selectedSection == 0) {
            // Medication Section
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "Today's Daily Dosage Schedule",
                        color = TextDark,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Tap checkmark when dose is consumed",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
            }

            items(MockDataRepository.medications) { med ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                        .shadow(3.dp, RoundedCornerShape(18.dp), spotColor = BlueDark.copy(alpha = 0.06f))
                        .clip(RoundedCornerShape(18.dp))
                        .background(SurfaceLight)
                        .border(1.dp, CardBorder, RoundedCornerShape(18.dp))
                        .padding(16.dp)
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
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (med.isTakenToday) StatusLowRiskBg else BlueVeryLight),
                                contentAlignment = Alignment.Center
                            ) {
                                ModernPillCapsuleIcon(
                                    size = 22.dp,
                                    tint = if (med.isTakenToday) StatusLowRisk else BluePrimary
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "${med.name} ${med.dosage}",
                                    color = TextDark,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = med.frequency,
                                    color = TextMuted,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "ℹ ${med.instruction}",
                                    color = BluePrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // Take Dose Checkbox Button
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (med.isTakenToday) StatusLowRisk else BlueVeryLight)
                                .clickable { MockDataRepository.toggleMedicationTaken(med.id) }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = if (med.isTakenToday) "✓ Taken" else "Mark Done",
                                color = if (med.isTakenToday) Color.White else BlueDark,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        } else {
            // Lab Reports List
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = "Laboratory Test Results",
                        color = TextDark,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            val labReports = listOf(
                Triple("Diagnostic HbA1c & Glycemic Control", "Makmal Kesihatan Putrajaya • 14 Oct 2026", "6.1% (Good Control)"),
                Triple("Renal Function & Serum Creatinine", "Hospital Kuala Lumpur • 22 Sep 2026", "eGFR: 98 mL/min (Optimal)"),
                Triple("Fasting Lipid Profile (Cholesterol)", "Klinik Kesihatan P9 • 15 Aug 2026", "Total: 4.1 mmol/L (Normal)")
            )

            items(labReports) { report ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                        .shadow(3.dp, RoundedCornerShape(18.dp), spotColor = BlueDark.copy(alpha = 0.06f))
                        .clip(RoundedCornerShape(18.dp))
                        .background(SurfaceLight)
                        .border(1.dp, CardBorder, RoundedCornerShape(18.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                ModernShieldVerifiedIcon(size = 18.dp, tint = StatusLowRisk)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "OFFICIAL LAB RESULT",
                                    color = StatusLowRisk,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                            }
                            Text(
                                text = "PDF Download ⤓",
                                color = BluePrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = report.first,
                            color = TextDark,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = report.second,
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(BlueSurface)
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Result: ${report.third}",
                                color = BlueDark,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}
