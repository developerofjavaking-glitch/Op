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
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple

enum class QrType(
    val title: String,
    val icon: ImageVector,
    val color: Color,
    val actionLabel: String
) {
    URL("Website URL", Icons.Default.Language, NeonCyan, "Open Website"),
    WIFI("Wi-Fi Network", Icons.Default.Wifi, EmeraldGreen, "Connect / Copy"),
    CONTACT("Contact Card", Icons.Default.Person, NeonPurple, "Add Contact"),
    EMAIL("Email Address", Icons.Default.Email, ElectricViolet, "Send Email"),
    PHONE("Phone Number", Icons.Default.Phone, EmeraldGreen, "Call Number"),
    SMS("SMS Message", Icons.Default.Message, NeonCyan, "Send SMS"),
    GEO("Location Coordinates", Icons.Default.LocationOn, Color(0xFFFF7043), "Open Maps"),
    TEXT("Text Content", Icons.Default.TextFields, Color(0xFFB0BEC5), "Copy Text")
}
