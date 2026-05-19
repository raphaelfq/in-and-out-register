package com.example.inandoutocrautomation.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ocr_results")
data class OcrResultEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val imageUri: String,
    val extractedTime: String,
    val scheduledTime: String,
    val createdAt: Long = System.currentTimeMillis()
)
