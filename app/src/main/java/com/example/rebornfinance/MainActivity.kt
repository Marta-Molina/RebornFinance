package com.example.rebornfinance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.rebornfinance.navigation.AppNavigation
import com.example.rebornfinance.ui.theme.RebornFinanceTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RebornFinanceTheme {
                AppNavigation()
            }
        }
    }
}
