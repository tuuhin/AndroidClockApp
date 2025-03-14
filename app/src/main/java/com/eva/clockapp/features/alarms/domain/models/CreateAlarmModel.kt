package com.eva.clockapp.features.alarms.domain.models

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime

typealias WeekDays = Set<DayOfWeek>

data class CreateAlarmModel(
	val time: LocalTime,
	val weekDays: WeekDays,
	val flags: AssociateAlarmFlags,
	val label: String? = null,
	val ringtone: RingtoneMusicFile? = null,
	val background: String? = null,
)
