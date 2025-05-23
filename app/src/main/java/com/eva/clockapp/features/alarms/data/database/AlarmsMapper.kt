package com.eva.clockapp.features.alarms.data.database

import com.eva.clockapp.core.utils.roundToNDecimals
import com.eva.clockapp.features.alarms.domain.models.AlarmsModel
import com.eva.clockapp.features.alarms.domain.models.AssociateAlarmFlags
import com.eva.clockapp.features.alarms.domain.models.CreateAlarmModel

fun AlarmsEntity.toModel(): AlarmsModel = AlarmsModel(
	id = id ?: -1,
	time = time,
	weekDays = weekDays,
	flags = AssociateAlarmFlags(
		isVolumeStepIncrease = isVolumeStepIncrease,
		isSoundEnabled = isSoundEnabled,
		isVibrationEnabled = isVibrationEnabled,
		isSnoozeEnabled = isSnoozeEnabled,
		snoozeInterval = snoozeInterval,
		snoozeRepeatMode = snoozeRepeatMode,
		vibrationPattern = vibrationPattern,
		alarmVolume = alarmVolume.roundToNDecimals()
	),
	isAlarmEnabled = isAlarmEnabled,
	label = label,
	soundUri = alarmSoundUri,
	background = background,
)

fun CreateAlarmModel.toEntity(): AlarmsEntity = AlarmsEntity(
	id = null,
	time = time,
	weekDays = weekDays,
	isAlarmEnabled = true,
	label = label,
	isVolumeStepIncrease = flags.isVolumeStepIncrease,
	isSoundEnabled = flags.isSoundEnabled,
	isVibrationEnabled = flags.isVibrationEnabled,
	isSnoozeEnabled = flags.isSnoozeEnabled,
	snoozeInterval = flags.snoozeInterval,
	snoozeRepeatMode = flags.snoozeRepeatMode,
	vibrationPattern = flags.vibrationPattern,
	alarmVolume = flags.alarmVolume.roundToNDecimals(2),
	alarmSoundUri = ringtone?.uri.toString(),
	background = background,
)

fun AlarmsModel.toEntity(): AlarmsEntity = AlarmsEntity(
	id = id,
	time = time,
	weekDays = weekDays,
	isAlarmEnabled = isAlarmEnabled,
	label = label,
	isVolumeStepIncrease = flags.isVolumeStepIncrease,
	isSoundEnabled = flags.isSoundEnabled,
	isVibrationEnabled = flags.isVibrationEnabled,
	isSnoozeEnabled = flags.isSnoozeEnabled,
	snoozeInterval = flags.snoozeInterval,
	snoozeRepeatMode = flags.snoozeRepeatMode,
	vibrationPattern = flags.vibrationPattern,
	alarmVolume = flags.alarmVolume.roundToNDecimals(2),
	alarmSoundUri = soundUri,
	background = background,
)