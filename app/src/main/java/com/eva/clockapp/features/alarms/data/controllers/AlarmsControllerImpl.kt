package com.eva.clockapp.features.alarms.data.controllers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.content.getSystemService
import androidx.core.os.bundleOf
import com.eva.clockapp.core.constants.ClockAppIntents
import com.eva.clockapp.core.constants.IntentRequestCodes
import com.eva.clockapp.features.alarms.data.receivers.UpcomingAlarmReceiver
import com.eva.clockapp.features.alarms.data.services.AlarmsControllerService
import com.eva.clockapp.features.alarms.domain.controllers.AlarmsController
import com.eva.clockapp.features.alarms.domain.exceptions.ExactAlarmPermissionNotFound
import com.eva.clockapp.features.alarms.domain.models.AlarmsModel
import com.eva.clockapp.features.alarms.domain.utils.AlarmUtils
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toKotlinLocalTime
import kotlinx.datetime.toLocalDateTime

private const val TAG = "ALARMS_CONTROLLER"

class AlarmsControllerImpl(private val context: Context) : AlarmsController {

	private val alarmManager by lazy { context.getSystemService<AlarmManager>() }

	private val timeZone: TimeZone
		get() = TimeZone.currentSystemDefault()

	override fun createAlarm(model: AlarmsModel, createUpcoming: Boolean): Result<LocalDateTime> {

		val triggerMillis = AlarmUtils.calculateAlarmTriggerMillis(model)

		val intent = createServiceIntentForAlarm(model)

		val pendingIntent = PendingIntent.getForegroundService(
			context,
			IntentRequestCodes.PLAY_ALARM.code,
			intent,
			PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
		)

		val cannotScheduleAlarms = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
				alarmManager?.canScheduleExactAlarms() == false

		if (cannotScheduleAlarms) {
			Log.d(TAG, "CANNOT SET EXACT ALARMS")
			return Result.failure(ExactAlarmPermissionNotFound())
		}

		return try {
			alarmManager?.setExactAndAllowWhileIdle(
				AlarmManager.RTC_WAKEUP,
				triggerMillis,
				pendingIntent
			)

			val schedule = Instant.fromEpochMilliseconds(triggerMillis).toLocalDateTime(timeZone)

			with(schedule) {
				val message = "ACTUAL ALARM-> DATE:${date} TIME:${time} MODEL_ID:${model.id}"
				Log.d(TAG, message)
			}
			// create the upcoming one
			if (createUpcoming) createUpcomingAlarm(model)

			Result.success(schedule)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}

	override fun cancelAlarm(model: AlarmsModel): Result<Unit> {

		val intent = createServiceIntentForAlarm(model)

		val pendingIntent = PendingIntent.getBroadcast(
			context,
			IntentRequestCodes.STOP_ALARM.code,
			intent,
			PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
		)

		return try {
			alarmManager?.cancel(pendingIntent)
			Log.d(TAG, "ALARM ON TIME:${model.time} MODEL_ID:${model.id} CANCELLED")
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}

	private fun createServiceIntentForAlarm(model: AlarmsModel): Intent {
		return Intent(context, AlarmsControllerService::class.java).apply {
			data = ClockAppIntents.alarmIntentData(model.id)
			action = ClockAppIntents.ACTION_PLAY_ALARM

			val extras = bundleOf(ClockAppIntents.EXTRA_ALARMS_ALARMS_ID to model.id)
			putExtras(extras)
		}
	}

	private fun createUpcomingAlarm(model: AlarmsModel): Result<LocalDateTime> {
		val triggerMillis = AlarmUtils.calculateUpcomingAlarmTriggerMillis(model)

		val intent = Intent(context, UpcomingAlarmReceiver::class.java).apply {
			data = ClockAppIntents.alarmIntentData(model.id)
			action = ClockAppIntents.ACTION_UPCOMING_ALARM

			val extras = bundleOf(ClockAppIntents.EXTRA_ALARMS_ALARMS_ID to model.id)
			putExtras(extras)
		}

		val pendingIntent = PendingIntent.getBroadcast(
			context,
			IntentRequestCodes.UPCOMING_ALARM.code,
			intent,
			PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
		)

		return try {
			// Don't show if the device is in Doze Mode
			alarmManager?.setExact(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent)

			val schedule = Instant.fromEpochMilliseconds(triggerMillis).toLocalDateTime(timeZone)

			with(schedule) {
				val message = "UPCOMING ALARM->DATE: $date TIME:$time"
				Log.d(TAG, message)
			}
			Result.success(schedule)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}

	private fun cancelUpcomingAlarm(model: AlarmsModel) {

		val time = java.time.LocalTime.now().toKotlinLocalTime()

		val hasTimePassed = model.time < time
		if (hasTimePassed) return

		val intent = Intent(context, UpcomingAlarmReceiver::class.java).apply {
			data = ClockAppIntents.alarmIntentData(model.id)
			action = ClockAppIntents.ACTION_UPCOMING_ALARM

			val extras = bundleOf(ClockAppIntents.EXTRA_ALARMS_ALARMS_ID to model.id)
			putExtras(extras)
		}

		val pendingIntent = PendingIntent.getBroadcast(
			context,
			IntentRequestCodes.UPCOMING_ALARM.code,
			intent,
			PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
		)

		try {
			// Don't show if the device is in Doze Mode
			alarmManager?.cancel(pendingIntent)
			val message = "UPCOMING ALARM CANCELLED ${model.id}"
			Log.d(TAG, message)
		} catch (err: Exception) {
			err.printStackTrace()
		}
	}
}