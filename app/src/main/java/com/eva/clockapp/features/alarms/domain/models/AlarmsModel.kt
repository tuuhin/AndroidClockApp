package com.eva.clockapp.features.alarms.domain.models

import kotlinx.datetime.LocalTime

data class AlarmsModel(
	val id: Int,
	val time: LocalTime,
	val weekDays: WeekDays,
	val flags: AssociateAlarmFlags,
	val isAlarmEnabled: Boolean,
	val label: String? = null,
	val soundUri: String? = null,
)
