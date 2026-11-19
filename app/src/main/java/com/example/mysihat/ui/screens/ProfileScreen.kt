package com.example.mysihat.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.mysihat.data.LanguageManager
import com.example.mysihat.data.MockDataRepository
import com.example.mysihat.data.NotificationService
import com.example.mysihat.data.PdfExportService
import com.example.mysihat.data.SupabaseDatabaseManager
import com.example.mysihat.ui.components.ModernQrPassIcon
import com.example.mysihat.ui.components.ModernShieldVerifiedIcon
import com.example.mysihat.ui.components.MySihatAppLogo
import com.example.mysihat.ui.components.MySihatTopBar
import com.example.mysihat.ui.components.bounceClick
import com.example.mysihat.ui.theme.*

@Composable
fun ProfileScreen(
    onShowQrDialog: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val isEn = LanguageManager.isEnglish
    val user = MockDataRepository.currentUser.value

    var showEditProfileModal by remember { mutableStateOf(false) }
    var showEditEmergencyModal by remember { mutableStateOf(false) }

    // Edit Profile Modal States with Hints
    var editName by remember { mutableStateOf(user.name) }
    var editMyKad by remember { mutableStateOf(user.myKad) }
    var editEmail by remember { mutableStateOf(user.email) }
    var editPassword by remember { mutableStateOf(user.password) }
    var editAge by remember { mutableStateOf(user.age.toString()) }
    var editGender by remember { mutableStateOf(user.gender) }
    var editGenderDropdownExpanded by remember { mutableStateOf(false) }
    var editCondition by remember { mutableStateOf(user.condition) }
    var editBloodType by remember { mutableStateOf(user.bloodType) }
    var editHeight by remember { mutableStateOf(user.heightCm.toString()) }
    var editWeight by remember { mutableStateOf(user.weightKg.toString()) }

    // Edit Emergency Kin States
    var kinName by remember { mutableStateOf(user.emergencyContact.name) }
    var kinRel by remember { mutableStateOf(user.emergencyContact.relationship) }
    var kinPhone by remember { mutableStateOf(user.emergencyContact.phone) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Safe Top Bar Below Status Bar
        item {
            MySihatTopBar(
                screenTitle = if (isEn) "Patient Profile" else "Profil Pesakit",
                subtitle = if (isEn) "Digital Health Pass & Patient Profile" else "Pas Kesihatan Digital & Rekod Peribadi",
                action = {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White.copy(alpha = 0.25f))
                            .bounceClick { onLogout() }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (isEn) "Log Out" else "Log Keluar",
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

        // Digital QR Pass Hero Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
                    .shadow(10.dp, RoundedCornerShape(22.dp), spotColor = BlueDark.copy(alpha = 0.15f))
                    .clip(RoundedCornerShape(22.dp))
                    .background(SurfaceLight)
                    .border(1.dp, CardBorder, RoundedCornerShape(22.dp))
                    .padding(20.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            MySihatAppLogo(size = 26.dp, shadowElevation = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isEn) "MYSIHAT DIGITAL HEALTH PASS" else "KAD KESIHATAN DIGITAL MYSIHAT",
                                color = StatusLowRisk,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }

                        Text(
                            text = if (isEn) "Active Status" else "Status Aktif",
                            color = BluePrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // QR Pass Canvas display
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .clip(RoundedCornerShape(18.dp))
                            .background(BlueSurface)
                            .border(2.dp, BluePrimary.copy(alpha = 0.35f), RoundedCornerShape(18.dp))
                            .bounceClick { onShowQrDialog() }
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        ModernQrPassIcon(size = 120.dp, tint = BlueDark)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = user.name,
                        color = Color(0xFF0F172A), // Black Font
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "MyKad: ${user.myKad}  •  ID: ${user.sihatId}",
                        color = Color(0xFF475569),
                        fontSize = 12.sp
                    )
                    val displayCondition = if (user.condition.isBlank() || user.condition == "-") {
                        if (isEn) "No Diagnosis" else "Tiada Diagnosis"
                    } else user.condition
                    val calculatedIcAge = SupabaseDatabaseManager.calculateAgeFromMyKad(user.myKad)
                    val resolvedUserAge = if (calculatedIcAge > 0) calculatedIcAge else user.age
                    val displayAge = if (resolvedUserAge <= 0) {
                        if (isEn) "Age: -" else "Umur: -"
                    } else "$resolvedUserAge ${if (isEn) "years old" else "tahun"}"
                    val displayGender = if (user.gender.isBlank() || user.gender == "-") "-" else user.gender

                    Text(
                        text = "$displayCondition  •  $displayAge  •  $displayGender",
                        color = BluePrimary,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Action Buttons (No "+" prefix!)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(BlueSurface)
                                .border(1.dp, Color(0xFFBFDBFE), RoundedCornerShape(12.dp))
                                .bounceClick {
                                    editName = user.name
                                    editMyKad = if (user.myKad == "-") "" else user.myKad
                                    editEmail = user.email
                                    editPassword = user.password
                                    val autoAge = SupabaseDatabaseManager.calculateAgeFromMyKad(user.myKad).let { if (it > 0) it else user.age }
                                    editAge = if (autoAge <= 0) "" else autoAge.toString()
                                    editGender = if (user.gender == "-") "" else user.gender
                                    editCondition = if (user.condition == "-") "" else user.condition
                                    editBloodType = if (user.bloodType == "-") "" else user.bloodType
                                    editHeight = if (user.heightCm <= 0) "" else user.heightCm.toString()
                                    editWeight = if (user.weightKg <= 0.0) "" else user.weightKg.toString()
                                    showEditProfileModal = true
                                }
                                .padding(vertical = 11.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isEn) "Edit Profile Details" else "Kemaskini Profil",
                                color = BlueDark,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1.3f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFF0D47A1), Color(0xFF1976D2))
                                    )
                                )
                                .bounceClick {
                                    val file = PdfExportService.generateAndSharePatientReport(context)
                                    if (file != null) {
                                        PdfExportService.sharePdf(context, file)
                                    }
                                }
                                .padding(vertical = 11.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isEn) "Export PDF Report 📄" else "Eksport Laporan PDF 📄",
                                color = Color.White,
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Biometric Profile Overview
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (isEn) "Patient Physical & Biometric Profile" else "Profil Fizikal & Biometrik Pesakit",
                    color = Color(0xFF0F172A), // Black Font
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(10.dp))

                val bloodDisplay = if (user.bloodType.isBlank() || user.bloodType == "-") "-" else user.bloodType
                val heightDisplay = if (user.heightCm <= 0) "-" else "${user.heightCm} cm"
                val weightDisplay = if (user.weightKg <= 0.0) "-" else String.format(java.util.Locale.US, "%.1f kg", user.weightKg)
                val bmiDisplay = if (user.heightCm > 0 && user.weightKg > 0.0) {
                    val heightMeters = user.heightCm / 100.0
                    String.format(java.util.Locale.US, "%.1f", user.weightKg / (heightMeters * heightMeters))
                } else "-"

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        (if (isEn) "Blood Type" else "Darah") to bloodDisplay,
                        (if (isEn) "Height" else "Tinggi") to heightDisplay,
                        (if (isEn) "Weight" else "Berat") to weightDisplay,
                        "BMI" to bmiDisplay
                    ).forEach { (label, value) ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .shadow(2.dp, RoundedCornerShape(14.dp), spotColor = BlueDark.copy(alpha = 0.05f))
                                .clip(RoundedCornerShape(14.dp))
                                .background(SurfaceLight)
                                .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(text = label, color = Color(0xFF64748B), fontSize = 9.5.sp, fontWeight = FontWeight.Medium, maxLines = 1)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(text = value, color = Color(0xFF0F172A), fontSize = 13.5.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }
                }
            }
        }

        // Emergency Contact Card with Add/Edit
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .shadow(4.dp, RoundedCornerShape(18.dp), spotColor = BlueDark.copy(alpha = 0.06f))
                    .clip(RoundedCornerShape(18.dp))
                    .background(SurfaceLight)
                    .border(1.dp, CardBorder, RoundedCornerShape(18.dp))
                    .padding(18.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isEn) "Emergency Contact (Next of Kin)" else "Waris Kecemasan (Next of Kin)",
                            color = Color(0xFF0F172A),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Text(
                            text = if (isEn) "Edit" else "Kemaskini",
                            color = BluePrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.bounceClick { showEditEmergencyModal = true }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = user.emergencyContact.name,
                        color = Color(0xFF0F172A), // Black Font
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${if (isEn) "Relationship" else "Hubungan"}: ${user.emergencyContact.relationship}  •  ${user.emergencyContact.phone}",
                        color = Color(0xFF475569),
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Quick Call Action
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFEFF6FF))
                            .bounceClick {
                                val callIntent = Intent(Intent.ACTION_DIAL).apply {
                                    data = Uri.parse("tel:${user.emergencyContact.phone.replace(" ", "")}")
                                }
                                context.startActivity(callIntent)
                            }
                            .padding(vertical = 11.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isEn) "📞 Call Emergency Contact Directly" else "📞 Panggil Waris Kecemasan Terus",
                            color = BlueDark,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    // ==================== EDIT USER PROFILE MODAL WITH HINTS ====================
    if (showEditProfileModal) {
        Dialog(onDismissRequest = { showEditProfileModal = false }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(SurfaceLight)
                    .padding(20.dp)
            ) {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    item {
                        Text(
                            text = if (isEn) "Update Patient Profile Details" else "Kemaskini Maklumat Profil Pesakit",
                            color = Color(0xFF0F172A),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = if (isEn) "Changes will update all MySihat medical records and PDF exports." else "Perubahan akan mengemaskini rekod kesihatan MySihat & eksport PDF.",
                            color = Color(0xFF64748B),
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    item {
                        OutlinedTextField(
                            value = editName,
                            onValueChange = { editName = it },
                            label = { Text(if (isEn) "Full Name" else "Nama Penuh") },
                            placeholder = { Text("Ahmad Zikri bin Razak", color = Color(0xFF94A3B8)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BluePrimary,
                                focusedTextColor = Color(0xFF0F172A),
                                unfocusedTextColor = Color(0xFF0F172A)
                            )
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = editMyKad,
                            onValueChange = {
                                editMyKad = it
                                val icAge = SupabaseDatabaseManager.calculateAgeFromMyKad(it)
                                if (icAge > 0) {
                                    editAge = icAge.toString()
                                }
                            },
                            label = { Text(if (isEn) "MyKad / IC Number" else "Nombor MyKad / No. Kad Pengenalan") },
                            placeholder = { Text("920814-14-5532", color = Color(0xFF94A3B8)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BluePrimary,
                                focusedTextColor = Color(0xFF0F172A),
                                unfocusedTextColor = Color(0xFF0F172A)
                            )
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = editEmail,
                            onValueChange = { editEmail = it },
                            label = { Text(if (isEn) "Email Address" else "Alamat E-mel") },
                            placeholder = { Text("ahmad.zikri@gmail.com", color = Color(0xFF94A3B8)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BluePrimary,
                                focusedTextColor = Color(0xFF0F172A),
                                unfocusedTextColor = Color(0xFF0F172A)
                            )
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = editPassword,
                            onValueChange = { editPassword = it },
                            label = { Text(if (isEn) "Security Password" else "Kata Laluan Keselamatan") },
                            placeholder = { Text("Zikri@2026*", color = Color(0xFF94A3B8)) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BluePrimary,
                                focusedTextColor = Color(0xFF0F172A),
                                unfocusedTextColor = Color(0xFF0F172A)
                            )
                        )
                    }

                    item {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = editAge,
                                onValueChange = { editAge = it },
                                label = { Text(if (isEn) "Age" else "Umur") },
                                placeholder = { Text("-", color = Color(0xFF94A3B8)) },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = BluePrimary,
                                    focusedTextColor = Color(0xFF0F172A),
                                    unfocusedTextColor = Color(0xFF0F172A)
                                )
                            )
                            Box(modifier = Modifier.weight(1.2f).padding(top = 6.dp)) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color.White)
                                        .border(
                                            width = 1.dp,
                                            color = if (editGenderDropdownExpanded) BluePrimary else CardBorder,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .clickable { editGenderDropdownExpanded = true }
                                        .padding(horizontal = 12.dp, vertical = 16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (editGender.isBlank() || editGender == "-") {
                                            if (isEn) "Select Gender" else "Pilih Jantina"
                                        } else editGender,
                                        color = if (editGender.isBlank() || editGender == "-") Color(0xFF94A3B8) else Color(0xFF0F172A),
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = if (editGenderDropdownExpanded) "▲" else "▼",
                                        color = Color(0xFF64748B),
                                        fontSize = 11.sp
                                    )
                                }
                                DropdownMenu(
                                    expanded = editGenderDropdownExpanded,
                                    onDismissRequest = { editGenderDropdownExpanded = false },
                                    modifier = Modifier.background(Color.White)
                                ) {
                                    listOf("Lelaki", "Perempuan").forEach { gOpt ->
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = gOpt,
                                                    color = if (editGender == gOpt) BluePrimary else Color(0xFF0F172A),
                                                    fontWeight = if (editGender == gOpt) FontWeight.ExtraBold else FontWeight.SemiBold
                                                )
                                            },
                                            onClick = {
                                                editGender = gOpt
                                                editGenderDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Text(
                            text = if (isEn) "Chronic Medical Condition:" else "Jenis Penyakit Kronik:",
                            color = Color(0xFF0F172A),
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        listOf(
                            "Diabetes Jenis 2 & Darah Tinggi",
                            "Diabetes Jenis 1 (Insulin Dependent)",
                            "Diabetes Gestasi (GDM)",
                            "Hipertensi / Tekanan Darah Tinggi"
                        ).forEach { cond ->
                            val isSelected = editCondition == cond
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) BlueVeryLight else BlueSurface)
                                    .border(1.dp, if (isSelected) BluePrimary else CardBorder, RoundedCornerShape(10.dp))
                                    .bounceClick { editCondition = cond }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = cond,
                                    color = if (isSelected) BlueDark else Color(0xFF0F172A),
                                    fontSize = 11.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = editHeight,
                                onValueChange = { editHeight = it },
                                label = { Text("Tinggi (cm)") },
                                placeholder = { Text("175", color = Color(0xFF94A3B8)) },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color(0xFF0F172A),
                                    unfocusedTextColor = Color(0xFF0F172A)
                                )
                            )
                            OutlinedTextField(
                                value = editWeight,
                                onValueChange = { editWeight = it },
                                label = { Text("Berat (kg)") },
                                placeholder = { Text("68.5", color = Color(0xFF94A3B8)) },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color(0xFF0F172A),
                                    unfocusedTextColor = Color(0xFF0F172A)
                                )
                            )
                            OutlinedTextField(
                                value = editBloodType,
                                onValueChange = { editBloodType = it },
                                label = { Text("Darah") },
                                placeholder = { Text("O+", color = Color(0xFF94A3B8)) },
                                modifier = Modifier.weight(0.8f),
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = Color(0xFF0F172A),
                                    unfocusedTextColor = Color(0xFF0F172A)
                                )
                            )
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(BlueSurface)
                                    .bounceClick { showEditProfileModal = false }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(if (isEn) "Cancel" else "Batal", color = Color(0xFF64748B), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }

                            Box(
                                modifier = Modifier
                                    .weight(1.5f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(Color(0xFF0D47A1), Color(0xFF1976D2))
                                        )
                                    )
                                    .bounceClick {
                                        val finalAge = editAge.toIntOrNull() ?: SupabaseDatabaseManager.calculateAgeFromMyKad(editMyKad)
                                        MockDataRepository.updateUserProfile(
                                            name = editName,
                                            myKad = editMyKad.ifBlank { "-" },
                                            email = editEmail,
                                            pass = editPassword,
                                            age = finalAge,
                                            gender = editGender.ifBlank { "-" },
                                            condition = editCondition.ifBlank { "-" },
                                            bloodType = editBloodType.ifBlank { "-" },
                                            heightCm = editHeight.toIntOrNull() ?: 0,
                                            weightKg = editWeight.toDoubleOrNull() ?: 0.0
                                        )
                                        showEditProfileModal = false
                                    }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(if (isEn) "Save Profile" else "Simpan Profil", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    // ==================== EDIT EMERGENCY CONTACT MODAL ====================
    if (showEditEmergencyModal) {
        Dialog(onDismissRequest = { showEditEmergencyModal = false }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .background(SurfaceLight)
                    .padding(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = if (isEn) "Update Emergency Contact" else "Kemaskini Waris Kecemasan",
                        color = Color(0xFF0F172A),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = kinName,
                        onValueChange = { kinName = it },
                        label = { Text(if (isEn) "Contact Name" else "Nama Waris") },
                        placeholder = { Text("Nurul Huda binti Halim", color = Color(0xFF94A3B8)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BluePrimary,
                            focusedTextColor = Color(0xFF0F172A),
                            unfocusedTextColor = Color(0xFF0F172A)
                        )
                    )

                    OutlinedTextField(
                        value = kinRel,
                        onValueChange = { kinRel = it },
                        label = { Text(if (isEn) "Relationship" else "Hubungan (cth: Pasangan, Ibu Bapa)") },
                        placeholder = { Text("Isteri", color = Color(0xFF94A3B8)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BluePrimary,
                            focusedTextColor = Color(0xFF0F172A),
                            unfocusedTextColor = Color(0xFF0F172A)
                        )
                    )

                    OutlinedTextField(
                        value = kinPhone,
                        onValueChange = { kinPhone = it },
                        label = { Text(if (isEn) "Phone Number" else "Nombor Telefon") },
                        placeholder = { Text("+60 12-345 6789", color = Color(0xFF94A3B8)) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BluePrimary,
                            focusedTextColor = Color(0xFF0F172A),
                            unfocusedTextColor = Color(0xFF0F172A)
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(BlueSurface)
                                .bounceClick { showEditEmergencyModal = false }
                                .padding(vertical = 11.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(if (isEn) "Cancel" else "Batal", color = Color(0xFF64748B), fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }

                        Box(
                            modifier = Modifier
                                .weight(1.5f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(BluePrimary)
                                .bounceClick {
                                    MockDataRepository.updateEmergencyContact(kinName, kinRel, kinPhone)
                                    showEditEmergencyModal = false
                                }
                                .padding(vertical = 11.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(if (isEn) "Save Contact" else "Simpan Waris", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
