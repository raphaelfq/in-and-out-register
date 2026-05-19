package com.example.inandoutocrautomation.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface OcrResultDao {
    @Insert
    suspend fun insert(result: OcrResultEntity)

    @Query("SELECT * FROM ocr_results ORDER BY createdAt DESC")
    fun getAll(): Flow<List<OcrResultEntity>>

    @Delete
    suspend fun delete(result: OcrResultEntity)
}
