package com.eva.clockapp.features.alarms.domain.enums

enum class VibrationPattern(val patterns: LongArray) {
	SHORT(longArrayOf(0, 100)),
	MEDIUM(longArrayOf(0, 200, 100, 100)),
	CALL_PATTERN(longArrayOf(0, 500, 500, 500)),
	HEART_BEAT(longArrayOf(0, 200, 300, 200)),
	SILENT(longArrayOf(0L))
}