package com.eva.clockapp.features.settings.domain.models

data class AlarmSettingsModel(
	val notificationTime: UpcomingAlarmTimeOption = UpcomingAlarmTimeOption.DURATION_30_MINUTES,
	val volumeControl: AlarmVolumeControlOption = AlarmVolumeControlOption.SNOOZE_ALARM,
	val startOfWeek: StartOfWeekOptions = StartOfWeekOptions.SYSTEM_DEFAULT,
	val timeFormat: TimeFormatOptions = TimeFormatOptions.SYSTEM_DEFAULT,
)