package com.eva.clockapp.features.alarms.data.controllers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.content.getSystemService
import androidx.core.os.bundleOf
import com.eva.clockapp.R
import com.eva.clockapp.core.constants.ClockAppIntents
import com.eva.clockapp.core.constants.IntentRequestCodes
import com.eva.clockapp.features.alarms.data.services.AlarmsControllerService
import com.eva.clockapp.features.alarms.domain.controllers.AlarmsController
import com.eva.clockapp.features.alarms.domain.exceptions.ExactAlarmPermissionNotFound
import com.eva.clockapp.features.alarms.domain.models.AlarmsModel
import com.eva.clockapp.features.alarms.domain.utils.AlarmUtils
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

private const val TAG = "ALARMS_CONTROLLER"

class AlarmsControllerImpl(private val context: Context) : AlarmsController {

	private val alarmManager by lazy { context.getSystemService<AlarmManager>() }

	override fun createAlarm(model: AlarmsModel, showToast: Boolean): Result<Unit> {

		val triggerMillis = AlarmUtils.calculateAlarmTriggerMillis(model)

		val intent = Intent(context, AlarmsControllerService::class.java).apply {
			data = ClockAppIntents.alarmIntentData(model.id)
			action = ClockAppIntents.ACTION_START_ALARM

			val extras = bundleOf(ClockAppIntents.EXTRA_ALARMS_ALARMS_ID to model.id)
			putExtras(extras)
		}

		val pendingIntent = PendingIntent.getForegroundService(
			context,
			IntentRequestCodes.PLAY_ALARM.code,
			intent,
			PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
		)

		val cannotScheduleAlarms = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
				alarmManager?.canScheduleExactAlarms() == false

		if (cannotScheduleAlarms)
			return Result.failure(ExactAlarmPermissionNotFound())

		return try {
			alarmManager?.setExactAndAllowWhileIdle(
				AlarmManager.RTC_WAKEUP,
				triggerMillis,
				pendingIntent
			)

			val schedule = Instant.fromEpochMilliseconds(triggerMillis)
				.toLocalDateTime(TimeZone.currentSystemDefault())

			if (showToast) Toast.makeText(
				context,
				createAlarmToastMessage(schedule),
				Toast.LENGTH_LONG
			).show()

			val message = "ALARM-> DATE:${schedule.date} TIME:${schedule.time} MODEL_ID:${model.id}"
			Log.d(TAG, message)
			Result.success(Unit)
		} catch (e: Exception) {
			Result.failure(e)
		}
	}

	override fun cancelAlarm(model: AlarmsModel): Result<Unit> {

		val intent = Intent(context, AlarmsControllerService::class.java).apply {
			data = ClockAppIntents.alarmIntentData(model.id)
			action = ClockAppIntents.ACTION_CANCEL_ALARM

			val extras = bundleOf(ClockAppIntents.EXTRA_ALARMS_ALARMS_ID to model.id)
			putExtras(extras)
		}

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

	fun createAlarmToastMessage(alarmTime: LocalDateTime): String {
		val timeZone = TimeZone.currentSystemDefault()
		val duration = alarmTime.toInstant(timeZone) - Clock.System.now()

		val days = duration.inWholeDays
		val hours = duration.inWholeHours % 24
		val minutes = duration.inWholeMinutes % 60

		val alarmText = buildString {
			when {
				days > 0 -> append("$days d")
				hours > 0 -> append("$hours h")
				minutes > 0 -> append("$minutes m")
				else -> return context.getString(R.string.next_alarm_within_one_min)
			}
		}
		return context.getString(R.string.next_alarms_at_time, alarmText)
	}
}