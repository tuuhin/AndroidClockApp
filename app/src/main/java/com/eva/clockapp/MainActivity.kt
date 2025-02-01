package com.eva.clockapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.eva.clockapp.features.alarms.presentation.CreateAlarmScreen
import com.eva.clockapp.features.alarms.presentation.composables.ScrollableTimePicker
import com.eva.clockapp.ui.theme.ClockAppTheme
import kotlinx.datetime.LocalTime

class MainActivity : ComponentActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		enableEdgeToEdge()

		setContent {
			ClockAppTheme {
			}
		}
	}
}
