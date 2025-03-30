package com.eva.clockapp.features.alarms.data.services

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
import com.eva.clockapp.features.alarms.domain.models.AlarmsModel
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration

private const val TAG = "SNOOZE_ALARM_CONTROLLER"

class SnoozeAlarmController(private val context: Context) {

	private val _manager by lazy { context.getSystemService<AlarmManager>() }

	fun startAlarmAfterSnooze(alarm: AlarmsModel, currentSnoozeCount: Int) {
		// times to repeat = repeat + 1
		val snoozeRepeat = alarm.flags.snoozeRepeatMode.times + 1
		val triggerDuration = alarm.flags.snoozeInterval.duration

		if (snoozeRepeat == currentSnoozeCount || triggerDuration == Duration.ZERO) {
			Log.d(TAG, "CANNOT SNOOZE ALARM ANY MORE CROSSED SNOOZE COUNT")
			return
		}

		val intent = Intent(context, AlarmsControllerService::class.java)
			.apply {
				data = ClockAppIntents.alarmIntentData(alarm.id)
				action = ClockAppIntents.ACTION_PLAY_ALARM_AFTER_SNOOZE

				val extras = bundleOf(ClockAppIntents.EXTRA_ALARMS_ALARMS_ID to alarm.id)
				putExtras(extras)
			}

		val pendingIntent = PendingIntent.getForegroundService(
			context,
			IntentRequestCodes.PLAY_SNOOZE_ALARM.code,
			intent,
			PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
		)

		val cannotScheduleAlarms = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
				_manager?.canScheduleExactAlarms() == false

		if (cannotScheduleAlarms) return

		val triggerMillis = Clock.System.now().toEpochMilliseconds() +
				triggerDuration.inWholeMilliseconds

		val schedule = Instant.fromEpochMilliseconds(triggerMillis)
			.toLocalDateTime(TimeZone.currentSystemDefault())

		try {
			_manager?.setExactAndAllowWhileIdle(
				AlarmManager.RTC_WAKEUP,
				triggerMillis,
				pendingIntent
			)

			Log.d(TAG, "ALARM SNOOZED WILL BE SHOWN AT :$schedule")
		} catch (e: Exception) {
			e.printStackTrace()
		}
	}

}