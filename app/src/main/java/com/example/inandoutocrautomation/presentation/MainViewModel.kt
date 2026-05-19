package com.example.inandoutocrautomation.presentation

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.inandoutocrautomation.data.local.AppDatabase
import com.example.inandoutocrautomation.data.repository.OcrResultRepository
import com.example.inandoutocrautomation.domain.AlarmScheduler
import com.example.inandoutocrautomation.domain.OcrTimeExtractor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow<MainUiState>(MainUiState.Idle)
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    private val ocrExtractor = OcrTimeExtractor()
    private val alarmScheduler = AlarmScheduler()
    private val repository = OcrResultRepository(AppDatabase.getInstance(application))

    fun onImageSelected(uri: Uri) {
        _uiState.value = MainUiState.ImageSelected(uri)
    }

    fun processImage(uri: Uri) {
        _uiState.value = MainUiState.Processing
        ocrExtractor.extractTime(
            context = getApplication(),
            imageUri = uri,
            onSuccess = { extractedTimes ->
                if (extractedTimes.size == 1) {
                    scheduleSelectedTimes(extractedTimes, uri)
                } else {
                    _uiState.value = MainUiState.TimesFound(uri, extractedTimes)
                }
            },
            onFailure = { error ->
                _uiState.value = MainUiState.Error(error)
            }
        )
    }

    fun scheduleSelectedTimes(times: List<String>, imageUri: Uri) {
        val scheduledTimes = mutableListOf<String>()
        val extractedTimes = mutableListOf<String>()

        for (time in times) {
            val result = alarmScheduler.schedule(getApplication(), time)
            result.onSuccess { scheduledFor ->
                extractedTimes.add(time)
                scheduledTimes.add(scheduledFor)
                viewModelScope.launch {
                    repository.save(
                        imageUri = imageUri.toString(),
                        extractedTime = time,
                        scheduledTime = scheduledFor
                    )
                }
            }.onFailure { error ->
                _uiState.value = MainUiState.Error(error.message ?: "Failed to schedule alarm")
                return
            }
        }

        _uiState.value = MainUiState.Success(extractedTimes, scheduledTimes)
    }

    fun openCamera() {
        _uiState.value = MainUiState.Camera
    }

    fun reset() {
        _uiState.value = MainUiState.Idle
    }
}
