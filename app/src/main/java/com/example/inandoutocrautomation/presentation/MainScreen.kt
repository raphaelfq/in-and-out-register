package com.example.inandoutocrautomation.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter

@Composable
fun MainScreen(
    onNavigateToHistory: () -> Unit = {},
    viewModel: MainViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.onImageSelected(it) }
    }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context, Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
        if (granted) {
            viewModel.openCamera()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (val state = uiState) {
            is MainUiState.Idle -> {
                Button(onClick = { imagePickerLauncher.launch("image/*") }) {
                    Text(text = "Pick from Gallery")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = {
                    if (hasCameraPermission) {
                        viewModel.openCamera()
                    } else {
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                }) {
                    Text(text = "Take Photo")
                }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedButton(onClick = onNavigateToHistory) {
                    Text(text = "View History")
                }
            }

            is MainUiState.Camera -> {
                CameraScreen(
                    onImageCaptured = { uri -> viewModel.onImageSelected(uri) },
                    onCancel = { viewModel.reset() }
                )
            }

            is MainUiState.ImageSelected -> {
                Image(
                    painter = rememberAsyncImagePainter(state.imageUri),
                    contentDescription = "Selected Image",
                    modifier = Modifier
                        .height(300.dp)
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    contentScale = ContentScale.Crop
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { viewModel.processImage(state.imageUri) }) {
                        Text(text = "Analyze and Set Alarm")
                    }
                    Button(onClick = { viewModel.reset() }) {
                        Text(text = "Pick Another")
                    }
                }
            }

            is MainUiState.Processing -> {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = "Analyzing image...")
            }

            is MainUiState.TimesFound -> {
                Text(
                    text = "Found ${state.times.size} times:",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))

                val selectedTimes = remember { mutableStateListOf<String>() }

                state.times.forEach { time ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = time in selectedTimes,
                            onCheckedChange = { checked ->
                                if (checked) selectedTimes.add(time) else selectedTimes.remove(time)
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = time, style = MaterialTheme.typography.bodyLarge)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { viewModel.scheduleSelectedTimes(selectedTimes.toList(), state.imageUri) },
                        enabled = selectedTimes.isNotEmpty()
                    ) {
                        Text("Set Alarms (${selectedTimes.size})")
                    }
                    OutlinedButton(onClick = { viewModel.reset() }) {
                        Text("Cancel")
                    }
                }
            }

            is MainUiState.Success -> {
                Text(
                    text = "Alarms Set",
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(8.dp))
                state.extractedTimes.zip(state.scheduledTimes).forEach { (extracted, scheduled) ->
                    Text(
                        text = "$extracted -> alarm at $scheduled",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = { viewModel.reset() }) {
                    Text(text = "Pick New Image")
                }
            }

            is MainUiState.Error -> {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { viewModel.reset() }) {
                    Text(text = "Try Again")
                }
            }
        }
    }
}
