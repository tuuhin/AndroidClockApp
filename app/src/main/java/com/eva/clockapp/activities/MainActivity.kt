package com.eva.clockapp.activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.eva.clockapp.BuildConfig
import com.eva.clockapp.core.navigation.AppNavHost
import com.eva.clockapp.features.alarms.data.worker.EnqueueDailyAlarmWorker
import com.eva.clockapp.ui.theme.ClockAppTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {

		installSplashScreen()
		super.onCreate(savedInstanceState)

		enableEdgeToEdge()

		if (BuildConfig.DEBUG) {
			lifecycleScope.launch {
				EnqueueDailyAlarmWorker.Companion.checkWorkerState(applicationContext)
			}
		}

		setContent {
			ClockAppTheme {
				Surface(color = MaterialTheme.colorScheme.background) {
					AppNavHost(modifier = Modifier.Companion.fillMaxSize())
				}
			}
		}
	}
}