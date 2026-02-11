package com.example.assistant_startup

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.assistant_startup.ui.features.AppNavHost
import com.example.assistant_startup.ui.theme.Assistant_StartupTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Assistant_StartupTheme(){
                AppNavHost()
            }
        }
    }
}

@Preview
@Composable
fun AppPreview() {
    Assistant_StartupTheme(){
        AppNavHost()
    }
}