package com.eva.clockapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.eva.clockapp.core.navigation.AppNavHost
import com.eva.clockapp.ui.theme.ClockAppTheme

class MainActivity : ComponentActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {

		installSplashScreen()
		super.onCreate(savedInstanceState)

		enableEdgeToEdge()

		setContent {
			ClockAppTheme {
			}
		}
	}
}
