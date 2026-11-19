package com.example.mysihat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.mysihat.data.SupabaseDatabaseManager
import com.example.mysihat.ui.navigation.MainAppContainer
import com.example.mysihat.ui.theme.BackgroundLight
import com.example.mysihat.ui.theme.MySihatTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SupabaseDatabaseManager.init(this)
        enableEdgeToEdge()
        setContent {
            MySihatTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BackgroundLight
                ) {
                    MainAppContainer()
                }
            }
        }
    }
}
