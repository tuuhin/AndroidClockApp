package com.eva.clockapp.features.alarms.data.receivers

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.getSystemService
import com.eva.clockapp.core.constants.ClockAppIntents
import com.eva.clockapp.features.alarms.data.services.AlarmsNotificationProvider
import com.eva.clockapp.features.alarms.domain.controllers.AlarmsController
import com.eva.clockapp.features.alarms.domain.repository.AlarmsRepository
import kotlinx.coroutines.Dispatchers
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private const val TAG = "UPCOMING_ALARM"

class UpcomingAlarmReceiver : BroadcastReceiver(), KoinComponent {

	private val repository by inject<AlarmsRepository>()
	private val controller by inject<AlarmsController>()
	private val notificationUtils by inject<AlarmsNotificationProvider>()

	override fun onReceive(context: Context, intent: Intent) {

		val alarmId = intent.getIntExtra(ClockAppIntents.EXTRA_ALARMS_ALARMS_ID, -1)
		if (alarmId == -1) {
			Log.d(TAG, "ALARM ID IS NOT PROVIDED OR ALARM ID IS WRONG")
			return
		}

		when (intent.action) {
			ClockAppIntents.ACTION_UPCOMING_ALARM -> showNotification(context, alarmId)
			ClockAppIntents.ACTION_DISMISS_ALARM -> cancelAlarm(context, alarmId)
		}
	}


	private fun showNotification(context: Context, alarmId: Int) {

		val notificationManager = context.getSystemService<NotificationManager>()

		goAsync(Dispatchers.Main) {
			val query = repository.getAlarmFromId(alarmId)
			query.fold(
				onSuccess = { alarm ->
					val notification = notificationUtils.createUpcomingNotification(alarm)
					notificationManager?.notify(alarmId, notification)
					Log.d(TAG, "UPCOMING ALARM NOTIFICATION SHOWN")
				},
				onError = { err, _ -> err.printStackTrace() }
			)
		}
	}

	private fun cancelAlarm(context: Context, alarmId: Int) {

		val notificationManager = context.getSystemService<NotificationManager>()

		goAsync(Dispatchers.Default) {
			val query = repository.getAlarmFromId(alarmId)
			query.fold(
				onSuccess = { alarm ->
					Log.d(TAG, "ALARM CANCEL ACTION ")
					controller.cancelAlarm(alarm)
					// then cancel the notification
					notificationManager?.cancel(alarmId)
				},
				onError = { err, _ -> err.printStackTrace() }
			)
		}
	}
}