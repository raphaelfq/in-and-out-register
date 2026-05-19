package com.example.inandoutocrautomation.presentation

import android.net.Uri

sealed interface MainUiState {
    data object Idle : MainUiState
    data class ImageSelected(val imageUri: Uri) : MainUiState
    data object Processing : MainUiState
    data class Success(val extractedTimes: List<String>, val scheduledTimes: List<String>) : MainUiState
    data class TimesFound(val imageUri: Uri, val times: List<String>) : MainUiState
    data class Error(val message: String) : MainUiState
    data object Camera : MainUiState
}
