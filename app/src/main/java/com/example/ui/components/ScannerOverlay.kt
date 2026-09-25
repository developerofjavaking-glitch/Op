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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
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
import com.example.ui.theme.ClassicBlue
import com.example.ui.theme.ClassicBlueContainer
import com.example.ui.theme.LaserBeam
import com.example.ui.theme.LaserBeamGlow
import com.example.ui.theme.TextDarkPrimary

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
        // Vignette with clear center cutout + animated laser + corner brackets
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            val boxSize = 270.dp.toPx()
            val left = (canvasWidth - boxSize) / 2f
            val top = (canvasHeight - boxSize) / 2f - 40.dp.toPx()
            val right = left + boxSize
            val bottom = top + boxSize

            // Outer darkened mask
            val scrimColor = Color(0x8A000000)
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
                color = Color.White.copy(alpha = 0.6f),
                topLeft = Offset(left, top),
                size = Size(boxSize, boxSize),
                cornerRadius = CornerRadius(20.dp.toPx()),
                style = Stroke(width = 2.dp.toPx())
            )

            // Corner brackets in classic bold blue
            val cornerLength = 34.dp.toPx()
            val cornerStroke = 5.dp.toPx()
            val cornerColor = ClassicBlue
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

            // Animated Laser Line with gentle blue glow
            val laserY = top + (bottom - top) * laserProgress
            val laserPadding = 12.dp.toPx()

            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        LaserBeamGlow,
                        LaserBeam,
                        LaserBeamGlow,
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

        // Top Header - High contrast white pill badge
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                shadowElevation = 6.dp,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCodeScanner,
                        contentDescription = null,
                        tint = ClassicBlue,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Point camera at any QR code",
                        color = TextDarkPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Bottom Controls Bar - Large, friendly touch buttons
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
                label = if (isTorchOn) "Flash On" else "Flashlight",
                isActive = isTorchOn,
                onClick = onToggleTorch,
                testTag = "torch_button"
            )

            ScannerControlButton(
                icon = Icons.Default.Image,
                label = "Photos",
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
                .size(60.dp)
                .shadow(6.dp, CircleShape)
                .clip(CircleShape)
                .background(if (isActive) ClassicBlue else Color.White)
                .border(
                    width = 2.dp,
                    color = if (isActive) ClassicBlue else Color(0xFFE2E8F0),
                    shape = CircleShape
                )
                .testTag(testTag),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isActive) Color.White else ClassicBlue,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
    }
}
