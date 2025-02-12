package com.eva.clockapp.features.alarms.presentation.create_alarm.state

import com.eva.clockapp.features.alarms.domain.models.RingtoneMusicFile
import com.eva.clockapp.features.alarms.domain.models.SnoozeIntervalOption
import com.eva.clockapp.features.alarms.domain.models.SnoozeRepeatMode
import com.eva.clockapp.features.alarms.domain.models.VibrationPattern
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime

sealed interface CreateAlarmEvents {
	data class OnAlarmTimeSelected(val time: LocalTime) : CreateAlarmEvents
	data class OnAddOrRemoveWeekDay(val dayOfWeek: DayOfWeek) : CreateAlarmEvents

	data class OnVibrationPatternSelected(val pattern: VibrationPattern) : CreateAlarmEvents
	data class OnVibrationEnabled(val isEnabled: Boolean) : CreateAlarmEvents

	data class OnSnoozeRepeatModeChange(val mode: SnoozeRepeatMode) : CreateAlarmEvents
	data class OnSnoozeIntervalChange(val intervalOptions: SnoozeIntervalOption) : CreateAlarmEvents
	data class OnSnoozeEnabled(val isEnabled: Boolean) : CreateAlarmEvents

	data class OnLabelValueChange(val newValue: String) : CreateAlarmEvents

	data object LoadDeviceRingtoneFiles : CreateAlarmEvents
	data class OnSoundSelected(val sound: RingtoneMusicFile) : CreateAlarmEvents
	data class OnSoundOptionEnabled(val isEnabled: Boolean) : CreateAlarmEvents

	data object OnSaveAlarm : CreateAlarmEvents
}