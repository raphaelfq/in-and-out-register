package com.example.inandoutocrautomation.domain

import android.content.Context
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.inandoutocrautomation.worker.VibrationWorker
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class AlarmScheduler {

    fun schedule(context: Context, extractedTime: String): Result<String> {
        val parts = extractedTime.split(":")
        if (parts.size != 2) {
            return Result.failure(IllegalArgumentException("Invalid time format"))
        }

        val hour = parts[0].toIntOrNull()
        val minute = parts[1].toIntOrNull()
        if (hour == null || minute == null || hour !in 0..23 || minute !in 0..59) {
            return Result.failure(IllegalArgumentException("Invalid time: $extractedTime"))
        }

        return try {
            val now = Calendar.getInstance()
            val target = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            if (target.before(now)) {
                target.add(Calendar.DAY_OF_YEAR, 1)
            }

            val delayInMillis = target.timeInMillis - now.timeInMillis

            val workRequest = OneTimeWorkRequestBuilder<VibrationWorker>()
                .setInitialDelay(delayInMillis, TimeUnit.MILLISECONDS)
                .build()

            WorkManager.getInstance(context).enqueue(workRequest)

            val formattedTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(target.time)
            Result.success(formattedTime)
        } catch (e: Exception) {
            Log.e("AlarmScheduler", "Failed to schedule: ${e.message}", e)
            Result.failure(e)
        }
    }
}
