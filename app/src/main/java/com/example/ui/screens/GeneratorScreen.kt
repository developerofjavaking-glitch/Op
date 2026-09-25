package com.example.ui.screens

import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkAdd
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.QrType
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.ScannerBorder
import com.example.ui.theme.ScannerDarkBg
import com.example.ui.theme.ScannerSurface
import com.example.ui.theme.ScannerSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.util.QrCodeGenerator

data class ColorThemePreset(
    val name: String,
    val fgColor: Color,
    val bgColor: Color
)

@Composable
fun GeneratorScreen(
    onSaveGenerated: (rawText: String, title: String, typeName: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val types = listOf(QrType.URL, QrType.TEXT, QrType.WIFI, QrType.CONTACT, QrType.EMAIL, QrType.PHONE)
    var selectedTypeIndex by remember { mutableIntStateOf(0) }
    val currentType = types[selectedTypeIndex]

    // Fields
    var urlInput by remember { mutableStateOf("https://") }
    var textInput by remember { mutableStateOf("") }
    // Wi-Fi
    var wifiSsid by remember { mutableStateOf("") }
    var wifiPassword by remember { mutableStateOf("") }
    var wifiAuth by remember { mutableStateOf("WPA") }
    // Contact
    var contactName by remember { mutableStateOf("") }
    var contactPhone by remember { mutableStateOf("") }
    var contactEmail by remember { mutableStateOf("") }
    // Email
    var emailAddress by remember { mutableStateOf("") }
    var emailSubject by remember { mutableStateOf("") }
    var emailBody by remember { mutableStateOf("") }
    // Phone
    var phoneNumber by remember { mutableStateOf("") }

    // Color presets
    val presets = listOf(
        ColorThemePreset("Classic", Color.Black, Color.White),
        ColorThemePreset("Cyber Cyan", NeonCyan, ScannerDarkBg),
        ColorThemePreset("Electric", ElectricViolet, ScannerDarkBg),
        ColorThemePreset("Matrix", EmeraldGreen, ScannerDarkBg)
    )
    var selectedPresetIndex by remember { mutableIntStateOf(0) }
    val currentPreset = presets[selectedPresetIndex]

    // Computed Raw Payload
    val rawPayload = remember(
        currentType, urlInput, textInput, wifiSsid, wifiPassword, wifiAuth,
        contactName, contactPhone, contactEmail, emailAddress, emailSubject, emailBody, phoneNumber
    ) {
        when (currentType) {
            QrType.URL -> urlInput.trim()
            QrType.TEXT -> textInput.trim()
            QrType.WIFI -> "WIFI:T:$wifiAuth;S:${wifiSsid.trim()};P:${wifiPassword.trim()};;"
            QrType.CONTACT -> "BEGIN:VCARD\nVERSION:3.0\nFN:${contactName.trim()}\nTEL:${contactPhone.trim()}\nEMAIL:${contactEmail.trim()}\nEND:VCARD"
            QrType.EMAIL -> "mailto:${emailAddress.trim()}?subject=${emailSubject.trim()}&body=${emailBody.trim()}"
            QrType.PHONE -> "tel:${phoneNumber.trim()}"
            else -> textInput.trim()
        }
    }

    val displayTitle = remember(currentType, urlInput, textInput, wifiSsid, contactName, emailAddress, phoneNumber) {
        when (currentType) {
            QrType.URL -> urlInput.removePrefix("https://").removePrefix("http://")
            QrType.TEXT -> textInput.take(25)
            QrType.WIFI -> wifiSsid
            QrType.CONTACT -> contactName
            QrType.EMAIL -> emailAddress
            QrType.PHONE -> phoneNumber
            else -> "QR Code"
        }.ifBlank { "New ${currentType.title}" }
    }

    var generatedBitmap by remember { mutableStateOf<Bitmap?>(null) }

    LaunchedEffect(rawPayload, currentPreset) {
        if (rawPayload.isNotBlank() && rawPayload != "https://" && rawPayload != "tel:") {
            generatedBitmap = QrCodeGenerator.generateBitmap(
                content = rawPayload,
                size = 512,
                foregroundColor = currentPreset.fgColor.toArgb(),
                backgroundColor = currentPreset.bgColor.toArgb()
            )
        } else {
            // Default sample preview
            generatedBitmap = QrCodeGenerator.generateBitmap(
                content = "https://google.com",
                size = 512,
                foregroundColor = currentPreset.fgColor.toArgb(),
                backgroundColor = currentPreset.bgColor.toArgb()
            )
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ScannerDarkBg)
            .verticalScroll(scrollState)
            .padding(vertical = 16.dp)
    ) {
        // Header
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = "QR GENERATOR",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = NeonCyan,
                letterSpacing = 1.2.sp
            )
            Text(
                text = "Create Custom QR Code",
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                color = TextWhite
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Type Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedTypeIndex,
            containerColor = ScannerDarkBg,
            contentColor = NeonCyan,
            edgePadding = 20.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTypeIndex]),
                    color = NeonCyan,
                    height = 2.dp
                )
            },
            divider = {}
        ) {
            types.forEachIndexed { index, type ->
                Tab(
                    selected = selectedTypeIndex == index,
                    onClick = { selectedTypeIndex = index },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = type.icon,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = type.title.substringBefore(" "),
                                fontSize = 13.sp,
                                fontWeight = if (selectedTypeIndex == index) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    },
                    selectedContentColor = NeonCyan,
                    unselectedContentColor = TextMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Input Fields Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = ScannerSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(ScannerBorder))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                when (currentType) {
                    QrType.URL -> {
                        ScannerInputField(
                            value = urlInput,
                            onValueChange = { urlInput = it },
                            label = "Website URL",
                            placeholder = "https://example.com"
                        )
                    }
                    QrType.TEXT -> {
                        ScannerInputField(
                            value = textInput,
                            onValueChange = { textInput = it },
                            label = "Text Content",
                            placeholder = "Enter message or notes",
                            singleLine = false,
                            maxLines = 4
                        )
                    }
                    QrType.WIFI -> {
                        ScannerInputField(
                            value = wifiSsid,
                            onValueChange = { wifiSsid = it },
                            label = "Network Name (SSID)",
                            placeholder = "Home_Wi-Fi"
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        ScannerInputField(
                            value = wifiPassword,
                            onValueChange = { wifiPassword = it },
                            label = "Password",
                            placeholder = "WPA2 Password"
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            listOf("WPA", "WEP", "nopass").forEach { auth ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (wifiAuth == auth) NeonCyan.copy(alpha = 0.2f) else ScannerSurfaceVariant)
                                        .border(1.dp, if (wifiAuth == auth) NeonCyan else ScannerBorder, RoundedCornerShape(8.dp))
                                        .clickable { wifiAuth = auth }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = auth,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (wifiAuth == auth) NeonCyan else TextMuted
                                    )
                                }
                            }
                        }
                    }
                    QrType.CONTACT -> {
                        ScannerInputField(
                            value = contactName,
                            onValueChange = { contactName = it },
                            label = "Full Name",
                            placeholder = "John Doe"
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        ScannerInputField(
                            value = contactPhone,
                            onValueChange = { contactPhone = it },
                            label = "Phone Number",
                            placeholder = "+1 555 123 4567"
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        ScannerInputField(
                            value = contactEmail,
                            onValueChange = { contactEmail = it },
                            label = "Email Address",
                            placeholder = "john@example.com"
                        )
                    }
                    QrType.EMAIL -> {
                        ScannerInputField(
                            value = emailAddress,
                            onValueChange = { emailAddress = it },
                            label = "Recipient Email",
                            placeholder = "recipient@domain.com"
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        ScannerInputField(
                            value = emailSubject,
                            onValueChange = { emailSubject = it },
                            label = "Subject",
                            placeholder = "Important meeting"
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        ScannerInputField(
                            value = emailBody,
                            onValueChange = { emailBody = it },
                            label = "Message Body",
                            placeholder = "Write your message...",
                            singleLine = false,
                            maxLines = 3
                        )
                    }
                    QrType.PHONE -> {
                        ScannerInputField(
                            value = phoneNumber,
                            onValueChange = { phoneNumber = it },
                            label = "Phone Number",
                            placeholder = "+1 234 567 8900"
                        )
                    }
                    else -> {}
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Color Theme Presets
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = null,
                    tint = NeonCyan,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "QR COLOR STYLE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                presets.forEachIndexed { index, preset ->
                    val isSelected = selectedPresetIndex == index
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(ScannerSurface)
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) NeonCyan else ScannerBorder,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedPresetIndex = index }
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(preset.fgColor)
                                    .border(1.dp, Color.Gray, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = if (preset.fgColor == Color.Black) Color.White else Color.Black,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = preset.name,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) NeonCyan else TextMuted
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Live QR Code Preview
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = currentPreset.bgColor),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(if (currentPreset.bgColor == ScannerDarkBg) ScannerBorder else Color.LightGray)
                )
            ) {
                Box(modifier = Modifier.padding(20.dp), contentAlignment = Alignment.Center) {
                    if (generatedBitmap != null) {
                        Image(
                            bitmap = generatedBitmap!!.asImageBitmap(),
                            contentDescription = "Generated QR Code",
                            modifier = Modifier
                                .size(220.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .testTag("generated_qr_image")
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Action Buttons: Save & Share
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    if (generatedBitmap != null) {
                        QrCodeGenerator.shareBitmap(context, generatedBitmap!!, displayTitle)
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .testTag("share_qr_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = Color.Black)
            ) {
                Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Share QR", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = {
                    onSaveGenerated(rawPayload, displayTitle, currentType.name)
                    Toast.makeText(context, "Saved to QR History", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .testTag("save_qr_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ScannerSurfaceVariant, contentColor = TextWhite)
            ) {
                Icon(imageVector = Icons.Default.BookmarkAdd, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Save Code", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
fun ScannerInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    singleLine: Boolean = true,
    maxLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 13.sp, color = TextMuted) },
        placeholder = { Text(placeholder, fontSize = 13.sp, color = TextMuted.copy(alpha = 0.5f)) },
        singleLine = singleLine,
        maxLines = maxLines,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = NeonCyan,
            unfocusedBorderColor = ScannerBorder,
            focusedContainerColor = ScannerSurfaceVariant,
            unfocusedContainerColor = ScannerSurfaceVariant,
            focusedTextColor = TextWhite,
            unfocusedTextColor = TextWhite
        )
    )
}
