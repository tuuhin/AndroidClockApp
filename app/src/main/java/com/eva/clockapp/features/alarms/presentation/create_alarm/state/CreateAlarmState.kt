package com.eva.clockapp.features.alarms.presentation.create_alarm.state

import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime

data class CreateAlarmState(
	val selectedTime: LocalTime = LocalTime(0, 0),
	val selectedDays: ImmutableSet<DayOfWeek> = persistentSetOf(),
	val labelState: String = "",
)
