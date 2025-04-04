package com.eva.clockapp.activities

import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.eva.clockapp.core.constants.ClockAppIntents
import com.eva.clockapp.features.alarms.data.services.AlarmsControllerService
import com.eva.clockapp.features.alarms.presentation.play_alarm.PlayAlarmsScreen
import com.eva.clockapp.features.settings.data.utils.is24HrFormat
import com.eva.clockapp.features.settings.domain.models.AlarmVolumeControlOption
import com.eva.clockapp.features.settings.domain.repository.AlarmSettingsRepository
import com.eva.clockapp.ui.theme.ClockAppTheme
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.koin.android.ext.android.inject

class AlarmsActivity : ComponentActivity() {

	private val settingsRepo by inject<AlarmSettingsRepository>()

	private val keyguardManager by lazy { getSystemService<KeyguardManager>() }

	private val finishActivityReceiver = object : BroadcastReceiver() {
		override fun onReceive(context: Context, intent: Intent) {
			if (intent.action != ClockAppIntents.ACTION_FINISH_ALARM_ACTIVITY) return
			finishAndRemoveTask()
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		ContextCompat.registerReceiver(
			this@AlarmsActivity,
			finishActivityReceiver,
			IntentFilter(ClockAppIntents.ACTION_FINISH_ALARM_ACTIVITY),
			ContextCompat.RECEIVER_EXPORTED
		)

		enableEdgeToEdge()
		hideSystemBars()
		turnOffKeyguard()

		val alarmId = intent.getIntExtra(ClockAppIntents.EXTRA_ALARMS_ALARMS_ID, -1)
		if (alarmId == -1) finishAndRemoveTask()

		val timeInMillis = intent.getLongExtra(ClockAppIntents.EXTRAS_ALARMS_TIME_IN_MILLIS, 0)
		val labelText = intent.getStringExtra(ClockAppIntents.EXTRAS_ALARMS_LABEL_TEXT)
		val imageUri = intent.getStringExtra(ClockAppIntents.EXTRAS_ALARM_BACKGROUND_IMAGE_URI)

		val dateTime = Instant.Companion.fromEpochMilliseconds(timeInMillis)
			.toLocalDateTime(TimeZone.Companion.currentSystemDefault())

		setContent {
			ClockAppTheme {
				PlayAlarmsScreen(
					dateTime = dateTime,
					labelText = labelText,
					backgroundImage = imageUri,
					is24HrFormat = settingsRepo.settingsValue.timeFormat.is24HrFormat,
					onStopAlarm = { stopAlarm(alarmId) },
					onSnoozeAlarm = { snoozeAlarm(alarmId) },
					modifier = Modifier.Companion.fillMaxSize()
				)
			}
		}
	}

	override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
		val alarmId = intent.getIntExtra(ClockAppIntents.EXTRA_ALARMS_ALARMS_ID, -1)
		if (alarmId == -1) finishAndRemoveTask()

		val isVolumeButtons = arrayOf(KeyEvent.KEYCODE_VOLUME_DOWN, KeyEvent.KEYCODE_VOLUME_UP)

		if (keyCode !in isVolumeButtons) return super.onKeyDown(keyCode, event)

		val settings = settingsRepo.settingsValue

		when (settings.volumeControl) {
			AlarmVolumeControlOption.NONE -> return true
			AlarmVolumeControlOption.STOP_ALARM -> stopAlarm(alarmId)
			AlarmVolumeControlOption.SNOOZE_ALARM -> snoozeAlarm(alarmId)
			AlarmVolumeControlOption.CONTROL_ALARM_VOLUME ->
				controlAlarmVolume(alarmId, keyCode == KeyEvent.KEYCODE_VOLUME_UP)
		}

		return false
	}

	override fun onDestroy() {
		unregisterReceiver(finishActivityReceiver)
		super.onDestroy()
	}

	private fun stopAlarm(alarmId: Int) {
		try {
			val intent = Intent(this, AlarmsControllerService::class.java).apply {
				action = ClockAppIntents.ACTION_CANCEL_ALARM
				data = ClockAppIntents.alarmIntentData(alarmId)
			}
			applicationContext.startService(intent)
		} catch (e: Exception) {
			e.printStackTrace()
		}
	}

	private fun snoozeAlarm(alarmId: Int) {
		try {
			val intent = Intent(this, AlarmsControllerService::class.java).apply {
				action = ClockAppIntents.ACTION_SNOOZE_ALARM
				data = ClockAppIntents.alarmIntentData(alarmId)
				putExtra(ClockAppIntents.EXTRA_ALARMS_ALARMS_ID, alarmId)
			}
			applicationContext.startService(intent)
		} catch (e: Exception) {
			e.printStackTrace()
		}
	}

	private fun controlAlarmVolume(alarmId: Int, isIncrease: Boolean) {
		try {
			val intent = Intent(this, AlarmsControllerService::class.java).apply {
				action = ClockAppIntents.ACTION_CHANGE_ALARM_VOLUME
				data = ClockAppIntents.alarmIntentData(alarmId)
				putExtra(ClockAppIntents.EXTRAS_ALARM_VOLUME_INCREASE, isIncrease)
			}
			applicationContext.startService(intent)
		} catch (e: Exception) {
			e.printStackTrace()
		}
	}

	private fun hideSystemBars() {
		val controller = WindowCompat.getInsetsController(window, window.decorView)
		controller.systemBarsBehavior =
			WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

		controller.hide(WindowInsetsCompat.Type.systemBars())
	}

	@Suppress("DEPRECATION")
	private fun turnOffKeyguard() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
			// activity will be shown even if the device is locked
			setShowWhenLocked(true)
			// turns the screen on
			setTurnScreenOn(true)
		} else {
			window.addFlags(
				WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
						WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
			)
		}
		// keep the screen on
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
		// dismiss the lock screen if its shown
		keyguardManager?.requestDismissKeyguard(this, null)
	}
}