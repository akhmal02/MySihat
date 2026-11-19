package com.example.mysihat.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mysihat.data.LanguageManager
import com.example.mysihat.data.MockDataRepository
import com.example.mysihat.data.NotificationService
import com.example.mysihat.data.SupabaseDatabaseManager
import com.example.mysihat.ui.components.ModernHealthCrossIcon
import com.example.mysihat.ui.components.ModernShieldVerifiedIcon
import com.example.mysihat.ui.components.MySihatAppLogo
import com.example.mysihat.ui.theme.*

@Composable
fun AuthScreen(
    onLoginSuccess: () -> Unit
) {
    val isEn = LanguageManager.isEnglish
    var isRegisterMode by remember { mutableStateOf(false) }

    // Form inputs start completely empty (no hidden/pre-filled characters) so cursor always starts at left
    var nameInput by remember { mutableStateOf("") }
    var myKadInput by remember { mutableStateOf("") }
    var genderInput by remember { mutableStateOf("") }
    var genderDropdownExpanded by remember { mutableStateOf(false) }
    var emailInput by remember { mutableStateOf("") }
    var phoneInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val authTextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedBorderColor = BluePrimary,
        unfocusedBorderColor = CardBorder,
        focusedTextColor = Color(0xFF0F172A),
        unfocusedTextColor = Color(0xFF0F172A),
        cursorColor = BluePrimary,
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color(0xFFF8FAFC),
        focusedPlaceholderColor = Color(0xFF94A3B8),
        unfocusedPlaceholderColor = Color(0xFF94A3B8)
    )
    val authInputTextStyle = TextStyle(
        color = Color(0xFF0F172A),
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        textAlign = TextAlign.Start
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F52BA),
                        Color(0xFF1E88E5),
                        Color(0xFFE3F2FD),
                        BackgroundLight
                    )
                )
            )
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Language Switcher Top Corner
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White.copy(alpha = 0.25f))
                        .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                        .clickable { LanguageManager.toggleLanguage() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (isEn) "🇬🇧 English" else "🇲🇾 B. Melayu",
                        color = Color.White,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // App Emblem
            MySihatAppLogo(size = 86.dp, shadowElevation = 14.dp)

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "MySihat Malaysia",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isEn) "Independent Digital Health Platform\nDiabetes & Personal Health Management System" else "Aplikasi Pengurusan Kesihatan Kendiri\nSistem Pemantauan Diabetes & Pesakit Kronik",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Mode Selector: Login vs Register
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.25f))
                    .padding(4.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (!isRegisterMode) Color.White else Color.Transparent)
                            .clickable { isRegisterMode = false }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isEn) "Login" else "Log Masuk",
                            color = if (!isRegisterMode) BlueDark else Color.White,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isRegisterMode) Color.White else Color.Transparent)
                            .clickable { isRegisterMode = true }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isEn) "Register" else "Daftar Pesakit",
                            color = if (isRegisterMode) BlueDark else Color.White,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Main Auth Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(16.dp, RoundedCornerShape(26.dp), spotColor = BlueDark.copy(alpha = 0.15f))
                    .clip(RoundedCornerShape(26.dp))
                    .background(SurfaceLight)
                    .border(1.dp, CardBorder, RoundedCornerShape(26.dp))
                    .padding(22.dp)
            ) {
                Column {
                    Text(
                        text = if (!isRegisterMode) {
                            if (isEn) "Patient Login" else "Log Masuk Pesakit"
                        } else {
                            if (isEn) "Register New Patient" else "Pendaftaran Pesakit Baharu"
                        },
                        color = TextDark,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = if (!isRegisterMode) {
                            if (isEn) "Enter your registered Email and Password" else "Masukkan E-mel dan Kata Laluan anda"
                        } else {
                            if (isEn) "Enter your MyKad, Email & personal details" else "Lengkapkan MyKad, E-mel & butiran pendaftaran pesakit"
                        },
                        color = TextMuted,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Fields exclusive to Register
                    AnimatedVisibility(visible = isRegisterMode) {
                        Column {
                            Text(
                                text = if (isEn) "Full Name as per MyKad:" else "Nama Penuh Mengikut MyKad:",
                                color = TextDark,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = nameInput,
                                onValueChange = { nameInput = it },
                                placeholder = { Text("cth: Ahmad Zikri bin Razak", color = Color(0xFF94A3B8), fontSize = 13.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = authInputTextStyle,
                                shape = RoundedCornerShape(12.dp),
                                colors = authTextFieldColors,
                                singleLine = true,
                                trailingIcon = if (nameInput.isNotEmpty()) {
                                    {
                                        Text(
                                            text = "✕",
                                            color = Color(0xFF64748B),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier
                                                .clickable { nameInput = "" }
                                                .padding(8.dp)
                                        )
                                    }
                                } else null
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = if (isEn) "MyKad / National ID Number:" else "Nombor MyKad / No. Kad Pengenalan:",
                                color = TextDark,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = myKadInput,
                                onValueChange = { myKadInput = it },
                                placeholder = { Text("cth: 920814-14-5532", color = Color(0xFF94A3B8), fontSize = 13.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = authInputTextStyle,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                shape = RoundedCornerShape(12.dp),
                                colors = authTextFieldColors,
                                singleLine = true,
                                trailingIcon = if (myKadInput.isNotEmpty()) {
                                    {
                                        Text(
                                            text = "✕",
                                            color = Color(0xFF64748B),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier
                                                .clickable { myKadInput = "" }
                                                .padding(8.dp)
                                        )
                                    }
                                } else null
                            )
                            val autoCalculatedAge = SupabaseDatabaseManager.calculateAgeFromMyKad(myKadInput)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (autoCalculatedAge > 0) Color(0xFFEFF6FF) else Color(0xFFF8FAFC))
                                    .border(1.dp, if (autoCalculatedAge > 0) Color(0xFFBFDBFE) else CardBorder, RoundedCornerShape(10.dp))
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isEn) "Age:" else "Umur:",
                                    color = Color(0xFF475569),
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = if (autoCalculatedAge > 0) {
                                        "$autoCalculatedAge ${if (isEn) "years old" else "tahun"}"
                                    } else "-",
                                    color = if (autoCalculatedAge > 0) BluePrimary else Color(0xFF94A3B8),
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = if (isEn) "Phone Number (SMS OTP):" else "Nombor Telefon Bimbit:",
                                color = TextDark,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = phoneInput,
                                onValueChange = { phoneInput = it },
                                placeholder = { Text("cth: +60 12-345 6789", color = Color(0xFF94A3B8), fontSize = 13.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                textStyle = authInputTextStyle,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                shape = RoundedCornerShape(12.dp),
                                colors = authTextFieldColors,
                                singleLine = true,
                                trailingIcon = if (phoneInput.isNotEmpty()) {
                                    {
                                        Text(
                                            text = "✕",
                                            color = Color(0xFF64748B),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier
                                                .clickable { phoneInput = "" }
                                                .padding(8.dp)
                                        )
                                    }
                                } else null
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            // Gender Dropdown Selector (Jantina Pesakit) - Starts unselected
                            Text(
                                text = if (isEn) "Gender:" else "Jantina Pesakit:",
                                color = TextDark,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color.White)
                                        .border(
                                            width = 1.dp,
                                            color = if (genderDropdownExpanded) BluePrimary else CardBorder,
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .clickable { genderDropdownExpanded = true }
                                        .padding(horizontal = 16.dp, vertical = 15.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val genderDisplay = when (genderInput) {
                                        "Lelaki" -> if (isEn) "Male (Lelaki)" else "Lelaki"
                                        "Perempuan" -> if (isEn) "Female (Perempuan)" else "Perempuan"
                                        else -> if (isEn) "Select Gender (Male / Female)" else "Pilih Jantina (Lelaki / Perempuan)"
                                    }
                                    Text(
                                        text = genderDisplay,
                                        color = if (genderInput.isEmpty()) Color(0xFF94A3B8) else Color(0xFF0F172A),
                                        fontSize = 14.sp,
                                        fontWeight = if (genderInput.isEmpty()) FontWeight.Normal else FontWeight.SemiBold
                                    )
                                    Text(
                                        text = if (genderDropdownExpanded) "▲" else "▼",
                                        color = if (genderDropdownExpanded) BluePrimary else Color(0xFF64748B),
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                DropdownMenu(
                                    expanded = genderDropdownExpanded,
                                    onDismissRequest = { genderDropdownExpanded = false },
                                    modifier = Modifier
                                        .fillMaxWidth(0.78f)
                                        .background(Color.White)
                                ) {
                                    listOf(
                                        "Lelaki" to (if (isEn) "Male (Lelaki)" else "Lelaki"),
                                        "Perempuan" to (if (isEn) "Female (Perempuan)" else "Perempuan")
                                    ).forEach { (genderVal, genderLabel) ->
                                        val isSelected = genderInput == genderVal
                                        DropdownMenuItem(
                                            text = {
                                                Text(
                                                    text = genderLabel,
                                                    color = if (isSelected) BluePrimary else Color(0xFF0F172A),
                                                    fontSize = 14.sp,
                                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold
                                                )
                                            },
                                            onClick = {
                                                genderInput = genderVal
                                                genderDropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }

                    // Email Address Field (Used for Login & Register)
                    Text(
                        text = if (isEn) "Email Address:" else "Alamat E-mel Pesakit:",
                        color = TextDark,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        placeholder = { Text("cth: ahmad.zikri@gmail.com", color = Color(0xFF94A3B8), fontSize = 13.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = authInputTextStyle,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        shape = RoundedCornerShape(12.dp),
                        colors = authTextFieldColors,
                        singleLine = true,
                        trailingIcon = if (emailInput.isNotEmpty()) {
                            {
                                Text(
                                    text = "✕",
                                    color = Color(0xFF64748B),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .clickable { emailInput = "" }
                                        .padding(8.dp)
                                )
                            }
                        } else null
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Password Field (Used for Login & Register)
                    Text(
                        text = if (isEn) "Security Password:" else "Kata Laluan Keselamatan:",
                        color = TextDark,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = passwordInput,
                        onValueChange = { passwordInput = it },
                        placeholder = { Text(if (isEn) "Enter your password" else "Masukkan kata laluan", color = Color(0xFF94A3B8), fontSize = 13.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = authInputTextStyle,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        shape = RoundedCornerShape(12.dp),
                        colors = authTextFieldColors,
                        singleLine = true,
                        trailingIcon = if (passwordInput.isNotEmpty()) {
                            {
                                Text(
                                    text = "✕",
                                    color = Color(0xFF64748B),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .clickable { passwordInput = "" }
                                        .padding(8.dp)
                                )
                            }
                        } else null
                    )

                    if (errorMessage.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorMessage,
                            color = StatusAlert,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Primary Action Button (Login or Register)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(6.dp, RoundedCornerShape(14.dp), spotColor = BluePrimary.copy(alpha = 0.3f))
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(0xFF0D47A1),
                                        Color(0xFF1976D2),
                                        Color(0xFF00B4D8)
                                    )
                                )
                            )
                            .clickable {
                                if (isRegisterMode) {
                                    if (emailInput.isBlank() || nameInput.isBlank() || myKadInput.isBlank() || passwordInput.isBlank() || genderInput.isBlank()) {
                                        errorMessage = if (isEn) "Registration Failed: Please complete Name, MyKad, Gender, Email & Password!" else "Pendaftaran Tidak Berjaya: Sila lengkapkan Nama, MyKad, Jantina, E-mel & Kata Laluan!"
                                        NotificationService.showError(errorMessage)
                                    } else {
                                        if (MockDataRepository.register(nameInput, myKadInput, emailInput, passwordInput, phoneInput, genderInput)) {
                                            errorMessage = ""
                                            onLoginSuccess()
                                        } else {
                                            errorMessage = NotificationService.currentNotification.value?.message ?: ""
                                        }
                                    }
                                } else {
                                    if (emailInput.isBlank() || passwordInput.isBlank()) {
                                        errorMessage = if (isEn) "Login Failed: Please enter your Email and Password!" else "Log Masuk Tidak Berjaya: Sila masukkan E-mel dan Kata Laluan anda!"
                                        NotificationService.showError(errorMessage)
                                    } else {
                                        if (MockDataRepository.login(emailInput, passwordInput)) {
                                            errorMessage = ""
                                            onLoginSuccess()
                                        } else {
                                            errorMessage = NotificationService.currentNotification.value?.message ?: ""
                                        }
                                    }
                                }
                            }
                            .padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (!isRegisterMode) {
                                if (isEn) "Login to Patient Portal →" else "Log Masuk Portal Pesakit →"
                            } else {
                                if (isEn) "Register & Create Patient Profile →" else "Daftar Akaun Pesakit Baharu →"
                            },
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Demo Fast Access Button
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(BlueSurface)
                            .border(1.dp, Color(0xFFBFDBFE), RoundedCornerShape(12.dp))
                            .clickable {
                                if (MockDataRepository.loginDemoAccount()) {
                                    errorMessage = ""
                                    onLoginSuccess()
                                }
                            }
                            .padding(vertical = 11.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isEn) "⚡ Instant Demo Patient Access (Ahmad Zikri)" else "⚡ Akses Pantas Pesakit Demo (Ahmad Zikri)",
                            color = BlueDark,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                ModernShieldVerifiedIcon(size = 16.dp, tint = BlueDark)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isEn) "Independent Digital Health Monitoring App" else "Aplikasi Bebas Pemantauan Kesihatan & Diabetes",
                    color = BlueDark,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
