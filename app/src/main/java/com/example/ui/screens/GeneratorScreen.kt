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
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import com.example.ui.theme.ClassicBg
import com.example.ui.theme.ClassicBlue
import com.example.ui.theme.ClassicBlueContainer
import com.example.ui.theme.ClassicBorder
import com.example.ui.theme.ClassicBorderLight
import com.example.ui.theme.ClassicGreen
import com.example.ui.theme.ClassicGreenContainer
import com.example.ui.theme.ClassicSurface
import com.example.ui.theme.ClassicSurfaceVariant
import com.example.ui.theme.TextDarkMuted
import com.example.ui.theme.TextDarkPrimary
import com.example.ui.theme.TextDarkSecondary
import com.example.ui.theme.TextLight
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

    // Classic & accessible color presets
    val presets = listOf(
        ColorThemePreset("Classic Black", Color(0xFF000000), Color(0xFFFFFFFF)),
        ColorThemePreset("Navy Blue", ClassicBlue, Color(0xFFFFFFFF)),
        ColorThemePreset("Forest Green", ClassicGreen, Color(0xFFFFFFFF)),
        ColorThemePreset("Dark Contrast", Color(0xFFFFFFFF), Color(0xFF1E293B))
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
            generatedBitmap = QrCodeGenerator.generateBitmap(
                content = "https://example.com",
                size = 512,
                foregroundColor = currentPreset.fgColor.toArgb(),
                backgroundColor = currentPreset.bgColor.toArgb()
            )
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ClassicBg)
            .verticalScroll(scrollState)
            .padding(vertical = 18.dp)
    ) {
        // Header (Clear & Friendly for all ages)
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(
                text = "CREATE QR CODE",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = ClassicBlue,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Generate Any QR Code",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = TextDarkPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Choose a type below, fill in the information, and share or print your code.",
                fontSize = 14.sp,
                color = TextDarkSecondary,
                lineHeight = 20.sp
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Type Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedTypeIndex,
            containerColor = ClassicBg,
            contentColor = ClassicBlue,
            edgePadding = 20.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTypeIndex]),
                    color = ClassicBlue,
                    height = 3.dp
                )
            },
            divider = {}
        ) {
            types.forEachIndexed { index, type ->
                val isSelected = selectedTypeIndex == index
                Tab(
                    selected = isSelected,
                    onClick = { selectedTypeIndex = index },
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 6.dp)
                        ) {
                            Icon(
                                imageVector = type.icon,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = type.title.substringBefore(" "),
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    },
                    selectedContentColor = ClassicBlue,
                    unselectedContentColor = TextDarkMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Input Fields Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = ClassicSurface),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(ClassicBorder)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                when (currentType) {
                    QrType.URL -> {
                        ScannerInputField(
                            value = urlInput,
                            onValueChange = { urlInput = it },
                            label = "Website Address (URL)",
                            placeholder = "https://example.com"
                        )
                    }
                    QrType.TEXT -> {
                        ScannerInputField(
                            value = textInput,
                            onValueChange = { textInput = it },
                            label = "Text Note or Message",
                            placeholder = "Enter any message, notes, or instructions",
                            singleLine = false,
                            maxLines = 4
                        )
                    }
                    QrType.WIFI -> {
                        ScannerInputField(
                            value = wifiSsid,
                            onValueChange = { wifiSsid = it },
                            label = "Wi-Fi Network Name (SSID)",
                            placeholder = "e.g. Home_Network"
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        ScannerInputField(
                            value = wifiPassword,
                            onValueChange = { wifiPassword = it },
                            label = "Wi-Fi Password",
                            placeholder = "Enter Wi-Fi password"
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Security Type",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextDarkSecondary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            listOf("WPA", "WEP", "nopass").forEach { auth ->
                                val isAuthSelected = wifiAuth == auth
                                val label = if (auth == "nopass") "No Password" else auth
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isAuthSelected) ClassicBlueContainer else ClassicSurfaceVariant)
                                        .border(
                                            1.dp,
                                            if (isAuthSelected) ClassicBlue else ClassicBorder,
                                            RoundedCornerShape(10.dp)
                                        )
                                        .clickable { wifiAuth = auth }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 13.sp,
                                        fontWeight = if (isAuthSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isAuthSelected) ClassicBlue else TextDarkSecondary
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
                            placeholder = "e.g. John Smith"
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        ScannerInputField(
                            value = contactPhone,
                            onValueChange = { contactPhone = it },
                            label = "Phone Number",
                            placeholder = "+1 555 123 4567"
                        )
                        Spacer(modifier = Modifier.height(12.dp))
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
                            label = "Send To (Email Address)",
                            placeholder = "friend@example.com"
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        ScannerInputField(
                            value = emailSubject,
                            onValueChange = { emailSubject = it },
                            label = "Email Subject",
                            placeholder = "Meeting details"
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        ScannerInputField(
                            value = emailBody,
                            onValueChange = { emailBody = it },
                            label = "Message Content",
                            placeholder = "Write your message here...",
                            singleLine = false,
                            maxLines = 3
                        )
                    }
                    QrType.PHONE -> {
                        ScannerInputField(
                            value = phoneNumber,
                            onValueChange = { phoneNumber = it },
                            label = "Phone Number to Call",
                            placeholder = "+1 234 567 8900"
                        )
                    }
                    else -> {}
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Color Theme Presets
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = null,
                    tint = ClassicBlue,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "QR CODE COLOR STYLE",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDarkMuted,
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
                            .background(ClassicSurface)
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) ClassicBlue else ClassicBorder,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedPresetIndex = index }
                            .padding(vertical = 10.dp, horizontal = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .background(preset.fgColor)
                                    .border(1.dp, ClassicBorderLight, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = if (preset.fgColor == Color.White) Color.Black else Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = preset.name,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) ClassicBlue else TextDarkSecondary
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
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = currentPreset.bgColor),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                border = CardDefaults.outlinedCardBorder().copy(
                    brush = androidx.compose.ui.graphics.SolidColor(ClassicBorder)
                )
            ) {
                Box(modifier = Modifier.padding(22.dp), contentAlignment = Alignment.Center) {
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

        Spacer(modifier = Modifier.height(22.dp))

        // Action Buttons: Save & Share (Large, 54dp, High Contrast)
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
                    .height(54.dp)
                    .testTag("share_qr_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ClassicBlue, contentColor = TextLight)
            ) {
                Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Share QR", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = {
                    onSaveGenerated(rawPayload, displayTitle, currentType.name)
                    Toast.makeText(context, "Saved to History", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .weight(1f)
                    .height(54.dp)
                    .testTag("save_qr_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = ClassicBlue)
            ) {
                Icon(imageVector = Icons.Default.BookmarkAdd, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save Code", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
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
        label = { Text(label, fontSize = 14.sp, color = TextDarkSecondary) },
        placeholder = { Text(placeholder, fontSize = 14.sp, color = TextDarkMuted.copy(alpha = 0.7f)) },
        singleLine = singleLine,
        maxLines = maxLines,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = ClassicBlue,
            unfocusedBorderColor = ClassicBorder,
            focusedContainerColor = ClassicSurfaceVariant,
            unfocusedContainerColor = ClassicSurfaceVariant,
            focusedTextColor = TextDarkPrimary,
            unfocusedTextColor = TextDarkPrimary
        )
    )
}
