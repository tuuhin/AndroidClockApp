package com.eva.clockapp.features.alarms.data.utils

import android.content.Context
import com.eva.clockapp.R
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

fun Context.createAlarmToastMessage(alarmTime: LocalDateTime): String {
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
			else -> return getString(R.string.next_alarm_within_one_min)
		}
	}
	return getString(R.string.alarm_set_after_time, alarmText)
}