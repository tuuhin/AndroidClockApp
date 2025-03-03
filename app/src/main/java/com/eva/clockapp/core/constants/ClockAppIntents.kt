package com.eva.clockapp.core.constants

import androidx.core.net.toUri

object ClockAppIntents {

	private const val PACKAGE_NAME = "com.eva.clockapp"
	private const val ALARMS_FEATURE = "$PACKAGE_NAME.alarms"

	//actions
	const val ACTION_START_ALARM = "$ALARMS_FEATURE.play_alarms"
	const val ACTION_CANCEL_ALARM = "$ALARMS_FEATURE.cancel_alarm"
	const val ACTION_SNOOZE_ALARM = "$ALARMS_FEATURE.snooze_alarm"
	const val ACTION_SHOW_ALARMS_ACTIVITY = "$ALARMS_FEATURE.show_alarm_activity"
	const val ACTION_FINISH_ALARMS_ACTIVITY = "$ALARMS_FEATURE.finish_alarms_activity"

	// wakelocks tags
	const val ALARMS_WAKE_LOCK_TAG = "app:ALARMS_WAKE_LOCK"

	//extras
	const val EXTRA_ALARMS_ALARMS_ID = "EXTRAS_ALARMS_ID"

	fun alarmIntentData(alarmId: Int) = "app://$PACKAGE_NAME/alarms/$alarmId".toUri()
}