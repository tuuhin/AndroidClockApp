package com.eva.clockapp.core.constants

object NotificationsConstants {

	const val ALARMS_NOTIFICATION_CHANNEL_ID = "Alarms Channel"
	const val ALARMS_NOTIFICATION_CHANNEL_NAME = "Alarms Channel"
	const val ALARMS_NOTIFICATION_CHANNEL_DESCRIPTION =
		"Alarms channel to provide alarms notifications"

	const val CLOCK_EVENT_NOTIFICATION_CHANNEL_ID = "Clock event channel"
	const val CLOCK_EVENT_NOTIFICATION_CHANNEL_NAME = "Clock events"
	const val CLOCK_EVENT_NOTIFICATION_DESC =
		"These notification will be less important clock notification, these inform the user about upcoming events"


	const val ALARMS_FOREGROUND_SERVICE_NOTIFICATION_ID = 1
	const val ALARMS_RESCHEDULE_NOTIFICATION_ID = 2
}