package com.example.data.model

data class ParsedQrResult(
    val rawText: String,
    val type: QrType,
    val title: String,
    val subtitle: String,
    val fields: Map<String, String> = emptyMap(),
    // Type-specific convenience accessors
    val url: String? = null,
    val wifiSsid: String? = null,
    val wifiPassword: String? = null,
    val wifiAuthType: String? = null,
    val contactName: String? = null,
    val contactPhone: String? = null,
    val contactEmail: String? = null,
    val emailAddress: String? = null,
    val emailSubject: String? = null,
    val emailBody: String? = null,
    val phoneNumber: String? = null,
    val smsNumber: String? = null,
    val smsBody: String? = null,
    val geoLatitude: Double? = null,
    val geoLongitude: Double? = null
)
