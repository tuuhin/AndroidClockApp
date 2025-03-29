package com.eva.clockapp.features.alarms.data.receivers

import android.app.AlarmManager
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.content.getSystemService
import com.eva.clockapp.R
import com.eva.clockapp.core.constants.NotificationsConstants
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

class RescheduleAlarmReceiver : BroadcastReceiver(), KoinComponent {

	private val repository by inject<AlarmsRepository>()
	private val controller by inject<AlarmsController>()
	private val notificationUtils by inject<AlarmsNotificationProvider>()

	override fun onReceive(context: Context, intent: Intent) {

		Log.d(TAG, "ACTION RECEIVER :${intent.action}")

		val supportedActions = buildList {
			add(Intent.ACTION_BOOT_COMPLETED)
			add(Intent.ACTION_TIMEZONE_CHANGED)
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
				add(AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED)
			}
		}.toTypedArray()

		if (intent.action !in supportedActions) return

		val isTimeZoneChange = intent.action == Intent.ACTION_TIMEZONE_CHANGED
		val exactAlarmStateChange = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
				intent.action == AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED

		val alarmManager = context.getSystemService<AlarmManager>()
		val canScheduleExactAlarms = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
				alarmManager?.canScheduleExactAlarms() == true

		val rescheduleType = when {
			isTimeZoneChange -> RescheduleType.RESCHEDULE_ALARMS_TIME_ZONE
			exactAlarmStateChange && canScheduleExactAlarms -> RescheduleType.RESCHEDULE_ALARMS_NORMAL
			exactAlarmStateChange && !canScheduleExactAlarms -> RescheduleType.CANCEL_ALARMS
			else -> RescheduleType.RESCHEDULE_ALARMS_DUE_TO_BOOT
		}

		goAsync(Dispatchers.Default) {
			// create alarms on getting all alarms
			repository.getAllAlarms().fold(
				onSuccess = { alarms ->
					val enabledAlarms = alarms.filter { it.isAlarmEnabled }
					createAlarms(
						context, enabledAlarms,
						type = rescheduleType,
					)
				},
			)
		}
	}

	private suspend fun createAlarms(
		context: Context,
		alarms: List<AlarmsModel>,
		type: RescheduleType = RescheduleType.RESCHEDULE_ALARMS_DUE_TO_BOOT,
	) {
		val manager = context.getSystemService<NotificationManager>()

		supervisorScope {

			try {
				val operation = alarms.filter { it.isAlarmEnabled }
					.map { alarm -> async(Dispatchers.IO) { controller.createAlarm(alarm) } }

				Log.d(TAG, "ALARMS ARE ENQUEUED : ${operation.size}")
				operation.awaitAll()
				Log.d(TAG, "ALARMS READY")

				// show the notification
				val notification = when (type) {
					RescheduleType.RESCHEDULE_ALARMS_TIME_ZONE ->
						notificationUtils.createRescheduleNotification(
							titleRes = R.string.notification_title_time_zone_changed
						)

					RescheduleType.CANCEL_ALARMS ->
						notificationUtils.createRescheduleNotification(
							titleRes = R.string.notification_title_alarms_turned_off,
							textRes = R.string.notification_title_alarms_turned_off_desc
						)

					RescheduleType.RESCHEDULE_ALARMS_NORMAL -> notificationUtils.createRescheduleNotification()
					RescheduleType.RESCHEDULE_ALARMS_DUE_TO_BOOT -> return@supervisorScope
				}

				manager?.notify(
					NotificationsConstants.ALARMS_RESCHEDULE_NOTIFICATION_ID,
					notification
				)
			} catch (e: Exception) {
				e.printStackTrace()
			}
		}
	}

	private enum class RescheduleType {
		RESCHEDULE_ALARMS_NORMAL,
		RESCHEDULE_ALARMS_DUE_TO_BOOT,
		RESCHEDULE_ALARMS_TIME_ZONE,
		CANCEL_ALARMS,
	}
}