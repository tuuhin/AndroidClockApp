package com.eva.clockapp.features.alarms.data.util

import com.eva.clockapp.features.alarms.domain.models.AlarmsModel
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

object AlarmUtils {

	fun calculateAlarmTriggerMillis(model: AlarmsModel): Long {

		val current = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
		val condition = current.dayOfWeek !in model.weekDays || current.time > model.time

		val dateToUse = if (condition) {
			val weekDay = current.dayOfWeek
			val nextMatchingDay = findNextMatchingWeekday(weekDay, model.weekDays)
			val daysToAdd = (nextMatchingDay.ordinal - weekDay.ordinal + 7) % 7
			current.date.plus(DatePeriod(days = daysToAdd))
		} else current.date

		return dateToUse.atTime(model.time)
			.toInstant(TimeZone.currentSystemDefault())
			.toEpochMilliseconds()
	}

	fun createAlarmToastMessage(alarmTime: LocalDateTime): String {
		val timeZone = TimeZone.currentSystemDefault()
		val current = Clock.System.now().toLocalDateTime(timeZone)
		val duration = alarmTime.toInstant(timeZone) - current.toInstant(timeZone)

		val days = duration.inWholeDays
		val hours = duration.inWholeHours % 24
		val minutes = duration.inWholeMinutes % 60

		val alamText = buildString {
			when {
				days > 0 -> append("$days days ")
				hours > 0 -> append("$hours hours")
				minutes > 0 -> append("$minutes minutes")
				else -> append("Less than a minute left")
			}
		}

		return "Alam in $alamText"

	}

	private fun findNextMatchingWeekday(current: DayOfWeek, alarmDays: Set<DayOfWeek>): DayOfWeek {
		val sortedDays = alarmDays.sortedBy { it.ordinal }

		for (day in sortedDays) {
			// match the next first one
			if (day.ordinal > current.ordinal) return day
		}
		// no matches thus use the same day
		return sortedDays.first()
	}
}