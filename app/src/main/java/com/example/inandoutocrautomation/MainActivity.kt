package com.example.inandoutocrautomation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.inandoutocrautomation.presentation.AppNavigation
import com.example.inandoutocrautomation.ui.theme.InAndOutOCRAutomationTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            InAndOutOCRAutomationTheme {
                AppNavigation()
            }
        }
    }
}
