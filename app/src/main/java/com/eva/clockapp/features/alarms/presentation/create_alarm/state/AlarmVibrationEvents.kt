package com.eva.clockapp.features.alarms.presentation.create_alarm.state

import com.eva.clockapp.features.alarms.domain.models.VibrationPattern

sealed interface AlarmVibrationEvents {
	data class OnVibrationSelected(val pattern: VibrationPattern) : AlarmVibrationEvents
	data class OnVibrationEnabled(val isEnabled: Boolean) : AlarmVibrationEvents
}