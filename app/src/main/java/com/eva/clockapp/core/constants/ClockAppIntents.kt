package com.eva.clockapp.core.constants

import androidx.core.net.toUri

object ClockAppIntents {

	private const val PACKAGE_NAME = "com.eva.clockapp"
	private const val ALARMS_FEATURE = "$PACKAGE_NAME.alarms"

	//actions
	const val ACTION_PLAY_ALARM = "$ALARMS_FEATURE.play_alarms"
	const val ACTION_CANCEL_ALARM = "$ALARMS_FEATURE.cancel_alarm"
	const val ACTION_SNOOZE_ALARM = "$ALARMS_FEATURE.snooze_alarm"
	const val ACTION_CHANGE_ALARM_VOLUME = "$ALARMS_FEATURE.change_alarm_volume"
	const val ACTION_SHOW_ALARMS_ACTIVITY = "$ALARMS_FEATURE.show_alarm_activity"
	const val ACTION_FINISH_ALARM_ACTIVITY = "$ALARMS_FEATURE.finish_alarms_activity"
	const val ACTION_PLAY_ALARM_AFTER_SNOOZE = "$ALARMS_FEATURE.play_snoozed_alarm"

	// actions for upcoming alarms
	const val ACTION_UPCOMING_ALARM = "$ALARMS_FEATURE.show_upcoming_alarm"
	const val ACTION_DISMISS_ALARM = "$ALARMS_FEATURE.dismiss_alarm"

	// wakelocks tags
	const val ALARMS_WAKE_LOCK_TAG = "app:ALARMS_WAKE_LOCK"

	//extras
	const val EXTRA_ALARMS_ALARMS_ID = "EXTRAS_ALARMS_ID"
	const val EXTRAS_ALARMS_TIME_IN_MILLIS = "EXTRAS_ALARMS_TIME"
	const val EXTRAS_ALARMS_LABEL_TEXT = "EXTRAS_LABEL_TEXT"
	const val EXTRAS_ALARM_BACKGROUND_IMAGE_URI = "EXTRAS_BACKGROUND_IMAGE"
	const val EXTRAS_ALARM_VOLUME_INCREASE = "EXTRAS_INCREASE_VOLUME"

	fun alarmIntentData(alarmId: Int) = "app://$PACKAGE_NAME/alarms/$alarmId".toUri()
}