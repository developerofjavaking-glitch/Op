package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.QrRecordEntity
import com.example.data.model.QrType
import com.example.ui.theme.ClassicAmber
import com.example.ui.theme.ClassicBg
import com.example.ui.theme.ClassicBlue
import com.example.ui.theme.ClassicBlueContainer
import com.example.ui.theme.ClassicBorder
import com.example.ui.theme.ClassicRed
import com.example.ui.theme.ClassicSurface
import com.example.ui.theme.ClassicSurfaceVariant
import com.example.ui.theme.TextDarkMuted
import com.example.ui.theme.TextDarkPrimary
import com.example.ui.theme.TextDarkSecondary
import com.example.ui.theme.TextLight
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    records: List<QrRecordEntity>,
    onSelectRecord: (QrRecordEntity) -> Unit,
    onToggleFavorite: (id: Long, isFavorite: Boolean) -> Unit,
    onDeleteRecord: (id: Long) -> Unit,
    onClearAll: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("ALL") }
    var showClearConfirmDialog by remember { mutableStateOf(false) }

    val filteredRecords = remember(records, searchQuery, selectedFilter) {
        records.filter { item ->
            val matchesFilter = when (selectedFilter) {
                "SCANNED" -> !item.isGenerated
                "CREATED" -> item.isGenerated
                "FAVORITES" -> item.isFavorite
                else -> true
            }
            val matchesSearch = searchQuery.isBlank() ||
                    item.title.contains(searchQuery, ignoreCase = true) ||
                    item.subtitle.contains(searchQuery, ignoreCase = true) ||
                    item.rawText.contains(searchQuery, ignoreCase = true)
            matchesFilter && matchesSearch
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(ClassicBg)
            .padding(horizontal = 20.dp, vertical = 18.dp)
    ) {
        item {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "SAVED CODES",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = ClassicBlue,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "History & Bookmarks",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDarkPrimary
                    )
                }

                if (records.isNotEmpty()) {
                    IconButton(
                        onClick = { showClearConfirmDialog = true },
                        modifier = Modifier.testTag("clear_history_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteSweep,
                            contentDescription = "Clear All History",
                            tint = ClassicRed,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Search Bar (Large, clean, high contrast)
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search by name, link, or text...", fontSize = 14.sp, color = TextDarkMuted) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = ClassicBlue, modifier = Modifier.size(20.dp))
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("history_search_field"),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ClassicBlue,
                    unfocusedBorderColor = ClassicBorder,
                    focusedContainerColor = ClassicSurface,
                    unfocusedContainerColor = ClassicSurface,
                    focusedTextColor = TextDarkPrimary,
                    unfocusedTextColor = TextDarkPrimary
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Filter Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                HistoryFilterPill("ALL", "All (${records.size})", selectedFilter == "ALL") { selectedFilter = "ALL" }
                HistoryFilterPill("SCANNED", "Scanned", selectedFilter == "SCANNED") { selectedFilter = "SCANNED" }
                HistoryFilterPill("CREATED", "Created", selectedFilter == "CREATED") { selectedFilter = "CREATED" }
                HistoryFilterPill("FAVORITES", "Favorites", selectedFilter == "FAVORITES") { selectedFilter = "FAVORITES" }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        if (filteredRecords.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(ClassicBlueContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                tint = ClassicBlue,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = if (searchQuery.isNotBlank()) "No records matching your search" else "No saved QR codes yet",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextDarkPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Codes you scan or generate will appear here automatically",
                            fontSize = 14.sp,
                            color = TextDarkSecondary
                        )
                    }
                }
            }
        } else {
            items(filteredRecords, key = { it.id }) { record ->
                HistoryItemCard(
                    record = record,
                    onClick = { onSelectRecord(record) },
                    onToggleFavorite = { onToggleFavorite(record.id, !record.isFavorite) },
                    onDelete = { onDeleteRecord(record.id) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            title = { Text("Clear All History?", color = TextDarkPrimary, fontWeight = FontWeight.Bold, fontSize = 20.sp) },
            text = { Text("Are you sure you want to permanently delete all saved and scanned QR codes?", color = TextDarkSecondary, fontSize = 15.sp) },
            confirmButton = {
                Button(
                    onClick = {
                        onClearAll()
                        showClearConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ClassicRed, contentColor = TextLight),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Clear All", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmDialog = false }) {
                    Text("Cancel", color = TextDarkSecondary, fontWeight = FontWeight.SemiBold)
                }
            },
            containerColor = ClassicSurface,
            shape = RoundedCornerShape(18.dp)
        )
    }
}

@Composable
fun HistoryFilterPill(
    key: String,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) ClassicBlueContainer else ClassicSurface)
            .border(
                1.5.dp,
                if (isSelected) ClassicBlue else ClassicBorder,
                RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) ClassicBlue else TextDarkSecondary
        )
    }
}

@Composable
fun HistoryItemCard(
    record: QrRecordEntity,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit,
    onDelete: () -> Unit
) {
    val qrType = remember(record.type) {
        try {
            QrType.valueOf(record.type)
        } catch (_: Exception) {
            QrType.TEXT
        }
    }

    val dateFormat = remember { SimpleDateFormat("MMM d, yyyy • h:mm a", Locale.getDefault()) }
    val dateStr = remember(record.timestamp) { dateFormat.format(Date(record.timestamp)) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ClassicSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(ClassicBorder))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(qrType.color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = qrType.icon,
                    contentDescription = null,
                    tint = qrType.color,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = record.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDarkPrimary,
                        maxLines = 1,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (record.isGenerated) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(ClassicBlueContainer)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("Created", fontSize = 10.sp, color = ClassicBlue, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = record.subtitle,
                    fontSize = 13.sp,
                    color = TextDarkSecondary,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = dateStr,
                    fontSize = 11.sp,
                    color = TextDarkMuted
                )
            }

            IconButton(onClick = onToggleFavorite) {
                Icon(
                    imageVector = if (record.isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = "Save to favorites",
                    tint = if (record.isFavorite) ClassicAmber else TextDarkMuted,
                    modifier = Modifier.size(24.dp)
                )
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = TextDarkMuted,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}
