package com.eva.clockapp.features.alarms.domain.models

import kotlinx.datetime.LocalTime

data class CreateAlarmModel(
	val time: LocalTime,
	val weekDays: WeekDays,
	val flags: AssociateAlarmFlags,
	val label: String? = null,
	val ringtone: RingtoneMusicFile? = null,
	val background: String? = null,
)
