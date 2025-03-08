package com.eva.clockapp.features.alarms.data.receivers

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.content.getSystemService
import com.eva.clockapp.core.constants.NotificationsConstants
import com.eva.clockapp.core.utils.Resource
import com.eva.clockapp.features.alarms.data.services.AlarmsNotificationProvider
import com.eva.clockapp.features.alarms.domain.controllers.AlarmsController
import com.eva.clockapp.features.alarms.domain.models.AlarmsModel
import com.eva.clockapp.features.alarms.domain.repository.AlarmsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private const val TAG = "DEVICE_BOOT_RECEIVER"

class DeviceBootReceiver : BroadcastReceiver(), KoinComponent {

	private val repository by inject<AlarmsRepository>()
	private val controller by inject<AlarmsController>()
	private val notificationUtils by inject<AlarmsNotificationProvider>()

	override fun onReceive(context: Context, intent: Intent) {

		val supportedActions = buildList {
			add(Intent.ACTION_BOOT_COMPLETED)
			add(Intent.ACTION_LOCKED_BOOT_COMPLETED)
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
				add(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
			}
		}.toTypedArray()

		if (intent.action !in supportedActions) return

		Log.d(TAG, "ACTION : ${intent.action}")

		goAsync(Dispatchers.Default) {
			val isRescheduleAlarms = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
					intent.action == Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
			when (val result = repository.getAllAlarms()) {
				is Resource.Success -> createAlarms(context, result.data, isRescheduleAlarms)
				else -> {}
			}
		}
	}

	private suspend fun createAlarms(
		context: Context,
		alarms: List<AlarmsModel>,
		showNotification: Boolean = false,
	) {
		val notificationManager = context.getSystemService<NotificationManager>()

		supervisorScope {

			val operation = alarms.filter { it.isAlarmEnabled }
				.map { alarm -> async(Dispatchers.IO) { controller.createAlarm(alarm) } }

			Log.d(TAG, "ALARMS ARE ENQUEUED : ${operation.size}")

			operation.awaitAll()
			// show a notification
			if (showNotification) {
				notificationManager?.notify(
					NotificationsConstants.ALARMS_RESCHEDULE_NOTIFICATION_ID,
					notificationUtils.createRescheduleNotification()
				)
			}

		}
	}
}