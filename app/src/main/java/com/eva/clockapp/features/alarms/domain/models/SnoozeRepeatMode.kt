package com.eva.clockapp.features.alarms.domain.models

import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

enum class SnoozeRepeatMode(val duration: Duration?) {
	NO_REPEAT(duration = Duration.ZERO),
	THREE(duration = 3.minutes),
	FIVE(duration = 5.minutes),
	FOR_EVER(duration = null),
}