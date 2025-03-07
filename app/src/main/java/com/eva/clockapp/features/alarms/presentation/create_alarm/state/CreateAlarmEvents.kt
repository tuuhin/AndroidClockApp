package com.eva.clockapp.features.alarms.presentation.create_alarm.state

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime

sealed interface CreateAlarmEvents {
	data class OnAlarmTimeChange(val time: LocalTime) : CreateAlarmEvents
	data class OnAddOrRemoveWeekDay(val dayOfWeek: DayOfWeek) : CreateAlarmEvents

	data class OnLabelValueChange(val newValue: String) : CreateAlarmEvents

	data object LoadDeviceRingtoneFiles : CreateAlarmEvents
	data object OnExitAlarmSoundScreen : CreateAlarmEvents

	data object OnSaveAlarm : CreateAlarmEvents
	data object OnUpdateAlarm : CreateAlarmEvents
}