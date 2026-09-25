package com.example.util

import com.example.data.model.ParsedQrResult
import com.example.data.model.QrType
import java.net.URI
import java.util.Locale

object QrParser {

    fun parse(rawText: String): ParsedQrResult {
        val trimmed = rawText.trim()

        // 1. Wi-Fi: WIFI:S:MySSID;T:WPA;P:MyPassword;;
        if (trimmed.startsWith("WIFI:", ignoreCase = true)) {
            return parseWifi(trimmed)
        }

        // 2. vCard / MeCard
        if (trimmed.startsWith("BEGIN:VCARD", ignoreCase = true) || trimmed.startsWith("MECARD:", ignoreCase = true)) {
            return parseContact(trimmed)
        }

        // 3. Mailto / MATMSG
        if (trimmed.startsWith("mailto:", ignoreCase = true) || trimmed.startsWith("MATMSG:", ignoreCase = true)) {
            return parseEmail(trimmed)
        }

        // 4. Telephone
        if (trimmed.startsWith("tel:", ignoreCase = true)) {
            val phone = trimmed.substring(4).trim()
            return ParsedQrResult(
                rawText = trimmed,
                type = QrType.PHONE,
                title = phone,
                subtitle = "Tap to call",
                phoneNumber = phone,
                fields = mapOf("Phone" to phone)
            )
        }

        // 5. SMS
        if (trimmed.startsWith("smsto:", ignoreCase = true) || trimmed.startsWith("sms:", ignoreCase = true)) {
            return parseSms(trimmed)
        }

        // 6. Geo Location
        if (trimmed.startsWith("geo:", ignoreCase = true)) {
            return parseGeo(trimmed)
        }

        // 7. URL
        if (isUrl(trimmed)) {
            val normalizedUrl = if (!trimmed.startsWith("http://", ignoreCase = true) &&
                !trimmed.startsWith("https://", ignoreCase = true)
            ) {
                "https://$trimmed"
            } else {
                trimmed
            }

            val host = try {
                URI(normalizedUrl).host ?: normalizedUrl
            } catch (e: Exception) {
                normalizedUrl
            }

            return ParsedQrResult(
                rawText = trimmed,
                type = QrType.URL,
                title = host,
                subtitle = normalizedUrl,
                url = normalizedUrl,
                fields = mapOf("URL" to normalizedUrl, "Host" to host)
            )
        }

        // 8. Default Plain Text
        val firstLine = trimmed.lines().firstOrNull() ?: trimmed
        val title = if (firstLine.length > 35) firstLine.take(35) + "..." else firstLine
        val subtitle = if (trimmed.length > 50) trimmed.take(50) + "..." else trimmed

        return ParsedQrResult(
            rawText = trimmed,
            type = QrType.TEXT,
            title = title,
            subtitle = subtitle,
            fields = mapOf("Content" to trimmed)
        )
    }

    private fun isUrl(text: String): Boolean {
        if (text.startsWith("http://", ignoreCase = true) || text.startsWith("https://", ignoreCase = true)) {
            return true
        }
        val lower = text.lowercase(Locale.ROOT)
        return (lower.startsWith("www.") ||
                lower.endsWith(".com") || lower.endsWith(".org") ||
                lower.endsWith(".net") || lower.endsWith(".io") ||
                lower.endsWith(".dev") || lower.endsWith(".app") ||
                lower.endsWith(".ai")) && !text.contains(" ")
    }

    private fun parseWifi(raw: String): ParsedQrResult {
        // Example: WIFI:S:MyNetwork;T:WPA;P:Pass123;H:false;;
        val payload = raw.substringAfter("WIFI:", "")
        var ssid = ""
        var pass = ""
        var auth = "WPA"
        var hidden = false

        val tokens = payload.split(";")
        for (token in tokens) {
            val t = token.trim()
            when {
                t.startsWith("S:", ignoreCase = true) -> ssid = t.substring(2)
                t.startsWith("P:", ignoreCase = true) -> pass = t.substring(2)
                t.startsWith("T:", ignoreCase = true) -> auth = t.substring(2)
                t.startsWith("H:", ignoreCase = true) -> hidden = t.substring(2).toBoolean()
            }
        }

        val fields = mutableMapOf<String, String>()
        fields["SSID"] = ssid.ifBlank { "Unknown Network" }
        if (pass.isNotBlank()) fields["Password"] = pass
        fields["Security"] = auth.ifBlank { "None" }
        if (hidden) fields["Hidden Network"] = "Yes"

        return ParsedQrResult(
            rawText = raw,
            type = QrType.WIFI,
            title = ssid.ifBlank { "Wi-Fi Network" },
            subtitle = if (pass.isNotBlank()) "Security: $auth • Protected" else "Open Network",
            wifiSsid = ssid,
            wifiPassword = pass,
            wifiAuthType = auth,
            fields = fields
        )
    }

    private fun parseContact(raw: String): ParsedQrResult {
        var name = ""
        var phone = ""
        var email = ""

        if (raw.startsWith("MECARD:", ignoreCase = true)) {
            val content = raw.substringAfter("MECARD:")
            val parts = content.split(";")
            for (p in parts) {
                val token = p.trim()
                when {
                    token.startsWith("N:", ignoreCase = true) -> name = token.substring(2).replace(",", " ")
                    token.startsWith("TEL:", ignoreCase = true) -> phone = token.substring(4)
                    token.startsWith("EMAIL:", ignoreCase = true) -> email = token.substring(6)
                }
            }
        } else {
            // vCard parsing
            val lines = raw.lines()
            for (line in lines) {
                val trimmed = line.trim()
                when {
                    trimmed.startsWith("FN:", ignoreCase = true) -> name = trimmed.substring(3)
                    trimmed.startsWith("TEL", ignoreCase = true) -> {
                        phone = trimmed.substringAfter(":")
                    }
                    trimmed.startsWith("EMAIL", ignoreCase = true) -> {
                        email = trimmed.substringAfter(":")
                    }
                }
            }
        }

        val fields = mutableMapOf<String, String>()
        if (name.isNotBlank()) fields["Name"] = name
        if (phone.isNotBlank()) fields["Phone"] = phone
        if (email.isNotBlank()) fields["Email"] = email

        return ParsedQrResult(
            rawText = raw,
            type = QrType.CONTACT,
            title = name.ifBlank { phone.ifBlank { "Contact Card" } },
            subtitle = listOf(phone, email).filter { it.isNotBlank() }.joinToString(" • ").ifBlank { "vCard Details" },
            contactName = name,
            contactPhone = phone,
            contactEmail = email,
            phoneNumber = phone,
            emailAddress = email,
            fields = fields
        )
    }

    private fun parseEmail(raw: String): ParsedQrResult {
        var email = ""
        var subject = ""
        var body = ""

        if (raw.startsWith("mailto:", ignoreCase = true)) {
            val uriStr = raw.substring(7)
            email = uriStr.substringBefore("?")
            if (uriStr.contains("?")) {
                val query = uriStr.substringAfter("?")
                for (param in query.split("&")) {
                    val kv = param.split("=")
                    if (kv.size == 2) {
                        if (kv[0].equals("subject", ignoreCase = true)) subject = java.net.URLDecoder.decode(kv[1], "UTF-8")
                        if (kv[0].equals("body", ignoreCase = true)) body = java.net.URLDecoder.decode(kv[1], "UTF-8")
                    }
                }
            }
        } else if (raw.startsWith("MATMSG:", ignoreCase = true)) {
            val content = raw.substringAfter("MATMSG:")
            val parts = content.split(";")
            for (p in parts) {
                val token = p.trim()
                when {
                    token.startsWith("TO:", ignoreCase = true) -> email = token.substring(3)
                    token.startsWith("SUB:", ignoreCase = true) -> subject = token.substring(4)
                    token.startsWith("BODY:", ignoreCase = true) -> body = token.substring(5)
                }
            }
        }

        val fields = mutableMapOf<String, String>()
        fields["To"] = email
        if (subject.isNotBlank()) fields["Subject"] = subject
        if (body.isNotBlank()) fields["Message"] = body

        return ParsedQrResult(
            rawText = raw,
            type = QrType.EMAIL,
            title = email,
            subtitle = if (subject.isNotBlank()) "Subject: $subject" else "Send email",
            emailAddress = email,
            emailSubject = subject,
            emailBody = body,
            fields = fields
        )
    }

    private fun parseSms(raw: String): ParsedQrResult {
        var number = ""
        var message = ""

        if (raw.startsWith("smsto:", ignoreCase = true)) {
            val content = raw.substring(6)
            number = content.substringBefore(":")
            message = content.substringAfter(":", "")
        } else if (raw.startsWith("sms:", ignoreCase = true)) {
            val content = raw.substring(4)
            number = content.substringBefore("?")
            if (content.contains("?body=")) {
                message = java.net.URLDecoder.decode(content.substringAfter("?body="), "UTF-8")
            }
        }

        val fields = mutableMapOf<String, String>()
        fields["Number"] = number
        if (message.isNotBlank()) fields["Message"] = message

        return ParsedQrResult(
            rawText = raw,
            type = QrType.SMS,
            title = number,
            subtitle = if (message.isNotBlank()) message else "Send SMS",
            smsNumber = number,
            smsBody = message,
            fields = fields
        )
    }

    private fun parseGeo(raw: String): ParsedQrResult {
        val coords = raw.substring(4).substringBefore("?")
        val latLng = coords.split(",")
        val lat = latLng.getOrNull(0)?.toDoubleOrNull()
        val lng = latLng.getOrNull(1)?.toDoubleOrNull()

        return ParsedQrResult(
            rawText = raw,
            type = QrType.GEO,
            title = "$lat, $lng",
            subtitle = "Geographic coordinates",
            geoLatitude = lat,
            geoLongitude = lng,
            fields = mapOf("Latitude" to "${lat ?: 0.0}", "Longitude" to "${lng ?: 0.0}")
        )
    }
}
