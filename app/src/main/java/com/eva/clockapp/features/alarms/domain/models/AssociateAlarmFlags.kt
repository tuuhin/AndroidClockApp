package com.eva.clockapp.features.alarms.domain.models

data class AssociateAlarmFlags(
	val vibrationPattern: VibrationPattern = VibrationPattern.CALL_PATTERN,
	val snoozeInterval: SnoozeIntervalOption = SnoozeIntervalOption.IntervalThreeMinutes,
	val snoozeRepeatMode: SnoozeRepeatMode = SnoozeRepeatMode.NO_REPEAT,
	val isSnoozeEnabled: Boolean = true,
	val isVibrationEnabled: Boolean = true,
	val isSoundEnabled: Boolean = true,
	val isVolumeStepIncrease: Boolean = false,
	val alarmVolume: Float = 100f,
)
