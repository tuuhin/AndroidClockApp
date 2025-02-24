package com.eva.clockapp.features.alarms.domain.models

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime

data class CreateAlarmModel(
	val time: LocalTime,
	val weekDays: Set<DayOfWeek>,
	val flags: AssociateAlarmFlags,
	val label: String? = null,
	val ringtone: RingtoneMusicFile? = null,
)
