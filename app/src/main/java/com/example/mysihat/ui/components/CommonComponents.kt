package com.example.mysihat.ui.components

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mysihat.R
import com.example.mysihat.data.*
import com.example.mysihat.ui.theme.*

/**
 * Official MySihat 3D Glossy Heart-ECG-Leaf Application Logo Component.
 */
@Composable
fun MySihatAppLogo(
    size: Dp = 40.dp,
    shadowElevation: Dp = 6.dp
) {
    val cornerRadius = (size.value * 0.22f).dp
    Image(
        painter = painterResource(id = R.drawable.mysihat_app_logo),
        contentDescription = "MySihat Logo",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(size)
            .shadow(shadowElevation, RoundedCornerShape(cornerRadius), spotColor = BlueGlow)
            .clip(RoundedCornerShape(cornerRadius))
    )
}

/**
 * Interactive bounce click animation for modern tactile response.
 */
fun Modifier.bounceClick(onClick: () -> Unit) = composed {
    var buttonState by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (buttonState) 0.96f else 1f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 400f),
        label = "bounce"
    )

    this
        .scale(scale)
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        ) {
            onClick()
        }
        .pointerInput(buttonState) {
            awaitPointerEventScope {
                buttonState = if (buttonState) {
                    waitForUpOrCancellation()
                    false
                } else {
                    awaitFirstDown(false)
                    true
                }
            }
        }
}

/**
 * Global Soft-Glass In-App Notification Toast Banner.
 * Features translucent frosted glassmorphism styling and auto-hides after 5 seconds (5000ms)
 * with a live animated countdown bar.
 */
@Composable
fun InAppNotificationBanner(modifier: Modifier = Modifier) {
    val notification = NotificationService.currentNotification.value
    val isEn = LanguageManager.isEnglish

    AnimatedVisibility(
        visible = notification != null,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(tween(240)),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(tween(240)),
        modifier = modifier
    ) {
        if (notification != null) {
            val progress = remember(notification.id) { Animatable(1f) }
            LaunchedEffect(notification.id) {
                progress.snapTo(1f)
                progress.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(
                        durationMillis = notification.durationMillis.toInt(),
                        easing = LinearEasing
                    )
                )
            }

            val (accentColor, icon, badgeLabel) = when (notification.type) {
                NotificationType.SUCCESS -> Triple(
                    Color(0xFF059669),
                    "✓",
                    if (isEn) "SUCCESS • BERJAYA" else "BERJAYA • DISAHKAN"
                )
                NotificationType.ERROR -> Triple(
                    Color(0xFFDC2626),
                    "✕",
                    if (isEn) "FAILED • TIDAK BERJAYA" else "TIDAK BERJAYA • RALAT"
                )
                NotificationType.INFO -> Triple(
                    Color(0xFF0284C7),
                    "ℹ",
                    if (isEn) "INFO • MAKLUMAN" else "MAKLUMAN SISTEM"
                )
            }

            val glassGradient = when (notification.type) {
                NotificationType.SUCCESS -> Brush.linearGradient(
                    colors = listOf(
                        Color(0xF5FFFFFF),
                        Color(0xF0ECFDF5),
                        Color(0xEBD1FAE5)
                    )
                )
                NotificationType.ERROR -> Brush.linearGradient(
                    colors = listOf(
                        Color(0xF5FFFFFF),
                        Color(0xF0FEF2F2),
                        Color(0xEBFEE2E2)
                    )
                )
                NotificationType.INFO -> Brush.linearGradient(
                    colors = listOf(
                        Color(0xF5FFFFFF),
                        Color(0xF0F0F9FF),
                        Color(0xEBE0F2FE)
                    )
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .shadow(
                        elevation = 16.dp,
                        shape = RoundedCornerShape(20.dp),
                        spotColor = accentColor.copy(alpha = 0.28f),
                        ambientColor = accentColor.copy(alpha = 0.18f)
                    )
                    .clip(RoundedCornerShape(20.dp))
                    .background(glassGradient)
                    .border(
                        width = 1.5.dp,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.95f),
                                accentColor.copy(alpha = 0.45f)
                            )
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .clickable { NotificationService.dismiss() }
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 14.dp, end = 14.dp, top = 12.dp, bottom = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Soft Frosted Glass Icon Orb
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(13.dp))
                                .background(accentColor.copy(alpha = 0.15f))
                                .border(1.dp, accentColor.copy(alpha = 0.35f), RoundedCornerShape(13.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = icon,
                                color = accentColor,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(accentColor.copy(alpha = 0.14f))
                                        .padding(horizontal = 7.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = badgeLabel,
                                        color = accentColor,
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 0.4.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "• 5s",
                                    color = Color(0xFF64748B),
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = notification.message,
                                color = Color(0xFF0F172A),
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Soft Glass Close Pill
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0F172A).copy(alpha = 0.06f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "✕",
                                color = Color(0xFF475569),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // 5-Second Animated Glass Countdown Bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.5.dp)
                            .background(accentColor.copy(alpha = 0.12f))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(progress.value.coerceIn(0f, 1f))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(accentColor, accentColor.copy(alpha = 0.6f))
                                    )
                                )
                        )
                    }
                }
            }
        }
    }
}

/**
 * Standard Top Application Bar for every screen.
 * Places title and logo safely BELOW the device status bar.
 * Contains direct Bilingual Language Switcher (BM / EN).
 */
@Composable
fun MySihatTopBar(
    screenTitle: String,
    subtitle: String = LanguageManager.t("Aplikasi Bebas Pemantauan Kesihatan", "Independent Health Monitoring App"),
    action: (@Composable () -> Unit)? = null
) {
    val isEn = LanguageManager.isEnglish

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0D47A1),
                        Color(0xFF1976D2)
                    )
                )
            )
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
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
                // Official Application Logo
                MySihatAppLogo(size = 40.dp, shadowElevation = 6.dp)

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "MySihat",
                            color = Color.White,
                            fontSize = 16.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "• $screenTitle",
                            color = Color(0xFF90CAF9),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }
                    Text(
                        text = subtitle,
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Normal,
                        maxLines = 1
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Pure Language Switcher Toggle (BM / EN)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color.White.copy(alpha = 0.22f))
                        .border(1.dp, Color.White.copy(alpha = 0.45f), RoundedCornerShape(10.dp))
                        .bounceClick { LanguageManager.toggleLanguage() }
                        .padding(horizontal = 8.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = if (isEn) "EN" else "BM",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        softWrap = false
                    )
                }

                if (action != null) {
                    action()
                }
            }
        }
    }
}

/**
 * 3 INDICATORS ONLY AS REQUESTED:
 * 1. LOW (Rendah)
 * 2. NORMAL (Normal)
 * 3. HIGH (Tinggi)
 */
@Composable
fun RangeLegendBox(modifier: Modifier = Modifier) {
    val isEn = LanguageManager.isEnglish
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(14.dp), spotColor = BlueDark.copy(alpha = 0.05f))
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceLight)
            .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
            .padding(12.dp)
    ) {
        Column {
            Text(
                text = if (isEn) "CLINICAL GLUCOSE TARGET (3 INDICATORS)" else "SASARAN GLUKOSA KLINIKAL (3 PENUNJUK UTAMA)",
                color = Color(0xFF0F172A),
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 1. LOW (Rendah)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFF3E8FF))
                        .border(1.dp, Color(0xFFD8B4FE), RoundedCornerShape(10.dp))
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = if (isEn) "LOW" else "RENDAH", color = Color(0xFF7E22CE), fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                        Text(text = "< 4.00 mmol/L", color = Color(0xFF7E22CE), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text(text = if (isEn) "Hypoglycemia" else "Hipoglikemia", color = Color(0xFF6B21A8), fontSize = 8.5.sp)
                    }
                }

                // 2. NORMAL (Normal)
                Box(
                    modifier = Modifier
                        .weight(1.3f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(StatusLowRiskBg)
                        .border(1.dp, Color(0xFFA7F3D0), RoundedCornerShape(10.dp))
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "NORMAL", color = StatusLowRisk, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                        Text(text = if (isEn) "Post-Meal < 10.0" else "Lepas Makan < 10.0", color = StatusLowRisk, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text(text = if (isEn) "Fasting 4.0 - 7.0" else "Puasa 4.0 - 7.0", color = Color(0xFF047857), fontSize = 8.5.sp)
                    }
                }

                // 3. HIGH (Tinggi)
                Box(
                    modifier = Modifier
                        .weight(1.1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(StatusAlertBg)
                        .border(1.dp, Color(0xFFFECACA), RoundedCornerShape(10.dp))
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = if (isEn) "HIGH" else "TINGGI", color = StatusAlert, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                        Text(text = if (isEn) "≥ 10.00 mmol/L" else "≥ 10.00 mmol/L", color = StatusAlert, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text(text = if (isEn) "Post-Meal Alert" else "Amaran Tinggi", color = Color(0xFFB91C1C), fontSize = 8.5.sp)
                    }
                }
            }
        }
    }
}

/**
 * 3 INDICATORS ONLY FOR BLOOD PRESSURE:
 * 1. LOW (Rendah)
 * 2. NORMAL (Normal)
 * 3. HIGH (Tinggi)
 */
@Composable
fun BpRangeLegendBox(modifier: Modifier = Modifier) {
    val isEn = LanguageManager.isEnglish
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(14.dp), spotColor = BlueDark.copy(alpha = 0.05f))
            .clip(RoundedCornerShape(14.dp))
            .background(SurfaceLight)
            .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
            .padding(12.dp)
    ) {
        Column {
            Text(
                text = if (isEn) "CLINICAL BLOOD PRESSURE TARGET (3 INDICATORS)" else "SASARAN TEKANAN DARAH KLINIKAL (3 PENUNJUK)",
                color = Color(0xFF0F172A),
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Low
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFF3E8FF))
                        .border(1.dp, Color(0xFFD8B4FE), RoundedCornerShape(10.dp))
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = if (isEn) "LOW" else "RENDAH", color = Color(0xFF7E22CE), fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                        Text(text = "< 90/60", color = Color(0xFF7E22CE), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Normal
                Box(
                    modifier = Modifier
                        .weight(1.3f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(StatusLowRiskBg)
                        .border(1.dp, Color(0xFFA7F3D0), RoundedCornerShape(10.dp))
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "NORMAL", color = StatusLowRisk, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                        Text(text = "< 130/85", color = StatusLowRisk, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // High
                Box(
                    modifier = Modifier
                        .weight(1.1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(StatusAlertBg)
                        .border(1.dp, Color(0xFFFECACA), RoundedCornerShape(10.dp))
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = if (isEn) "HIGH" else "TINGGI", color = StatusAlert, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                        Text(text = "≥ 140/90", color = StatusAlert, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

/**
 * Flagship MySejahtera Digital Health Pass Card
 */
@Composable
fun MySejahteraHealthCard(
    user: UserProfile,
    onQrClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isEn = LanguageManager.isEnglish
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(22.dp), spotColor = BlueDark.copy(alpha = 0.2f))
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.linearGradient(
                    listOf(
                        Color(0xFF0D47A1),
                        Color(0xFF1976D2),
                        Color(0xFF0288D1)
                    )
                )
            )
            .padding(20.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f, fill = false),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MySihatAppLogo(size = 38.dp, shadowElevation = 4.dp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "MySihat Digital Pass",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp,
                            maxLines = 1
                        )
                        Text(
                            text = if (isEn) "MySihat • Personal Health Record" else "MySihat • Rekod Kesihatan Peribadi",
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 10.5.sp,
                            maxLines = 1
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.22f))
                        .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .bounceClick { onQrClick() }
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        ModernQrPassIcon(size = 16.dp, tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "QR Pass",
                            color = Color.White,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            softWrap = false
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = user.name,
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 0.3.sp
            )

            Spacer(modifier = Modifier.height(3.dp))

            Text(
                text = "MyKad: ${user.myKad}  •  ID: ${user.sihatId}",
                color = Color(0xFFBBDEFB),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White)
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Column {
                        Text(
                            text = if (isEn) "STATUS" else "STATUS KESIHATAN",
                            color = Color(0xFF475569),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            ModernShieldVerifiedIcon(size = 14.dp, tint = StatusLowRisk)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isEn) "Healthy (Normal)" else "Sihat (Normal)",
                                color = StatusLowRisk,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.White)
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Column {
                        Text(
                            text = if (isEn) "DIAGNOSIS" else "DIAGNOSIS KLINIKAL",
                            color = Color(0xFF475569),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            ModernHealthCrossIcon(size = 14.dp, tint = BluePrimary)
                            Spacer(modifier = Modifier.width(4.dp))
                            val diagText = if (user.condition.isBlank() || user.condition == "-") {
                                if (isEn) "No Record" else "Tiada Rekod"
                            } else {
                                user.condition.split("&").firstOrNull()?.trim() ?: user.condition
                            }
                            Text(
                                text = diagText,
                                color = Color(0xFF0F172A),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * 3-Category Clinical Severity Badge Only (Low, Normal, High).
 * Guaranteed horizontal pill layout that never squashes or wraps into vertical letters.
 */
@Composable
fun ClinicalSeverityBadge(
    severity: HealthSeverity,
    modifier: Modifier = Modifier
) {
    val isEn = LanguageManager.isEnglish
    val (bgColor, textColor, text) = when (severity) {
        HealthSeverity.LOW -> Triple(Color(0xFFF3E8FF), Color(0xFF7E22CE), if (isEn) "LOW" else "RENDAH")
        HealthSeverity.NORMAL -> Triple(StatusLowRiskBg, StatusLowRisk, if (isEn) "NORMAL" else "NORMAL")
        HealthSeverity.HIGH -> Triple(StatusAlertBg, StatusAlert, if (isEn) "HIGH" else "TINGGI")
    }

    Box(
        modifier = modifier
            .defaultMinSize(minWidth = 72.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            softWrap = false,
            maxLines = 1
        )
    }
}

@Composable
fun QuickServiceItem(
    title: String,
    subtitle: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(108.dp)
            .shadow(2.dp, RoundedCornerShape(16.dp), spotColor = BlueDark.copy(alpha = 0.06f))
            .clip(RoundedCornerShape(16.dp))
            .background(SurfaceLight)
            .border(1.dp, CardBorder, RoundedCornerShape(16.dp))
            .bounceClick { onClick() }
            .padding(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(BlueSurface),
                    contentAlignment = Alignment.Center
                ) {
                    icon()
                }
                Text(
                    text = "→",
                    color = BluePrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Column {
                Text(
                    text = title,
                    color = Color(0xFF0F172A),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    color = Color(0xFF64748B),
                    fontSize = 10.sp,
                    lineHeight = 13.sp,
                    maxLines = 2,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * Native Android DatePickerDialog Launcher.
 * Opens native system calendar dialog to select Day, Month, Year.
 */
fun showAndroidDatePicker(
    context: Context,
    initialDateStr: String = "",
    onDateSelected: (String) -> Unit
) {
    val cal = Calendar.getInstance()
    try {
        if (initialDateStr.isNotBlank()) {
            val parsed = SimpleDateFormat("dd MMM yyyy", Locale.US).parse(initialDateStr)
            if (parsed != null) cal.time = parsed
        }
    } catch (_: Exception) {}

    val dialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val formatted = SimpleDateFormat("dd MMM yyyy", Locale.US).format(cal.time)
            onDateSelected(formatted)
        },
        cal.get(Calendar.YEAR),
        cal.get(Calendar.MONTH),
        cal.get(Calendar.DAY_OF_MONTH)
    )
    dialog.show()
}

/**
 * Native Android TimePickerDialog Launcher.
 * Opens native system radial clock dialog to select Hour, Minute and AM/PM.
 */
fun showAndroidTimePicker(
    context: Context,
    initialTimeStr: String = "",
    onTimeSelected: (String) -> Unit
) {
    val cal = Calendar.getInstance()
    try {
        if (initialTimeStr.isNotBlank()) {
            val parsed = SimpleDateFormat("hh:mm a", Locale.US).parse(initialTimeStr)
            if (parsed != null) cal.time = parsed
        }
    } catch (_: Exception) {}

    val dialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
            cal.set(Calendar.MINUTE, minute)
            val formatted = SimpleDateFormat("hh:mm a", Locale.US).format(cal.time)
            onTimeSelected(formatted)
        },
        cal.get(Calendar.HOUR_OF_DAY),
        cal.get(Calendar.MINUTE),
        false
    )
    dialog.show()
}

/**
 * Interactive Date/Time Property Selector Field (No Manual Typing!).
 * Tapping this component opens the official Android Date/Time picker dialog.
 */
@Composable
fun ModernDateTimePickerField(
    label: String,
    value: String,
    icon: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF8FAFC))
            .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
            .bounceClick { onClick() }
            .padding(horizontal = 12.dp, vertical = 9.dp)
    ) {
        Column {
            Text(
                text = label,
                color = Color(0xFF64748B),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(3.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(text = icon, fontSize = 14.sp)
                Text(
                    text = value.ifBlank { "Pilih..." },
                    color = Color(0xFF0F172A),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1
                )
            }
        }
    }
}
