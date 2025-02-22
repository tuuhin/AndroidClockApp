package com.eva.clockapp.core.utils

import kotlinx.datetime.LocalTime
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.Padding

val LocalTime.Formats.HH_MM_A: DateTimeFormat<LocalTime>
	get() = LocalTime.Format {
		hour(padding = Padding.ZERO)
		chars(":")
		minute(padding = Padding.ZERO)
		chars(" ")
		amPmMarker(am = "am", pm = "pm")
	}