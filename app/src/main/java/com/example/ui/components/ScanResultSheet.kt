package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ParsedQrResult
import com.example.data.model.QrType
import com.example.ui.theme.ClassicAmber
import com.example.ui.theme.ClassicBlue
import com.example.ui.theme.ClassicBlueContainer
import com.example.ui.theme.ClassicBorder
import com.example.ui.theme.ClassicSurface
import com.example.ui.theme.ClassicSurfaceVariant
import com.example.ui.theme.TextDarkMuted
import com.example.ui.theme.TextDarkPrimary
import com.example.ui.theme.TextDarkSecondary
import com.example.ui.theme.TextLight
import kotlinx.coroutines.launch
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanResultSheet(
    result: ParsedQrResult,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onDismiss: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = ClassicSurface,
        scrimColor = Color(0x66000000),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // QR Type Header Pill + Favorite Bookmark Icon
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(result.type.color.copy(alpha = 0.12f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = result.type.icon,
                            contentDescription = null,
                            tint = result.type.color,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = result.type.title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = result.type.color
                        )
                    }
                }

                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.testTag("favorite_toggle_button")
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Save to Bookmarks",
                        tint = if (isFavorite) ClassicAmber else TextDarkMuted,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main Title & Subtitle (High Contrast, Large Font)
            Text(
                text = result.title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextDarkPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = result.subtitle,
                fontSize = 15.sp,
                color = TextDarkSecondary,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Key Details Breakdown Box (if fields exist)
            if (result.fields.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = ClassicSurfaceVariant),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(ClassicBorder))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        result.fields.entries.forEachIndexed { index, entry ->
                            if (index > 0) {
                                Spacer(modifier = Modifier.height(10.dp))
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = entry.key,
                                    fontSize = 13.sp,
                                    color = TextDarkMuted,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = entry.value,
                                    fontSize = 15.sp,
                                    color = TextDarkPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
            }

            // Prominent Primary Action Button
            Button(
                onClick = {
                    executePrimaryAction(context, result)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("primary_qr_action_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = result.type.color,
                    contentColor = TextLight
                )
            ) {
                Icon(
                    imageVector = result.type.icon,
                    contentDescription = null,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = result.type.actionLabel,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quick Tools Row (Copy, Share, Google Search)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionChip(
                    icon = Icons.Default.ContentCopy,
                    label = "Copy",
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("QR Code", result.rawText))
                        Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                    }
                )

                QuickActionChip(
                    icon = Icons.Default.Share,
                    label = "Share",
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, result.rawText)
                        }
                        context.startActivity(Intent.createChooser(shareIntent, "Share Scanned QR"))
                    }
                )

                QuickActionChip(
                    icon = Icons.Default.Search,
                    label = "Search",
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val query = URLEncoder.encode(result.rawText, "UTF-8")
                        val searchIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=$query"))
                        try {
                            context.startActivity(searchIntent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Browser not available", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Rescan Button
            OutlinedButton(
                onClick = {
                    scope.launch {
                        sheetState.hide()
                        onDismiss()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("rescan_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextDarkPrimary)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Scan Again",
                    modifier = Modifier.size(20.dp),
                    tint = TextDarkPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Scan Another Code", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
fun QuickActionChip(
    icon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(ClassicSurface)
            .border(1.dp, ClassicBorder, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = ClassicBlue,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextDarkPrimary
            )
        }
    }
}

private fun executePrimaryAction(context: Context, result: ParsedQrResult) {
    try {
        when (result.type) {
            QrType.URL -> {
                val url = result.url ?: result.rawText
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
            }
            QrType.WIFI -> {
                val pass = result.wifiPassword ?: ""
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText("Wi-Fi Password", pass))
                Toast.makeText(
                    context,
                    if (pass.isNotBlank()) "Wi-Fi password copied: $pass" else "Open network: ${result.wifiSsid}",
                    Toast.LENGTH_LONG
                ).show()
            }
            QrType.PHONE -> {
                val phone = result.phoneNumber ?: result.rawText
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                context.startActivity(intent)
            }
            QrType.EMAIL -> {
                val email = result.emailAddress ?: ""
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:$email")
                    result.emailSubject?.let { putExtra(Intent.EXTRA_SUBJECT, it) }
                    result.emailBody?.let { putExtra(Intent.EXTRA_TEXT, it) }
                }
                context.startActivity(intent)
            }
            QrType.SMS -> {
                val num = result.smsNumber ?: ""
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("smsto:$num")
                    result.smsBody?.let { putExtra("sms_body", it) }
                }
                context.startActivity(intent)
            }
            QrType.GEO -> {
                val lat = result.geoLatitude
                val lng = result.geoLongitude
                val uri = if (lat != null && lng != null) {
                    Uri.parse("geo:$lat,$lng?q=$lat,$lng")
                } else {
                    Uri.parse(result.rawText)
                }
                val intent = Intent(Intent.ACTION_VIEW, uri)
                context.startActivity(intent)
            }
            QrType.CONTACT -> {
                val intent = Intent(Intent.ACTION_INSERT).apply {
                    type = android.provider.ContactsContract.RawContacts.CONTENT_TYPE
                    result.contactName?.let { putExtra(android.provider.ContactsContract.Intents.Insert.NAME, it) }
                    result.contactPhone?.let { putExtra(android.provider.ContactsContract.Intents.Insert.PHONE, it) }
                    result.contactEmail?.let { putExtra(android.provider.ContactsContract.Intents.Insert.EMAIL, it) }
                }
                context.startActivity(intent)
            }
            QrType.TEXT -> {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText("QR Text", result.rawText))
                Toast.makeText(context, "Text copied to clipboard", Toast.LENGTH_SHORT).show()
            }
        }
    } catch (e: Exception) {
        Toast.makeText(context, "Could not open action handler", Toast.LENGTH_SHORT).show()
    }
}
