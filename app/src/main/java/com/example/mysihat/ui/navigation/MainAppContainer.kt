package com.example.mysihat.ui.navigation

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
import com.example.mysihat.data.*
import com.example.mysihat.ui.components.*
import com.example.mysihat.ui.screens.*
import com.example.mysihat.ui.theme.*

@Composable
fun MainAppContainer() {
    val context = LocalContext.current
    val isEn = LanguageManager.isEnglish
    var isSplashDone by remember { mutableStateOf(false) }
    val isLoggedIn = MockDataRepository.isLoggedIn.value
    var selectedTab by remember { mutableIntStateOf(0) }
    var showQrModal by remember { mutableStateOf(false) }
    var showQuickLogModal by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        if (!isSplashDone) {
            SplashScreen(onFinished = { isSplashDone = true })
        } else if (!isLoggedIn) {
            // Show Login / Register Screen First for Patient
            AuthScreen(
                onLoginSuccess = {
                    // Patient logged in
                }
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundLight)
            ) {
                // Main Active Screen Content
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 68.dp)
                ) {
                    when (selectedTab) {
                        0 -> DashboardScreen(
                            onNavigateToTab = { selectedTab = it },
                            onShowQrDialog = { showQrModal = true },
                            onShowQuickLogDialog = { showQuickLogModal = true }
                        )
                        1 -> VitalsScreen()
                        2 -> MapScreen()
                        3 -> AppointmentsScreen()
                        4 -> ProfileScreen(
                            onShowQrDialog = { showQrModal = true },
                            onLogout = { MockDataRepository.logout() }
                        )
                    }
                }

                // Floating Modern Bottom Navigation Bar
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(16.dp, RoundedCornerShape(26.dp), spotColor = BlueDark.copy(alpha = 0.25f))
                        .clip(RoundedCornerShape(26.dp))
                        .background(SurfaceLight)
                        .border(1.dp, CardBorder, RoundedCornerShape(26.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val tabLabels = if (isEn) {
                            listOf("Home", "Vitals", "Map", "Queue", "Profile")
                        } else {
                            listOf("Utama", "Vitals", "Peta", "Temujanji", "Profil")
                        }

                        tabLabels.forEachIndexed { index, label ->
                            val isSelected = selectedTab == index
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(if (isSelected) BlueVeryLight else Color.Transparent)
                                    .bounceClick { selectedTab = index }
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    val iconTint = if (isSelected) BluePrimary else TextSubtle
                                    when (index) {
                                        0 -> ModernHomeIcon(size = 20.dp, tint = iconTint)
                                        1 -> ModernGlucoseDropletIcon(size = 20.dp, tint = iconTint)
                                        2 -> ModernRadarMapIcon(size = 20.dp, tint = iconTint)
                                        3 -> ModernCalendarCheckIcon(size = 20.dp, tint = iconTint)
                                        4 -> ModernShieldVerifiedIcon(size = 20.dp, tint = iconTint)
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = label,
                                        color = if (isSelected) BlueDark else TextSubtle,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // QR Pass Enlarge Dialog Modal
            if (showQrModal) {
                Dialog(onDismissRequest = { showQrModal = false }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(26.dp))
                            .background(SurfaceLight)
                            .padding(24.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            MySihatAppLogo(size = 48.dp, shadowElevation = 6.dp)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = if (isEn) "MySihat Digital Health Pass" else "Kad Kesihatan Digital MySihat",
                                color = Color(0xFF0F172A),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isEn) "Patient Digital Health Pass" else "Pas Kesihatan Digital Pesakit",
                                color = Color(0xFF64748B),
                                fontSize = 12.sp
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Box(
                                modifier = Modifier
                                    .size(200.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(BlueSurface)
                                    .border(2.dp, BluePrimary, RoundedCornerShape(20.dp))
                                    .padding(20.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                ModernQrPassIcon(size = 160.dp, tint = BlueDark)
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            Text(
                                text = MockDataRepository.currentUser.value.name,
                                color = Color(0xFF0F172A),
                                fontSize = 16.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "MyKad: ${MockDataRepository.currentUser.value.myKad}",
                                color = Color(0xFF475569),
                                fontSize = 13.sp
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(StatusLowRiskBg)
                                    .padding(horizontal = 16.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = if (isEn) "LOW RISK • FULLY VACCINATED" else "SIHAT (RISIKO RENDAH) • VAKSINASI LENGKAP",
                                    color = StatusLowRisk,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(BluePrimary)
                                    .bounceClick { showQrModal = false }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isEn) "Close" else "Tutup Paparan",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Quick Log Modal Dialog (Supports 2 decimal places & custom typing with Hints)
            if (showQuickLogModal) {
                Dialog(onDismissRequest = { showQuickLogModal = false }) {
                    var quickVal by remember { mutableStateOf("") }
                    var quickTiming by remember { mutableStateOf(GlucoseTiming.FASTING) }
                    var quickDate by remember { mutableStateOf(MockDataRepository.getTodayDateString()) }
                    var quickTime by remember { mutableStateOf(MockDataRepository.getNowTimeString()) }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(26.dp))
                            .background(SurfaceLight)
                            .padding(22.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isEn) "Record Quick Glucose" else "Catat Glukosa Pantas",
                                    color = Color(0xFF0F172A),
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = "✕",
                                    color = Color(0xFF64748B),
                                    fontSize = 16.sp,
                                    modifier = Modifier.bounceClick { showQuickLogModal = false }
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Editable Numerical Input for Glucose (with 2 decimal places)
                            OutlinedTextField(
                                value = quickVal,
                                onValueChange = { quickVal = it },
                                label = { Text(if (isEn) "Glucose Value (mmol/L)" else "Nilai Glukosa (mmol/L)") },
                                placeholder = { Text("5.40", color = Color(0xFF94A3B8)) },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                trailingIcon = {
                                    Text(
                                        text = "mmol/L",
                                        color = Color(0xFF64748B),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(end = 12.dp)
                                    )
                                },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BluePrimary,
                                    focusedTextColor = Color(0xFF0F172A),
                                    unfocusedTextColor = Color(0xFF0F172A)
                                )
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Preset Quick Tap Chips
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                listOf("4.80", "5.40", "6.25", "8.50", "11.20").forEach { preset ->
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(if (quickVal == preset) BluePrimary else BlueVeryLight)
                                            .bounceClick { quickVal = preset }
                                            .padding(vertical = 8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = preset,
                                            color = if (quickVal == preset) Color.White else BlueDark,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Native Android Date & Time Picker Properties (No Manual Typing!)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                ModernDateTimePickerField(
                                    label = if (isEn) "Date (Tap to Pick)" else "Tarikh (Sentuh Pilih)",
                                    value = quickDate,
                                    icon = "📅",
                                    onClick = {
                                        showAndroidDatePicker(context, quickDate) { selectedDate ->
                                            quickDate = selectedDate
                                        }
                                    },
                                    modifier = Modifier.weight(1.2f)
                                )
                                ModernDateTimePickerField(
                                    label = if (isEn) "Time (Tap to Pick)" else "Masa (Sentuh Pilih)",
                                    value = quickTime,
                                    icon = "⏰",
                                    onClick = {
                                        showAndroidTimePicker(context, quickTime) { selectedTime ->
                                            quickTime = selectedTime
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(Color(0xFF0D47A1), Color(0xFF1976D2), Color(0xFF00B4D8))
                                        )
                                    )
                                    .bounceClick {
                                        val parsed = quickVal.toDoubleOrNull()
                                        if (parsed == null || parsed !in 0.6..45.0) {
                                            NotificationService.showError(
                                                if (isEn) "Add Record Failed: Please enter a valid glucose value (0.6 – 45.0 mmol/L)!"
                                                else "Tambah Rekod Tidak Berjaya: Sila masukkan nilai glukosa yang sah (0.6 – 45.0 mmol/L)!"
                                            )
                                        } else {
                                            MockDataRepository.addGlucoseReading(
                                                value = parsed,
                                                timing = quickTiming,
                                                date = quickDate,
                                                time = quickTime,
                                                note = "Catatan Pantas Papan Pemuka"
                                            )
                                            showQuickLogModal = false
                                        }
                                    }
                                    .padding(vertical = 13.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isEn) "Save Glucose Record" else "Simpan Rekod Glukosa",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
        }

        // Global Soft-Glass In-App Notification Banner (Visible across AuthScreen & All Tabs, Auto-hides in 5s)
        InAppNotificationBanner(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 56.dp)
                .zIndex(999f)
        )
    }
}
