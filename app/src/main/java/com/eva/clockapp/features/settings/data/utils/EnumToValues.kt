package com.eva.clockapp.features.settings.data.utils

import androidx.core.text.util.LocalePreferences
import androidx.core.text.util.LocalePreferences.HourCycle
import com.eva.clockapp.features.settings.domain.models.TimeFormatOptions

val TimeFormatOptions.is24HrFormat: Boolean
	get() = when (this) {
		TimeFormatOptions.SYSTEM_DEFAULT -> LocalePreferences.getHourCycle() == HourCycle.H23
		TimeFormatOptions.FORMAT_12_HR -> false
		TimeFormatOptions.FORMAT_24_HR -> true
	}