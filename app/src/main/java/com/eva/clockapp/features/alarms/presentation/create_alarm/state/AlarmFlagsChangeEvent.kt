package com.eva.clockapp.features.alarms.presentation.create_alarm.state

import com.eva.clockapp.features.alarms.domain.models.SnoozeIntervalOption
import com.eva.clockapp.features.alarms.domain.models.SnoozeRepeatMode
import com.eva.clockapp.features.alarms.domain.models.VibrationPattern

sealed interface AlarmFlagsChangeEvent {

	// modifies properties associated with vibration
	data class OnVibrationPatternSelected(val pattern: VibrationPattern) : AlarmFlagsChangeEvent
	data class OnVibrationEnabled(val isEnabled: Boolean) : AlarmFlagsChangeEvent

	// modifies properties associated with snooze
	data class OnSnoozeRepeatModeChange(val mode: SnoozeRepeatMode) : AlarmFlagsChangeEvent
	data class OnSnoozeIntervalChange(val interval: SnoozeIntervalOption) : AlarmFlagsChangeEvent
	data class OnSnoozeEnabled(val isEnabled: Boolean) : AlarmFlagsChangeEvent

	// modifies properties associated with alarm sound
	data class OnSoundOptionEnabled(val isEnabled: Boolean) : AlarmFlagsChangeEvent
	data class OnIncreaseVolumeByStep(val isEnabled: Boolean) : AlarmFlagsChangeEvent
	data class OnSoundVolumeChange(val volume: Float) : AlarmFlagsChangeEvent

}