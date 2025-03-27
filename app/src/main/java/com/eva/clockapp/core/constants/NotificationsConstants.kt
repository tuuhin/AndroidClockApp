package com.eva.clockapp.core.constants

import com.eva.clockapp.features.alarms.domain.models.AlarmsModel

object NotificationsConstants {

	const val ALARMS_NOTIFICATION_CHANNEL_ID = "Alarms Channel"
	const val ALARMS_NOTIFICATION_CHANNEL_NAME = "Alarms Channel"
	const val ALARMS_NOTIFICATION_CHANNEL_DESCRIPTION =
		"Alarms channel to provide alarms notifications"

	const val CLOCK_EVENT_NOTIFICATION_CHANNEL_ID = "Clock event channel"
	const val CLOCK_EVENT_NOTIFICATION_CHANNEL_NAME = "Clock events"
	const val CLOCK_EVENT_NOTIFICATION_DESC =
		"These notification will be less important clock notification, these inform the user about upcoming events"

	// increase the reserve count if needed later
	private const val RESERVED_NOTIFICATION_ID_COUNT = 10

	// we can have reserved notification for this application
	const val ALARMS_FOREGROUND_SERVICE_NOTIFICATION_ID = 1
	const val ALARMS_RESCHEDULE_NOTIFICATION_ID = 2
	const val ALARMS_SYNC_FOREGROUND_NOTIFICATION_ID = 3

	fun notificationIdFromModel(alarm: AlarmsModel) = RESERVED_NOTIFICATION_ID_COUNT + alarm.id
}