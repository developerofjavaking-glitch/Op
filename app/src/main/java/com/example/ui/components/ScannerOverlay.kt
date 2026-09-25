package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cameraswitch
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.LaserColor
import com.example.ui.theme.LaserGlow
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.ScannerSurface
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@Composable
fun ScannerOverlay(
    isTorchOn: Boolean,
    onToggleTorch: () -> Unit,
    onFlipCamera: () -> Unit,
    onPickImage: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "laser_transition")
    val laserProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser_progress"
    )

    Box(modifier = modifier.fillMaxSize()) {
        // Darkened vignette canvas with clear center cutout + animated laser + corner brackets
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            val boxSize = 270.dp.toPx()
            val left = (canvasWidth - boxSize) / 2f
            val top = (canvasHeight - boxSize) / 2f - 40.dp.toPx()
            val right = left + boxSize
            val bottom = top + boxSize

            // Outer darkened mask
            val scrimColor = Color(0x99070A10)
            drawRect(color = scrimColor)

            // Punch out reticle using BlendMode.Clear
            drawRoundRect(
                color = Color.Transparent,
                topLeft = Offset(left, top),
                size = Size(boxSize, boxSize),
                cornerRadius = CornerRadius(20.dp.toPx()),
                blendMode = BlendMode.Clear
            )

            // Inner subtle border
            drawRoundRect(
                color = Color(0x3300E5FF),
                topLeft = Offset(left, top),
                size = Size(boxSize, boxSize),
                cornerRadius = CornerRadius(20.dp.toPx()),
                style = Stroke(width = 1.5.dp.toPx())
            )

            // Corner brackets
            val cornerLength = 32.dp.toPx()
            val cornerStroke = 4.dp.toPx()
            val cornerColor = NeonCyan
            val cornerRadius = 14.dp.toPx()

            // Top-Left
            val tlPath = Path().apply {
                moveTo(left, top + cornerLength)
                lineTo(left, top + cornerRadius)
                quadraticBezierTo(left, top, left + cornerRadius, top)
                lineTo(left + cornerLength, top)
            }
            drawPath(tlPath, cornerColor, style = Stroke(width = cornerStroke))

            // Top-Right
            val trPath = Path().apply {
                moveTo(right - cornerLength, top)
                lineTo(right - cornerRadius, top)
                quadraticBezierTo(right, top, right, top + cornerRadius)
                lineTo(right, top + cornerLength)
            }
            drawPath(trPath, cornerColor, style = Stroke(width = cornerStroke))

            // Bottom-Left
            val blPath = Path().apply {
                moveTo(left, bottom - cornerLength)
                lineTo(left, bottom - cornerRadius)
                quadraticBezierTo(left, bottom, left + cornerRadius, bottom)
                lineTo(left + cornerLength, bottom)
            }
            drawPath(blPath, cornerColor, style = Stroke(width = cornerStroke))

            // Bottom-Right
            val brPath = Path().apply {
                moveTo(right - cornerLength, bottom)
                lineTo(right - cornerRadius, bottom)
                quadraticBezierTo(right, bottom, right, bottom - cornerRadius)
                lineTo(right, bottom - cornerLength)
            }
            drawPath(brPath, cornerColor, style = Stroke(width = cornerStroke))

            // Animated Laser Line with gradient glow
            val laserY = top + (bottom - top) * laserProgress
            val laserPadding = 12.dp.toPx()

            // Glow brush
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        LaserGlow,
                        LaserColor,
                        LaserGlow,
                        Color.Transparent
                    ),
                    startY = laserY - 12.dp.toPx(),
                    endY = laserY + 12.dp.toPx()
                ),
                topLeft = Offset(left + laserPadding, laserY - 10.dp.toPx()),
                size = Size(boxSize - (laserPadding * 2), 20.dp.toPx())
            )

            // Sharp core beam
            drawLine(
                color = Color.White,
                start = Offset(left + laserPadding + 10.dp.toPx(), laserY),
                end = Offset(right - laserPadding - 10.dp.toPx(), laserY),
                strokeWidth = 2.dp.toPx()
            )
        }

        // Top Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xCC131926))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = null,
                        tint = NeonCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Point camera at any QR code",
                        color = TextWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Bottom Controls Bar
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 36.dp, start = 24.dp, end = 24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ScannerControlButton(
                icon = if (isTorchOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                label = if (isTorchOn) "Torch On" else "Torch",
                isActive = isTorchOn,
                onClick = onToggleTorch,
                testTag = "torch_button"
            )

            ScannerControlButton(
                icon = Icons.Default.Image,
                label = "Gallery",
                isActive = false,
                onClick = onPickImage,
                testTag = "gallery_button"
            )

            ScannerControlButton(
                icon = Icons.Default.Cameraswitch,
                label = "Flip",
                isActive = false,
                onClick = onFlipCamera,
                testTag = "flip_camera_button"
            )
        }
    }
}

@Composable
fun ScannerControlButton(
    icon: ImageVector,
    label: String,
    isActive: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(if (isActive) NeonCyan.copy(alpha = 0.25f) else Color(0xCC1A2333))
                .border(
                    width = 1.5.dp,
                    color = if (isActive) NeonCyan else Color(0x4D3B4B70),
                    shape = CircleShape
                )
                .testTag(testTag),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isActive) NeonCyan else TextWhite,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = if (isActive) NeonCyan else TextMuted
        )
    }
}
