package com.eva.clockapp.features.alarms.presentation.alarms

import com.eva.clockapp.features.alarms.domain.models.AlarmsModel

sealed interface AlarmsScreenEvents {

	data class OnEnableOrDisAbleAlarm(val enabled: Boolean, val model: AlarmsModel) :
		AlarmsScreenEvents

	data class ToggleAlarmSelection(val alarm: AlarmsModel) : AlarmsScreenEvents

	data object DeleteSelectedAlarms : AlarmsScreenEvents

	data object DeSelectAllAlarms : AlarmsScreenEvents
}