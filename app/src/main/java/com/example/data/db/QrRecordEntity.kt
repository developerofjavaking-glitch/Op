package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "qr_records")
data class QrRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val rawText: String,
    val type: String, // Matches QrType.name
    val title: String,
    val subtitle: String,
    val isFavorite: Boolean = false,
    val isGenerated: Boolean = false, // false = scanned, true = created in generator
    val timestamp: Long = System.currentTimeMillis()
)
