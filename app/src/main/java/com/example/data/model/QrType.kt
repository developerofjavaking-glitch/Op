package com.example.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.ui.theme.ClassicBlue
import com.example.ui.theme.ClassicGreen
import com.example.ui.theme.ClassicPurple

enum class QrType(
    val title: String,
    val icon: ImageVector,
    val color: Color,
    val actionLabel: String
) {
    URL("Website Link", Icons.Default.Language, ClassicBlue, "Open Website"),
    WIFI("Wi-Fi Network", Icons.Default.Wifi, ClassicGreen, "Connect to Wi-Fi"),
    CONTACT("Contact Card", Icons.Default.Person, ClassicPurple, "Save Contact"),
    EMAIL("Email Address", Icons.Default.Email, Color(0xFF0284C7), "Send Email"),
    PHONE("Phone Call", Icons.Default.Phone, ClassicGreen, "Call Number"),
    SMS("Text Message (SMS)", Icons.Default.Message, ClassicBlue, "Send Message"),
    GEO("Map Location", Icons.Default.LocationOn, Color(0xFFEA580C), "Open Maps"),
    TEXT("Plain Text", Icons.Default.TextFields, Color(0xFF475569), "Copy Text")
}
