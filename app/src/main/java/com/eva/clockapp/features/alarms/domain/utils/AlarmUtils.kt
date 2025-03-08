package com.eva.clockapp.features.alarms.domain.utils

import com.eva.clockapp.features.alarms.domain.models.AlarmsModel
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.DurationUnit

object AlarmUtils {

	private val currentInstant: Instant
		get() = Clock.System.now()

	private val timeZone: TimeZone
		get() = TimeZone.currentSystemDefault()

	private val current: LocalDateTime
		get() = currentInstant.toLocalDateTime(timeZone)

	fun calculateAlarmTriggerMillis(model: AlarmsModel): Long {

		val condition = current.dayOfWeek !in model.weekDays || current.time > model.time

		val dateToUse = if (condition) {
			val weekDay = current.dayOfWeek
			val nextMatchingDay = findNextMatchingWeekday(weekDay, model.weekDays)
			val daysToAdd = (nextMatchingDay.ordinal - weekDay.ordinal + 7) % 7
			current.date.plus(DatePeriod(days = daysToAdd))
		} else current.date

		return dateToUse.atTime(model.time)
			.toInstant(TimeZone.Companion.currentSystemDefault())
			.toEpochMilliseconds()
	}

	fun calculateUpcomingAlarmTriggerMillis(
		model: AlarmsModel,
		showBefore: Duration = 3.hours,
	): Long {
		return calculateAlarmTriggerMillis(model).let { millis ->
			val showBeforeMillis = showBefore.toInt(DurationUnit.MILLISECONDS)
			millis - showBeforeMillis
		}
	}

	fun calculateNextAlarmTimeInDuration(alarms: List<AlarmsModel>): Duration? {
		var shortest: Duration? = null

		for (alarm in alarms) {
			val triggerTime = calculateAlarmTriggerMillis(alarm)
			val duration = Instant.Companion.fromEpochMilliseconds(triggerTime) - currentInstant

			if (shortest == null || duration < shortest)
				shortest = duration
		}
		return shortest
	}

	fun calculateNextAlarmTime(): LocalTime {
		val currentTime = current.time

		val sixAm = LocalTime(6, 0)
		val threePm = LocalTime(15, 0)
		val sixPm = LocalTime(18, 0)
		val ninePm = LocalTime(21, 0)

		return when {
			currentTime >= ninePm -> sixAm
			currentTime >= sixPm -> ninePm
			currentTime >= threePm -> sixPm
			currentTime >= sixAm -> threePm
			else -> sixAm
		}
	}

	private fun findNextMatchingWeekday(current: DayOfWeek, alarmDays: Set<DayOfWeek>): DayOfWeek {
		val sortedDays = alarmDays.sortedBy { it.ordinal }

		for (day in sortedDays) {
			// match the next first one
			if (day.ordinal > current.ordinal) return day
		}
		// if there is no match then alarm will be only set for next day
		return sortedDays.firstOrNull() ?: this.currentInstant.plus(1.days)
			.toLocalDateTime(timeZone).dayOfWeek
	}
}