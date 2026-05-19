package com.example.inandoutocrautomation.domain

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class OcrTimeExtractor {

    fun extractTime(
        context: Context,
        imageUri: Uri,
        onSuccess: (List<String>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

        try {
            val image = InputImage.fromFilePath(context, imageUri)

            recognizer.process(image)
                .addOnCompleteListener { recognizer.close() }
                .addOnSuccessListener { visionText ->
                    val extractedText = visionText.text
                    Log.d("OCR", "Extracted Text: $extractedText")

                    val timePattern = "\\d{1,2}:\\d{2}".toRegex()
                    val times = timePattern.findAll(extractedText)
                        .map { it.value }
                        .filter { time ->
                            val parts = time.split(":")
                            val hour = parts[0].toIntOrNull() ?: return@filter false
                            val minute = parts[1].toIntOrNull() ?: return@filter false
                            hour in 0..23 && minute in 0..59
                        }
                        .distinct()
                        .toList()

                    if (times.isNotEmpty()) {
                        Log.d("OCR", "Extracted Times: $times")
                        onSuccess(times)
                    } else {
                        onFailure("No valid time found in image")
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("OCR", "Text recognition failed: ${e.message}", e)
                    onFailure("Text recognition failed")
                }
        } catch (e: Exception) {
            Log.e("OCR", "Failed to process image: ${e.message}", e)
            recognizer.close()
            onFailure("Failed to process image")
        }
    }
}
