package com.example.inandoutocrautomation.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.inandoutocrautomation.data.local.AppDatabase
import com.example.inandoutocrautomation.data.local.OcrResultEntity
import com.example.inandoutocrautomation.data.repository.OcrResultRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = OcrResultRepository(AppDatabase.getInstance(application))

    val results: StateFlow<List<OcrResultEntity>> = repository.getAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun delete(result: OcrResultEntity) {
        viewModelScope.launch {
            repository.delete(result)
        }
    }
}
