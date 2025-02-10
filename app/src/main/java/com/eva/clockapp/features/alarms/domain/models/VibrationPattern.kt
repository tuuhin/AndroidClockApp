package com.eva.clockapp.features.alarms.domain.models

enum class VibrationPattern(val patterns: LongArray) {
	SHORT(patterns = longArrayOf(0, 100)),
	MEDIUM(patterns = longArrayOf(0, 200, 100, 100)),
	GENTLE(patterns = longArrayOf(0, 200, 400, 300, 600, 400, 800, 500)),
	CALL_PATTERN(patterns = longArrayOf(0, 500, 500, 500)),
	HEART_BEAT(patterns = longArrayOf(0, 300, 150, 500, 150, 300, 150, 1000)),
	SILENT(patterns = longArrayOf(0L)),
	SLOW_PULSE(patterns = longArrayOf(0, 1000, 500, 1000, 500, 1000, 500, 2000)),
	MORSE_CODE(
		patterns = longArrayOf(
			0, 200, 200, 200, 200, 200, 600, 200, 600, 200, 600, 200, 200, 200, 200, 200, 200
		)
	),
	INTENSE(patterns = longArrayOf(0, 300, 100, 300, 100, 300, 100, 300, 200, 1000))
}