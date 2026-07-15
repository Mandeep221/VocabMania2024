package com.msarangal.vocabmania.presentation.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.msarangal.vocabmania.presentation.compose.navigation.AppNavHost
import com.msarangal.vocabmania.presentation.compose.theme.VocabManiaTheme

/**
 * Compose entry point for the rebuilt VocabMania experience.
 * Legacy Java Activities remain in the APK but are unreachable from Compose UI.
 */
class AppShellActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VocabManiaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    AppNavHost()
                }
            }
        }
    }
}
