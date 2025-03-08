package com.eva.clockapp.core.utils

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.DayOfWeekNames
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.Padding

val LocalTime.Formats.HH_MM_A: DateTimeFormat<LocalTime>
	get() = LocalTime.Format {
		amPmHour(padding = Padding.ZERO)
		chars(":")
		minute(padding = Padding.ZERO)
		chars(" ")
		amPmMarker(am = "am", pm = "pm")
	}

val LocalTime.Formats.HH_MM: DateTimeFormat<LocalTime>
	get() = LocalTime.Format {
		hour(padding = Padding.ZERO)
		chars(":")
		minute(padding = Padding.ZERO)
	}

val LocalDate.Formats.WEEK_MONTH_DAY: DateTimeFormat<LocalDate>
	get() = LocalDate.Format {
		dayOfWeek(names = DayOfWeekNames.ENGLISH_ABBREVIATED)
		chars(", ")
		monthName(names = MonthNames.ENGLISH_ABBREVIATED)
		chars(" ")
		dayOfMonth()
	}

val LocalDateTime.Formats.WEEK_DAY_AM_TIME: DateTimeFormat<LocalDateTime>
	get() = LocalDateTime.Format {
		dayOfWeek(names = DayOfWeekNames.ENGLISH_ABBREVIATED)
		chars(", ")
		amPmHour(padding = Padding.ZERO)
		chars(":")
		minute(padding = Padding.ZERO)
		chars(" ")
		amPmMarker(am = "am", pm = "pm")
	}