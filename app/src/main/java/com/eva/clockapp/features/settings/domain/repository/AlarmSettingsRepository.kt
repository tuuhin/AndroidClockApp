package com.eva.clockapp.features.settings.domain.repository

import com.eva.clockapp.features.settings.domain.models.AlarmSettingsModel
import com.eva.clockapp.features.settings.domain.models.AlarmVolumeControlOption
import com.eva.clockapp.features.settings.domain.models.StartOfWeekOptions
import com.eva.clockapp.features.settings.domain.models.TimeFormatOptions
import com.eva.clockapp.features.settings.domain.models.UpcomingAlarmTimeOption
import kotlinx.coroutines.flow.Flow

interface AlarmSettingsRepository {

	val settingsFlow: Flow<AlarmSettingsModel>

	val settingsValue: AlarmSettingsModel

	suspend fun onStartOfWeekChange(startOfWeek: StartOfWeekOptions)

	suspend fun onTimeFormatChange(timeFormat: TimeFormatOptions)

	suspend fun onVolumeControlChange(control: AlarmVolumeControlOption)

	suspend fun onUpcomingNotificationTimeChange(time: UpcomingAlarmTimeOption)
}