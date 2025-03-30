package com.eva.clockapp.features.alarms.data.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
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
			ClockAppIntents.ACTION_UPCOMING_ALARM -> showNotification(alarmId)
			ClockAppIntents.ACTION_DISMISS_ALARM -> cancelAlarm(alarmId)
		}
	}


	private fun showNotification(alarmId: Int) {
		goAsync(Dispatchers.IO) {
			val query = repository.getAlarmFromId(alarmId)
			query.fold(
				onSuccess = { alarm ->
					notificationUtils.showUpcomingAlarmNotification(alarm)
					Log.d(TAG, "UPCOMING ALARM NOTIFICATION SHOWN")
				},
				onError = { err, _ -> err.printStackTrace() }
			)
		}
	}

	private fun cancelAlarm(alarmId: Int) {
		goAsync(Dispatchers.IO) {
			val query = repository.getAlarmFromId(alarmId)
			query.fold(
				onSuccess = { alarm ->
					Log.d(TAG, "ALARM CANCEL ACTION ")
					controller.cancelAlarm(alarm)
					// then cancel the notification
					notificationUtils.cancelUpcomingNotification(alarm)
				},
				onError = { err, _ -> err.printStackTrace() }
			)
		}
	}
}