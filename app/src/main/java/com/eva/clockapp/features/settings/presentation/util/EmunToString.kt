package com.eva.clockapp.features.settings.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.eva.clockapp.R
import com.eva.clockapp.features.settings.domain.models.AlarmVolumeControlOption
import com.eva.clockapp.features.settings.domain.models.StartOfWeekOptions
import com.eva.clockapp.features.settings.domain.models.TimeFormatOptions
import com.eva.clockapp.features.settings.domain.models.UpcomingAlarmTimeOption

val TimeFormatOptions.toText: String
	@Composable
	get() = when (this) {
		 TimeFormatOptions.SYSTEM_DEFAULT -> stringResource(R.string.settings_option_system_default)
		 TimeFormatOptions.FORMAT_12_HR -> stringResource(R.string.settings_time_format_12_hr)
		 TimeFormatOptions.FORMAT_24_HR -> stringResource(R.string.settings_time_format_24_hr)
	}

val StartOfWeekOptions.toText: String
	@Composable
	get() = when (this) {
		StartOfWeekOptions.SUNDAY -> stringResource(R.string.settings_start_of_week_sunday)
		StartOfWeekOptions.MONDAY -> stringResource(R.string.settings_start_of_week_monday)
		StartOfWeekOptions.SYSTEM_DEFAULT -> stringResource(R.string.settings_option_system_default)
	}

val AlarmVolumeControlOption.toText: String
	@Composable
	get() = when (this) {
		AlarmVolumeControlOption.STOP_ALARM -> stringResource(R.string.settings_volume_control_stop_alarm)
		AlarmVolumeControlOption.SNOOZE_ALARM -> stringResource(R.string.settings_volume_control_snooze_alarm)
		AlarmVolumeControlOption.CONTROL_ALARM_VOLUME -> stringResource(R.string.settings_volume_control_volume)
		AlarmVolumeControlOption.NONE -> stringResource(R.string.settings_option_none)
	}

val UpcomingAlarmTimeOption.toText: String
	@Composable
	get() = when (this) {
		UpcomingAlarmTimeOption.DURATION_30_MINUTES -> stringResource(R.string.settings_duration_option_30_minutes)
		UpcomingAlarmTimeOption.DURATION_10_MINUTES -> stringResource(R.string.settings_duration_option_10_minutes)
		UpcomingAlarmTimeOption.DURATION_NONE -> stringResource(R.string.settings_option_none)
		UpcomingAlarmTimeOption.DURATION_1_HOUR -> stringResource(R.string.settings_duration_option_1_hr)
	}
