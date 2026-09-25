package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface QrRecordDao {
    @Query("SELECT * FROM qr_records ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<QrRecordEntity>>

    @Query("SELECT * FROM qr_records WHERE isGenerated = 0 ORDER BY timestamp DESC")
    fun getScannedRecords(): Flow<List<QrRecordEntity>>

    @Query("SELECT * FROM qr_records WHERE isGenerated = 1 ORDER BY timestamp DESC")
    fun getGeneratedRecords(): Flow<List<QrRecordEntity>>

    @Query("SELECT * FROM qr_records WHERE isFavorite = 1 ORDER BY timestamp DESC")
    fun getFavoriteRecords(): Flow<List<QrRecordEntity>>

    @Query("SELECT * FROM qr_records WHERE id = :id LIMIT 1")
    suspend fun getRecordById(id: Long): QrRecordEntity?

    @Query("SELECT * FROM qr_records WHERE rawText = :rawText AND isGenerated = :isGenerated LIMIT 1")
    suspend fun findExistingRecord(rawText: String, isGenerated: Boolean): QrRecordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: QrRecordEntity): Long

    @Query("UPDATE qr_records SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: Long, isFavorite: Boolean)

    @Query("DELETE FROM qr_records WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM qr_records")
    suspend fun clearAll()

    @Query("DELETE FROM qr_records WHERE isGenerated = :isGenerated")
    suspend fun clearByGenerated(isGenerated: Boolean)
}
