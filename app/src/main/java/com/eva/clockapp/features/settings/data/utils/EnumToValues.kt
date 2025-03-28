package com.eva.clockapp.features.settings.data.utils

import androidx.core.text.util.LocalePreferences
import androidx.core.text.util.LocalePreferences.HourCycle
import com.eva.clockapp.features.settings.domain.models.StartOfWeekOptions
import com.eva.clockapp.features.settings.domain.models.TimeFormatOptions
import kotlinx.datetime.DayOfWeek

val TimeFormatOptions.is24HrFormat: Boolean
	get() = when (this) {
		TimeFormatOptions.SYSTEM_DEFAULT -> LocalePreferences.getHourCycle() == HourCycle.H23
		TimeFormatOptions.FORMAT_12_HR -> false
		TimeFormatOptions.FORMAT_24_HR -> true
	}

val StartOfWeekOptions.weekEntries: Set<DayOfWeek>
	get() = when (this) {
		StartOfWeekOptions.SUNDAY -> getWeekNamesListViaStartOfDay(DayOfWeek.SUNDAY)
		StartOfWeekOptions.MONDAY -> DayOfWeek.entries.toSet()
		StartOfWeekOptions.SYSTEM_DEFAULT -> {
			val startDay = when (LocalePreferences.getFirstDayOfWeek()) {
				LocalePreferences.FirstDayOfWeek.MONDAY -> DayOfWeek.MONDAY
				LocalePreferences.FirstDayOfWeek.TUESDAY -> DayOfWeek.TUESDAY
				LocalePreferences.FirstDayOfWeek.WEDNESDAY -> DayOfWeek.WEDNESDAY
				LocalePreferences.FirstDayOfWeek.THURSDAY -> DayOfWeek.THURSDAY
				LocalePreferences.FirstDayOfWeek.FRIDAY -> DayOfWeek.FRIDAY
				else -> DayOfWeek.SUNDAY
			}
			getWeekNamesListViaStartOfDay(startDay)
		}
	}


private fun getWeekNamesListViaStartOfDay(startDay: DayOfWeek): Set<DayOfWeek> {
	val weekDays = DayOfWeek.entries
	val startIndex = weekDays.indexOf(startDay)

	return buildSet {
		for (i in 0 until weekDays.size) {
			val currentIndex = (startIndex + i) % weekDays.size
			add(weekDays[currentIndex])
		}
	}
}