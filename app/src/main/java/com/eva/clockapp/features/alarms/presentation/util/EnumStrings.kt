package com.eva.clockapp.features.alarms.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.eva.clockapp.R
import com.eva.clockapp.features.alarms.domain.models.RingtoneMusicFile
import com.eva.clockapp.features.alarms.domain.models.SnoozeIntervalOption
import com.eva.clockapp.features.alarms.domain.models.SnoozeRepeatMode
import com.eva.clockapp.features.alarms.domain.models.VibrationPattern

val VibrationPattern.toText: String
	@Composable
	get() = when (this) {
		VibrationPattern.SHORT -> stringResource(R.string.vibration_pattern_short)
		VibrationPattern.MEDIUM -> stringResource(R.string.vibration_pattern_medium)
		VibrationPattern.CALL_PATTERN -> stringResource(R.string.vibration_pattern_call)
		VibrationPattern.HEART_BEAT -> stringResource(R.string.vibration_pattern_heart_beat)
		VibrationPattern.SILENT -> stringResource(R.string.vibration_pattern_silent)
		VibrationPattern.GENTLE -> stringResource(R.string.vibration_pattern_gentle)
		VibrationPattern.SLOW_PULSE -> stringResource(R.string.vibration_pattern_slow_pulse)
		VibrationPattern.MORSE_CODE -> stringResource(R.string.vibration_pattern_morse_code)
		VibrationPattern.INTENSE -> stringResource(R.string.vibration_pattern_intense)
	}


val SnoozeRepeatMode.toText: String
	@Composable
	get() = when (this) {
		SnoozeRepeatMode.THREE -> stringResource(R.string.repeat_options_number, 3)
		SnoozeRepeatMode.FIVE -> stringResource(R.string.repeat_options_number, 5)
		SnoozeRepeatMode.FOR_EVER -> stringResource(R.string.repeat_option_forever)
		SnoozeRepeatMode.NO_REPEAT -> stringResource(R.string.repeat_option_no_repeat)
	}

val SnoozeIntervalOption.toText: String
	@Composable
	get() = "${this.duration.inWholeMinutes} minutes"

val RingtoneMusicFile.RingtoneType.toText: String
	@Composable
	get() = when (this) {
		RingtoneMusicFile.RingtoneType.APPLICATION_LOCAL -> stringResource(R.string.alarm_sound_defined_title)
		RingtoneMusicFile.RingtoneType.DEVICE_LOCAL -> stringResource(R.string.alarm_sound_external_title)
	}


