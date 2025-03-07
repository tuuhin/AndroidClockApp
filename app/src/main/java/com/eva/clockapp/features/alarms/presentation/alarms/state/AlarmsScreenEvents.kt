package com.eva.clockapp.features.alarms.presentation.alarms.state

import com.eva.clockapp.features.alarms.domain.models.AlarmsModel

sealed interface AlarmsScreenEvents {

	data class OnEnableOrDisAbleAlarm(val model: AlarmsModel) : AlarmsScreenEvents
	data class OnSelectOrUnSelectAlarm(val model: AlarmsModel) : AlarmsScreenEvents

	data object DeleteSelectedAlarms : AlarmsScreenEvents

	data object DeSelectAllAlarms : AlarmsScreenEvents
	data object OnSelectAllAlarms : AlarmsScreenEvents

	data object OnEnableSelectedAlarms : AlarmsScreenEvents
	data object OnDisableSelectedAlarms : AlarmsScreenEvents
}