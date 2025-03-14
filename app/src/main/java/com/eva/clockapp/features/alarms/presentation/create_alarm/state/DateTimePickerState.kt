package com.eva.clockapp.features.alarms.presentation.create_alarm.state

import androidx.compose.runtime.Stable
import com.eva.clockapp.features.alarms.domain.models.WeekDays
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime

@Stable
data class DateTimePickerState(
	val weekDays: WeekDays = persistentSetOf<DayOfWeek>(),
	val startTime: LocalTime = LocalTime(0, 0),
	val selectedTime: LocalTime = LocalTime(0, 0),
)
