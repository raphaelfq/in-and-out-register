package com.example.inandoutocrautomation

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import java.util.concurrent.TimeUnit
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.text.SimpleDateFormat
import java.util.*
import android.provider.Settings
import androidx.core.content.ContextCompat
import androidx.work.Worker
import androidx.work.WorkerParameters

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            JobRegisterApp()
        }
    }
}

@Composable
fun JobRegisterApp() {
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var extractedText by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    // Image picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri // Store the selected image URI
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = { imagePickerLauncher.launch("image/*") },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Add Photo to be Analyzed")
        }

        selectedImageUri?.let { uri ->
            Image(
                painter = rememberAsyncImagePainter(uri),
                contentDescription = "Selected Image",
                modifier = Modifier
                    .height(300.dp)
                    .fillMaxWidth()
                    .padding(16.dp),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Button to analyze image and set alarm
            Button(onClick = { processImage(uri, context, onTextExtracted = { extractedText = it }) }) {
                Text(text = "Analyze and Set Alarm")
            }
        }

        // Display extracted text
        extractedText?.let {
            Text(text = "Extracted Text: $it", modifier = Modifier.padding(16.dp))
        }
    }
}


class VibrationWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        // Get the vibrator service to vibrate the device when the alarm triggers
        val vibrator = applicationContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(5000, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(500) // Fallback for older devices
        }

        return Result.success()
    }
}

// Function to process image and extract time
fun processImage(imageUri: Uri, context: Context, onTextExtracted: (String) -> Unit) {
    val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    try {
        val image = InputImage.fromFilePath(context, imageUri)

        recognizer.process(image)
            .addOnSuccessListener { visionText ->
                val extractedText = visionText.text
                Log.d("OCR", "Extracted Text: $extractedText")

                // Extract time using regex (pattern HH:mm)
                val timePattern = "\\d{1,2}:\\d{2}".toRegex()
                val timeMatch = timePattern.find(extractedText)?.value

                if (timeMatch != null) {
                    Log.d("OCR", "Extracted Time: $timeMatch")
                    onTextExtracted("Extracted Time: $timeMatch")
                    setAlarmWithWorkManager(context, timeMatch) // Schedule the task with WorkManager
                } else {
                    Toast.makeText(context, "No valid time found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Log.e("OCR", "Text recognition failed: ${e.message}", e)
                Toast.makeText(context, "Failed to extract text", Toast.LENGTH_SHORT).show()
            }
    } catch (e: Exception) {
        Log.e("OCR", "Failed to process image: ${e.message}", e)
        Toast.makeText(context, "Failed to process image", Toast.LENGTH_SHORT).show()
    }
}



// Function to set an alarm for 2 minutes after the extracted time


// Function to schedule the "alarm" using WorkManager
fun setAlarmWithWorkManager(context: Context, extractedTime: String) {
    val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val calendar = Calendar.getInstance()

    try {
        // Parse the extracted time from the OCR
        val parsedTime = dateFormat.parse(extractedTime)
        calendar.time = parsedTime!!

        // Add 2 minutes to the extracted time
        calendar.add(Calendar.SECOND, 5)

        // Calculate the delay from the current time
        val delayInMillis = calendar.timeInMillis - System.currentTimeMillis()

        val workRequest: WorkRequest = OneTimeWorkRequestBuilder<VibrationWorker>()
            .setInitialDelay(delayInMillis, TimeUnit.MILLISECONDS)
            .build()

        // Enqueue the WorkManager task
        WorkManager.getInstance(context).enqueue(workRequest)

        // Show a confirmation message
        val formattedTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time)
        Toast.makeText(context, "Task scheduled for $formattedTime", Toast.LENGTH_LONG).show()

    } catch (e: Exception) {
        Log.e("AlarmSet", "Failed to schedule task: ${e.message}", e)
        Toast.makeText(context, "Failed to schedule task", Toast.LENGTH_SHORT).show()
    }
}
// AlarmReceiver class (you need to define this to handle the alarm action)
class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Handle what happens when the alarm goes off
        Toast.makeText(context, "Alarm Triggered!", Toast.LENGTH_LONG).show()
    }
}
