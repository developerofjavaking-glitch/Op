package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.QrRecordEntity
import com.example.data.model.ParsedQrResult
import com.example.data.repository.QrRepository
import com.example.util.QrParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class QrViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = QrRepository.getInstance(application)

    val historyRecords: StateFlow<List<QrRecordEntity>> = repository.allRecords
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _activeResult = MutableStateFlow<ParsedQrResult?>(null)
    val activeResult: StateFlow<ParsedQrResult?> = _activeResult.asStateFlow()

    private val _isCurrentFavorite = MutableStateFlow(false)
    val isCurrentFavorite: StateFlow<Boolean> = _isCurrentFavorite.asStateFlow()

    private var currentRecordId: Long = 0L

    fun onQrScanned(rawText: String) {
        val parsed = QrParser.parse(rawText)
        _activeResult.value = parsed
        _isCurrentFavorite.value = false

        viewModelScope.launch {
            val id = repository.saveScannedResult(parsed)
            currentRecordId = id
        }
    }

    fun selectRecordFromHistory(record: QrRecordEntity) {
        val parsed = QrParser.parse(record.rawText)
        _activeResult.value = parsed
        _isCurrentFavorite.value = record.isFavorite
        currentRecordId = record.id
    }

    fun dismissResult() {
        _activeResult.value = null
    }

    fun toggleCurrentResultFavorite() {
        val newFav = !_isCurrentFavorite.value
        _isCurrentFavorite.value = newFav
        if (currentRecordId != 0L) {
            viewModelScope.launch {
                repository.toggleFavorite(currentRecordId, newFav)
            }
        }
    }

    fun toggleFavorite(id: Long, isFavorite: Boolean) {
        viewModelScope.launch {
            repository.toggleFavorite(id, isFavorite)
            if (id == currentRecordId) {
                _isCurrentFavorite.value = isFavorite
            }
        }
    }

    fun saveGeneratedQr(rawText: String, title: String, typeName: String) {
        viewModelScope.launch {
            repository.saveGeneratedResult(rawText, title, typeName)
        }
    }

    fun deleteRecord(id: Long) {
        viewModelScope.launch {
            repository.deleteRecord(id)
            if (id == currentRecordId) {
                _activeResult.value = null
            }
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearHistory()
            _activeResult.value = null
        }
    }
}
