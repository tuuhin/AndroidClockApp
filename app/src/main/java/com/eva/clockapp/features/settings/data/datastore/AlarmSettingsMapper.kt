package com.eva.clockapp.features.settings.data.datastore

import com.eva.clockapp.features.settings.domain.models.AlarmSettingsModel
import com.eva.clockapp.features.settings.domain.models.AlarmVolumeControlOption
import com.eva.clockapp.features.settings.domain.models.StartOfWeekOptions
import com.eva.clockapp.features.settings.domain.models.TimeFormatOptions
import com.eva.clockapp.features.settings.domain.models.UpcomingAlarmTimeOption
import com.eva.clockapp.features.settings.proto.AlarmSettingsProto
import com.eva.clockapp.features.settings.proto.StartOfWeekProto
import com.eva.clockapp.features.settings.proto.TimeFormatProto
import com.eva.clockapp.features.settings.proto.UpcomingNotificationTimeProto
import com.eva.clockapp.features.settings.proto.VolumeButtonControlProto

fun AlarmSettingsProto.toModel(): AlarmSettingsModel = AlarmSettingsModel(
	notificationTime = upcomingAlarm.toDomainModel,
	startOfWeek = startOfWeek.toDomainModel,
	timeFormat = timeFormat.toDomainModel,
	volumeControl = volumeControl.toDomainModel
)

val StartOfWeekProto.toDomainModel: StartOfWeekOptions
	get() = when (this) {
		StartOfWeekProto.START_OF_WEEK_SYSTEM_DEFAULT -> StartOfWeekOptions.SYSTEM_DEFAULT
		StartOfWeekProto.START_OF_WEEK_MONDAY -> StartOfWeekOptions.MONDAY
		StartOfWeekProto.START_OF_WEEK_SUNDAY -> StartOfWeekOptions.SUNDAY
		else -> StartOfWeekOptions.SYSTEM_DEFAULT
	}

val StartOfWeekOptions.toProto: StartOfWeekProto
	get() = when (this) {
		StartOfWeekOptions.SUNDAY -> StartOfWeekProto.START_OF_WEEK_SUNDAY
		StartOfWeekOptions.MONDAY -> StartOfWeekProto.START_OF_WEEK_MONDAY
		StartOfWeekOptions.SYSTEM_DEFAULT -> StartOfWeekProto.START_OF_WEEK_SYSTEM_DEFAULT
	}

val TimeFormatProto.toDomainModel: TimeFormatOptions
	get() = when (this) {
		TimeFormatProto.TIME_FORMAT_SYSTEM_DEFAULT -> TimeFormatOptions.SYSTEM_DEFAULT
		TimeFormatProto.TIME_FORMAT_24_HRS -> TimeFormatOptions.FORMAT_24_HR
		TimeFormatProto.TIME_FORMAT_12_HRS -> TimeFormatOptions.FORMAT_12_HR
		else -> TimeFormatOptions.SYSTEM_DEFAULT
	}

val TimeFormatOptions.toProto: TimeFormatProto
	get() = when (this) {
		TimeFormatOptions.SYSTEM_DEFAULT -> TimeFormatProto.TIME_FORMAT_SYSTEM_DEFAULT
		TimeFormatOptions.FORMAT_12_HR -> TimeFormatProto.TIME_FORMAT_12_HRS
		TimeFormatOptions.FORMAT_24_HR -> TimeFormatProto.TIME_FORMAT_24_HRS
	}

val UpcomingNotificationTimeProto.toDomainModel: UpcomingAlarmTimeOption
	get() = when (this) {
		UpcomingNotificationTimeProto.DURATION_30_MINUTES -> UpcomingAlarmTimeOption.DURATION_30_MINUTES
		UpcomingNotificationTimeProto.DURATION_10_MINUTES -> UpcomingAlarmTimeOption.DURATION_10_MINUTES
		UpcomingNotificationTimeProto.DURATION_NONE -> UpcomingAlarmTimeOption.DURATION_NONE
		UpcomingNotificationTimeProto.DURATION_1_HOUR -> UpcomingAlarmTimeOption.DURATION_1_HOUR
		UpcomingNotificationTimeProto.UNRECOGNIZED -> UpcomingAlarmTimeOption.DURATION_30_MINUTES
	}

val UpcomingAlarmTimeOption.toProto: UpcomingNotificationTimeProto
	get() = when (this) {
		UpcomingAlarmTimeOption.DURATION_30_MINUTES -> UpcomingNotificationTimeProto.DURATION_30_MINUTES
		UpcomingAlarmTimeOption.DURATION_10_MINUTES -> UpcomingNotificationTimeProto.DURATION_10_MINUTES
		UpcomingAlarmTimeOption.DURATION_NONE -> UpcomingNotificationTimeProto.DURATION_NONE
		UpcomingAlarmTimeOption.DURATION_1_HOUR -> UpcomingNotificationTimeProto.DURATION_1_HOUR
	}

val VolumeButtonControlProto.toDomainModel: AlarmVolumeControlOption
	get() = when (this) {
		VolumeButtonControlProto.STOP_ALARM -> AlarmVolumeControlOption.STOP_ALARM
		VolumeButtonControlProto.SNOOZE_ALARM -> AlarmVolumeControlOption.SNOOZE_ALARM
		VolumeButtonControlProto.CONTROL_ALARM_VOLUME -> AlarmVolumeControlOption.CONTROL_ALARM_VOLUME
		VolumeButtonControlProto.NONE -> AlarmVolumeControlOption.NONE
		VolumeButtonControlProto.UNRECOGNIZED -> AlarmVolumeControlOption.STOP_ALARM
	}

val AlarmVolumeControlOption.toProto: VolumeButtonControlProto
	get() = when (this) {
		AlarmVolumeControlOption.STOP_ALARM -> VolumeButtonControlProto.STOP_ALARM
		AlarmVolumeControlOption.SNOOZE_ALARM -> VolumeButtonControlProto.SNOOZE_ALARM
		AlarmVolumeControlOption.CONTROL_ALARM_VOLUME -> VolumeButtonControlProto.CONTROL_ALARM_VOLUME
		AlarmVolumeControlOption.NONE -> VolumeButtonControlProto.NONE
	}