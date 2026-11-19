package com.example.mysihat.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Custom modern geometric icons for MySihat.
 * Designed from scratch with custom geometry, dual-tones, and sleek rounded aesthetics
 * completely distinct from generic Google icons.
 */

@Composable
fun ModernHomeIcon(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    tint: Color = Color.White
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        // Roof Path
        val roofPath = Path().apply {
            moveTo(w * 0.12f, h * 0.48f)
            lineTo(w * 0.50f, h * 0.16f)
            lineTo(w * 0.88f, h * 0.48f)
        }
        drawPath(
            path = roofPath,
            color = tint,
            style = Stroke(width = w * 0.09f, cap = StrokeCap.Round)
        )

        // Walls Path
        val bodyPath = Path().apply {
            moveTo(w * 0.22f, h * 0.44f)
            lineTo(w * 0.22f, h * 0.82f)
            lineTo(w * 0.78f, h * 0.82f)
            lineTo(w * 0.78f, h * 0.44f)
        }
        drawPath(
            path = bodyPath,
            color = tint,
            style = Stroke(width = w * 0.09f, cap = StrokeCap.Round)
        )

        // Door
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.40f, h * 0.58f),
            size = Size(w * 0.20f, h * 0.24f),
            cornerRadius = CornerRadius(w * 0.04f, w * 0.04f)
        )
    }
}

@Composable
fun ModernSaveDiskIcon(
    modifier: Modifier = Modifier,
    size: Dp = 20.dp,
    tint: Color = Color.White
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        // Disk body
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.15f, h * 0.15f),
            size = Size(w * 0.70f, h * 0.70f),
            cornerRadius = CornerRadius(w * 0.10f, w * 0.10f),
            style = Stroke(width = w * 0.09f)
        )

        // Disk slider/label
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.30f, h * 0.20f),
            size = Size(w * 0.40f, h * 0.25f),
            cornerRadius = CornerRadius(w * 0.04f, w * 0.04f)
        )

        // Bottom window
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.32f, h * 0.56f),
            size = Size(w * 0.36f, h * 0.22f),
            cornerRadius = CornerRadius(w * 0.04f, w * 0.04f)
        )
    }
}

@Composable
fun ModernHealthCrossIcon(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    tint: Color = Color.White
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val barThickness = w * 0.32f
        val barLength = w * 0.85f
        val corner = CornerRadius(w * 0.12f, w * 0.12f)

        // Vertical Bar
        drawRoundRect(
            color = tint,
            topLeft = Offset((w - barThickness) / 2f, (h - barLength) / 2f),
            size = Size(barThickness, barLength),
            cornerRadius = corner
        )

        // Horizontal Bar
        drawRoundRect(
            color = tint,
            topLeft = Offset((w - barLength) / 2f, (h - barThickness) / 2f),
            size = Size(barLength, barThickness),
            cornerRadius = corner
        )
    }
}

@Composable
fun ModernPulseHeartIcon(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    tint: Color = Color(0xFF1976D2)
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val strokeWidth = w * 0.10f

        // ECG Wave Path
        val path = Path().apply {
            moveTo(w * 0.08f, h * 0.52f)
            lineTo(w * 0.30f, h * 0.52f)
            lineTo(w * 0.42f, h * 0.18f)
            lineTo(w * 0.58f, h * 0.82f)
            lineTo(w * 0.70f, h * 0.36f)
            lineTo(w * 0.80f, h * 0.52f)
            lineTo(w * 0.92f, h * 0.52f)
        }

        drawPath(
            path = path,
            color = tint,
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // Heart pulse point
        drawCircle(
            color = tint,
            radius = w * 0.08f,
            center = Offset(w * 0.42f, h * 0.18f)
        )
    }
}

@Composable
fun ModernGlucoseDropletIcon(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    tint: Color = Color(0xFF00B4D8)
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        // Blood Droplet Shape
        val path = Path().apply {
            moveTo(w * 0.50f, h * 0.10f)
            cubicTo(
                w * 0.75f, h * 0.40f,
                w * 0.90f, h * 0.65f,
                w * 0.50f, h * 0.92f
            )
            cubicTo(
                w * 0.10f, h * 0.65f,
                w * 0.25f, h * 0.40f,
                w * 0.50f, h * 0.10f
            )
            close()
        }

        drawPath(path = path, color = tint)

        // Inner soft highlight
        drawCircle(
            color = Color.White.copy(alpha = 0.55f),
            radius = w * 0.12f,
            center = Offset(w * 0.40f, h * 0.55f)
        )
    }
}

@Composable
fun ModernRadarMapIcon(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    tint: Color = Color(0xFF1976D2)
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val center = Offset(w / 2f, h / 2f)

        // Concentric radar arcs
        drawCircle(
            color = tint.copy(alpha = 0.30f),
            radius = w * 0.42f,
            style = Stroke(width = w * 0.08f)
        )
        drawCircle(
            color = tint.copy(alpha = 0.60f),
            radius = w * 0.26f,
            style = Stroke(width = w * 0.08f)
        )
        // Center Pin
        drawCircle(
            color = tint,
            radius = w * 0.14f
        )
        // Scanner needle
        drawLine(
            color = tint,
            start = center,
            end = Offset(w * 0.85f, h * 0.20f),
            strokeWidth = w * 0.08f,
            cap = StrokeCap.Round
        )
    }
}

@Composable
fun ModernCalendarCheckIcon(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    tint: Color = Color(0xFF1976D2)
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        // Outer Tile
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.10f, h * 0.20f),
            size = Size(w * 0.80f, h * 0.72f),
            cornerRadius = CornerRadius(w * 0.15f, w * 0.15f),
            style = Stroke(width = w * 0.09f)
        )

        // Calendar header bar
        drawLine(
            color = tint,
            start = Offset(w * 0.12f, h * 0.42f),
            end = Offset(w * 0.88f, h * 0.42f),
            strokeWidth = w * 0.08f
        )

        // Rings
        drawLine(
            color = tint,
            start = Offset(w * 0.32f, h * 0.10f),
            end = Offset(w * 0.32f, h * 0.26f),
            strokeWidth = w * 0.09f,
            cap = StrokeCap.Round
        )
        drawLine(
            color = tint,
            start = Offset(w * 0.68f, h * 0.10f),
            end = Offset(w * 0.68f, h * 0.26f),
            strokeWidth = w * 0.09f,
            cap = StrokeCap.Round
        )

        // Checkmark inside
        val checkPath = Path().apply {
            moveTo(w * 0.35f, h * 0.65f)
            lineTo(w * 0.48f, h * 0.76f)
            lineTo(w * 0.72f, h * 0.54f)
        }
        drawPath(
            path = checkPath,
            color = tint,
            style = Stroke(width = w * 0.09f, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun ModernPillCapsuleIcon(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    tint: Color = Color(0xFF00B4D8)
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        // Capsule Body
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.18f, h * 0.28f),
            size = Size(w * 0.64f, h * 0.44f),
            cornerRadius = CornerRadius(w * 0.22f, w * 0.22f)
        )

        // Divider band in middle
        drawLine(
            color = Color.White,
            start = Offset(w * 0.50f, h * 0.28f),
            end = Offset(w * 0.50f, h * 0.72f),
            strokeWidth = w * 0.09f
        )
    }
}

@Composable
fun ModernShieldVerifiedIcon(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    tint: Color = Color(0xFF059669)
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        val shieldPath = Path().apply {
            moveTo(w * 0.50f, h * 0.08f)
            lineTo(w * 0.88f, h * 0.24f)
            lineTo(w * 0.88f, h * 0.56f)
            cubicTo(
                w * 0.88f, h * 0.78f,
                w * 0.65f, h * 0.92f,
                w * 0.50f, h * 0.96f
            )
            cubicTo(
                w * 0.35f, h * 0.92f,
                w * 0.12f, h * 0.78f,
                w * 0.12f, h * 0.56f
            )
            lineTo(w * 0.12f, h * 0.24f)
            close()
        }

        drawPath(path = shieldPath, color = tint)

        // Inner Verified Checkmark
        val checkPath = Path().apply {
            moveTo(w * 0.32f, h * 0.52f)
            lineTo(w * 0.46f, h * 0.66f)
            lineTo(w * 0.70f, h * 0.40f)
        }
        drawPath(
            path = checkPath,
            color = Color.White,
            style = Stroke(width = w * 0.09f, cap = StrokeCap.Round)
        )
    }
}

@Composable
fun ModernQrPassIcon(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    tint: Color = Color.White
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val boxSize = w * 0.32f
        val cr = CornerRadius(w * 0.07f, w * 0.07f)
        val stroke = Stroke(width = w * 0.08f)

        // Top-Left Frame
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.10f, h * 0.10f),
            size = Size(boxSize, boxSize),
            cornerRadius = cr,
            style = stroke
        )
        drawCircle(
            color = tint,
            radius = w * 0.07f,
            center = Offset(w * 0.10f + boxSize / 2f, h * 0.10f + boxSize / 2f)
        )

        // Top-Right Frame
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.58f, h * 0.10f),
            size = Size(boxSize, boxSize),
            cornerRadius = cr,
            style = stroke
        )
        drawCircle(
            color = tint,
            radius = w * 0.07f,
            center = Offset(w * 0.58f + boxSize / 2f, h * 0.10f + boxSize / 2f)
        )

        // Bottom-Left Frame
        drawRoundRect(
            color = tint,
            topLeft = Offset(w * 0.10f, h * 0.58f),
            size = Size(boxSize, boxSize),
            cornerRadius = cr,
            style = stroke
        )
        drawCircle(
            color = tint,
            radius = w * 0.07f,
            center = Offset(w * 0.10f + boxSize / 2f, h * 0.58f + boxSize / 2f)
        )

        // Bottom-Right Data matrix dots
        drawCircle(color = tint, radius = w * 0.07f, center = Offset(w * 0.65f, h * 0.65f))
        drawCircle(color = tint, radius = w * 0.07f, center = Offset(w * 0.82f, h * 0.65f))
        drawCircle(color = tint, radius = w * 0.07f, center = Offset(w * 0.65f, h * 0.82f))
        drawCircle(color = tint, radius = w * 0.07f, center = Offset(w * 0.82f, h * 0.82f))
    }
}

@Composable
fun ModernThemedGoogleMapIcon(
    modifier: Modifier = Modifier,
    size: Dp = 18.dp,
    tint: Color = Color.White
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        // Folded Map Base
        val mapPath = Path().apply {
            moveTo(w * 0.12f, h * 0.28f)
            lineTo(w * 0.38f, h * 0.16f)
            lineTo(w * 0.62f, h * 0.28f)
            lineTo(w * 0.88f, h * 0.16f)
            lineTo(w * 0.88f, h * 0.76f)
            lineTo(w * 0.62f, h * 0.88f)
            lineTo(w * 0.38f, h * 0.76f)
            lineTo(w * 0.12f, h * 0.88f)
            close()
        }
        drawPath(
            path = mapPath,
            color = tint,
            style = Stroke(width = w * 0.09f, cap = StrokeCap.Round)
        )

        // Center fold lines
        drawLine(
            color = tint,
            start = Offset(w * 0.38f, h * 0.16f),
            end = Offset(w * 0.38f, h * 0.76f),
            strokeWidth = w * 0.08f
        )
        drawLine(
            color = tint,
            start = Offset(w * 0.62f, h * 0.28f),
            end = Offset(w * 0.62f, h * 0.88f),
            strokeWidth = w * 0.08f
        )

        // Map pin marker at center
        drawCircle(
            color = tint,
            radius = w * 0.14f,
            center = Offset(w * 0.50f, h * 0.45f)
        )
    }
}

@Composable
fun ModernThemedPhoneCallIcon(
    modifier: Modifier = Modifier,
    size: Dp = 18.dp,
    tint: Color = Color.White
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height

        // Modern handset receiver path
        val phonePath = Path().apply {
            moveTo(w * 0.28f, h * 0.18f)
            cubicTo(w * 0.22f, h * 0.18f, w * 0.18f, h * 0.24f, w * 0.20f, h * 0.34f)
            cubicTo(w * 0.24f, h * 0.56f, w * 0.44f, h * 0.76f, w * 0.66f, h * 0.80f)
            cubicTo(w * 0.76f, h * 0.82f, w * 0.82f, h * 0.78f, w * 0.82f, h * 0.72f)
            lineTo(w * 0.72f, h * 0.60f)
            cubicTo(w * 0.68f, h * 0.56f, w * 0.62f, h * 0.58f, w * 0.58f, h * 0.62f)
            lineTo(w * 0.52f, h * 0.66f)
            cubicTo(w * 0.44f, h * 0.60f, w * 0.40f, h * 0.56f, w * 0.34f, h * 0.48f)
            lineTo(w * 0.38f, h * 0.42f)
            cubicTo(w * 0.42f, h * 0.38f, w * 0.44f, h * 0.32f, w * 0.40f, h * 0.28f)
            close()
        }
        drawPath(
            path = phonePath,
            color = tint
        )

        // Sound wave
        val wavePath = Path().apply {
            moveTo(w * 0.68f, h * 0.22f)
            cubicTo(w * 0.78f, h * 0.28f, w * 0.82f, h * 0.38f, w * 0.82f, h * 0.46f)
        }
        drawPath(
            path = wavePath,
            color = tint,
            style = Stroke(width = w * 0.08f, cap = StrokeCap.Round)
        )
    }
}
