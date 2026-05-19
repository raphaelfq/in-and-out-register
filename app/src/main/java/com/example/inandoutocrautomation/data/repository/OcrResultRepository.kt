package com.example.inandoutocrautomation.data.repository

import com.example.inandoutocrautomation.data.local.AppDatabase
import com.example.inandoutocrautomation.data.local.OcrResultEntity
import kotlinx.coroutines.flow.Flow

class OcrResultRepository(private val database: AppDatabase) {

    fun getAll(): Flow<List<OcrResultEntity>> = database.ocrResultDao().getAll()

    suspend fun save(imageUri: String, extractedTime: String, scheduledTime: String) {
        database.ocrResultDao().insert(
            OcrResultEntity(
                imageUri = imageUri,
                extractedTime = extractedTime,
                scheduledTime = scheduledTime
            )
        )
    }

    suspend fun delete(result: OcrResultEntity) {
        database.ocrResultDao().delete(result)
    }
}
