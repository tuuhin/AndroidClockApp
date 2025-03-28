package com.eva.clockapp.features.settings.presentation

import com.eva.clockapp.features.settings.domain.models.AlarmVolumeControlOption
import com.eva.clockapp.features.settings.domain.models.StartOfWeekOptions
import com.eva.clockapp.features.settings.domain.models.TimeFormatOptions
import com.eva.clockapp.features.settings.domain.models.UpcomingAlarmTimeOption

sealed interface ChangeAlarmSettingsEvent {
	data class OnStartOfWeekChange(val startOfWeek: StartOfWeekOptions) : ChangeAlarmSettingsEvent
	data class OnTimeFormatChange(val format: TimeFormatOptions) : ChangeAlarmSettingsEvent
	data class OnUpcomingNotificationTimeChange(val time: UpcomingAlarmTimeOption) :
		ChangeAlarmSettingsEvent

	data class OnVolumeControlChange(val control: AlarmVolumeControlOption) : ChangeAlarmSettingsEvent
}