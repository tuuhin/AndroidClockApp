package com.eva.clockapp.features.alarms.domain.models

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime

typealias WeekDays = Set<DayOfWeek>

data class AlarmsModel(
	val id: Int,
	val time: LocalTime,
	val weekDays: WeekDays,
	val flags: AssociateAlarmFlags,
	val isAlarmEnabled: Boolean,
	val label: String? = null,
	val soundUri: String? = null,
	val background: String? = null,
)
