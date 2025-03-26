package com.eva.clockapp.features.alarms.presentation.create_alarm.state

sealed interface CreateAlarmNavEvent {
	data object NavigateToSnoozeScreen : CreateAlarmNavEvent
	data object NavigateToVibrationScreen : CreateAlarmNavEvent
	data object NavigateToSoundScreen : CreateAlarmNavEvent
	data object NavigateToBackgroundScreen : CreateAlarmNavEvent
}