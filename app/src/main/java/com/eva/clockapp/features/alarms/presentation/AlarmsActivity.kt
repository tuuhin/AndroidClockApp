package com.eva.clockapp.features.alarms.presentation

import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.eva.clockapp.core.constants.ClockAppIntents
import com.eva.clockapp.features.alarms.data.services.AlarmsControllerService
import com.eva.clockapp.ui.theme.ClockAppTheme

class AlarmsActivity : ComponentActivity() {

	private val keyguardManager by lazy { getSystemService<KeyguardManager>() }

	private val finishActivityReceiver = object : BroadcastReceiver() {
		override fun onReceive(context: Context, intent: Intent) {
			if (intent.action != ClockAppIntents.ACTION_FINISH_ALARMS_ACTIVITY) return
			finishAndRemoveTask()
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

		enableEdgeToEdge()
		hideSystemBars()
		turnOffKeyguard()

		// add receiver to finish this activity
		ContextCompat.registerReceiver(
			this, finishActivityReceiver,
			IntentFilter(ClockAppIntents.ACTION_FINISH_ALARMS_ACTIVITY),
			ContextCompat.RECEIVER_NOT_EXPORTED
		)


		val alarmId = intent.getIntExtra(ClockAppIntents.EXTRA_ALARMS_ALARMS_ID, -1)
		if (alarmId == -1) finishAndRemoveTask()

		setContent {
			ClockAppTheme {
				Surface(color = MaterialTheme.colorScheme.background) {
//					Column(
//						modifier = Modifier.fillMaxSize(),
//						verticalArrangement = Arrangement.Center
//					) {
//						// TODO: Change to the original model when done
//						PlayAlarmsScreen(
//							alarmModel = AlarmPreviewFakes.FAKE_ALARMS_MODEL,
//							onStopAlarm = { finish() }
//						)
//					}
				}
			}
		}
	}

	override fun onDestroy() {
		try {
			val intent = Intent(applicationContext, AlarmsControllerService::class.java)
			applicationContext.stopService(intent)
		} catch (e: Exception) {
			e.printStackTrace()
		}
		// remove the receiver
		unregisterReceiver(finishActivityReceiver)
		super.onDestroy()
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