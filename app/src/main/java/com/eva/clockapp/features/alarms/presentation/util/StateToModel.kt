package com.eva.clockapp.features.alarms.presentation.util

import com.eva.clockapp.features.alarms.domain.models.AlarmsModel
import com.eva.clockapp.features.alarms.domain.models.AssociateAlarmFlags
import com.eva.clockapp.features.alarms.domain.models.CreateAlarmModel
import com.eva.clockapp.features.alarms.presentation.create_alarm.state.CreateAlarmState

fun CreateAlarmState.toCreateModel(flags: AssociateAlarmFlags) = CreateAlarmModel(
	time = selectedTime,
	weekDays = selectedDays,
	label = if (labelState.isEmpty()) null else labelState,
	flags = flags,
	ringtone = ringtone
)

fun CreateAlarmState.toAlarmModel(alarmId: Int, flags: AssociateAlarmFlags) = AlarmsModel(
	id = alarmId,
	time = selectedTime,
	weekDays = selectedDays,
	flags = flags,
	isAlarmEnabled = true,
	label = if (labelState.isEmpty()) null else labelState,
	soundUri = ringtone.uri
)