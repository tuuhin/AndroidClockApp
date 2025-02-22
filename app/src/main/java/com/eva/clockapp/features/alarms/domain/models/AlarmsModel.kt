package com.eva.clockapp.features.alarms.domain.models

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime

data class AlarmsModel(
	val id: Int,
	val time: LocalTime,
	val weekDays: Set<DayOfWeek>,
	val flags: AssociateAlarmFlags,
	val isAlarmEnabled: Boolean,
	val label: String? = null,
)
