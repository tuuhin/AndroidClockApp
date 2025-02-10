package com.eva.clockapp.features.alarms.domain.models

import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

sealed class SnoozeIntervalOption(val duration: Duration) {
	data object IntervalThreeMinutes : SnoozeIntervalOption(3.minutes)
	data object IntervalTenMinutes : SnoozeIntervalOption(10.minutes)
	data object IntervalFifteenMinutes : SnoozeIntervalOption(15.minutes)
	data object IntervalThirtyMinutes : SnoozeIntervalOption(30.minutes)
	data class IntervalCustomMinutes(val minutes: Int) : SnoozeIntervalOption(minutes.minutes)
}