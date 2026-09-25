package com.example.data.repository

import android.content.Context
import com.example.data.db.AppDatabase
import com.example.data.db.QrRecordEntity
import com.example.data.model.ParsedQrResult
import com.example.util.QrParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class QrRepository(context: Context) {

    private val db = AppDatabase.getInstance(context)
    private val dao = db.qrRecordDao()

    val allRecords: Flow<List<QrRecordEntity>> = dao.getAllRecords()
    val scannedRecords: Flow<List<QrRecordEntity>> = dao.getScannedRecords()
    val generatedRecords: Flow<List<QrRecordEntity>> = dao.getGeneratedRecords()
    val favoriteRecords: Flow<List<QrRecordEntity>> = dao.getFavoriteRecords()

    suspend fun saveScannedResult(result: ParsedQrResult): Long = withContext(Dispatchers.IO) {
        val existing = dao.findExistingRecord(result.rawText, isGenerated = false)
        val entity = QrRecordEntity(
            id = existing?.id ?: 0,
            rawText = result.rawText,
            type = result.type.name,
            title = result.title,
            subtitle = result.subtitle,
            isFavorite = existing?.isFavorite ?: false,
            isGenerated = false,
            timestamp = System.currentTimeMillis()
        )
        dao.insertRecord(entity)
    }

    suspend fun saveGeneratedResult(rawText: String, title: String, typeName: String): Long = withContext(Dispatchers.IO) {
        val parsed = QrParser.parse(rawText)
        val existing = dao.findExistingRecord(rawText, isGenerated = true)
        val entity = QrRecordEntity(
            id = existing?.id ?: 0,
            rawText = rawText,
            type = typeName,
            title = title.ifBlank { parsed.title },
            subtitle = parsed.subtitle,
            isFavorite = existing?.isFavorite ?: false,
            isGenerated = true,
            timestamp = System.currentTimeMillis()
        )
        dao.insertRecord(entity)
    }

    suspend fun toggleFavorite(id: Long, isFavorite: Boolean) = withContext(Dispatchers.IO) {
        dao.updateFavorite(id, isFavorite)
    }

    suspend fun deleteRecord(id: Long) = withContext(Dispatchers.IO) {
        dao.deleteById(id)
    }

    suspend fun clearHistory(isGeneratedOnly: Boolean? = null) = withContext(Dispatchers.IO) {
        if (isGeneratedOnly == null) {
            dao.clearAll()
        } else {
            dao.clearByGenerated(isGeneratedOnly)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: QrRepository? = null

        fun getInstance(context: Context): QrRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = QrRepository(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}
