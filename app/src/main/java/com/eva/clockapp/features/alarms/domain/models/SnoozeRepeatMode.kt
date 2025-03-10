package com.eva.clockapp.features.alarms.domain.models

enum class SnoozeRepeatMode(val times: Int) {
	NO_REPEAT(0),
	THREE(3),
	FIVE(5),
	FOR_EVER(Int.MAX_VALUE),
}