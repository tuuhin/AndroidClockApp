package com.eva.clockapp.features.alarms.presentation.create_alarm.screens

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.eva.clockapp.features.alarms.presentation.create_alarm.state.CreateAlarmState
import com.eva.clockapp.features.alarms.presentation.play_alarm.PlayAlarmsScreen
import kotlinx.datetime.atTime
import kotlinx.datetime.toKotlinLocalDate
import java.time.LocalDate

@Composable
fun PlayAlarmsSneakPeekScreen(
	state: CreateAlarmState,
	modifier: Modifier = Modifier,
) {

	val lifecyleOwner = LocalLifecycleOwner.current
	val activity = LocalActivity.current

	DisposableEffect(lifecyleOwner) {

		activity?.window?.let { window ->
			val controller = WindowCompat.getInsetsController(window, window.decorView)
			controller.systemBarsBehavior =
				WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
			controller.hide(WindowInsetsCompat.Type.systemBars())
		}

		onDispose {
			activity?.window?.let { window ->
				val controller = WindowCompat.getInsetsController(window, window.decorView)
				controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
				controller.show(WindowInsetsCompat.Type.systemBars())
			}
		}
	}

	val dateTime = remember(state.selectedTime) {
		val date = LocalDate.now().toKotlinLocalDate()
		date.atTime(state.selectedTime)
	}

	PlayAlarmsScreen(
		dateTime = dateTime,
		labelText = state.labelState,
		backgroundImage = state.backgroundImageUri,
		isPreview = false,
		onStopAlarm = { },
		onSnoozeAlarm = {},
		modifier = modifier.fillMaxSize()
	)
}