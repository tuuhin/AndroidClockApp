package com.eva.clockapp.core.constants

enum class IntentRequestCodes(val code: Int) {
	PLAY_ALARM(100),
	STOP_ALARM(101),
	SNOOZE_ALARM(102),
	SHOW_ALARMS_ACTIVITY(103),
	DISMISS_ALARM(104),
	UPCOMING_ALARM(105),
}