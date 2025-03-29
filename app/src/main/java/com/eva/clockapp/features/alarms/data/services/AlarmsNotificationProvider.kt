package com.eva.clockapp.features.alarms.data.services

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.annotation.StringRes
import androidx.core.os.bundleOf
import com.eva.clockapp.R
import com.eva.clockapp.core.constants.ClockAppIntents
import com.eva.clockapp.core.constants.IntentRequestCodes
import com.eva.clockapp.core.constants.NotificationsConstants
import com.eva.clockapp.core.utils.HH_MM
import com.eva.clockapp.core.utils.HH_MM_A
import com.eva.clockapp.core.utils.WEEK_DAY_AM_TIME
import com.eva.clockapp.core.utils.buildNotificationAction
import com.eva.clockapp.core.utils.buildPendingIntentForegroundService
import com.eva.clockapp.core.utils.buildPendingIntentReceiver
import com.eva.clockapp.features.alarms.data.receivers.UpcomingAlarmReceiver
import com.eva.clockapp.features.alarms.domain.models.AlarmsModel
import com.eva.clockapp.features.alarms.presentation.activity.AlarmsActivity
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atDate
import kotlinx.datetime.atTime
import kotlinx.datetime.format
import kotlinx.datetime.toInstant
import kotlinx.datetime.toKotlinLocalDateTime

class AlarmsNotificationProvider(private val context: Context) {

	private fun createFullScreenIntent(alarm: AlarmsModel): PendingIntent {

		val currentDateTime = java.time.LocalDateTime.now().toKotlinLocalDateTime()

		val alarmDateTime = alarm.time.atDate(currentDateTime.date)
			.toInstant(TimeZone.currentSystemDefault())
			.toEpochMilliseconds()

		val intent = Intent(context, AlarmsActivity::class.java).apply {

			data = ClockAppIntents.alarmIntentData(alarm.id)
			action = ClockAppIntents.ACTION_SHOW_ALARMS_ACTIVITY

			flags = Intent.FLAG_ACTIVITY_NEW_TASK or
					Intent.FLAG_ACTIVITY_CLEAR_TOP or
					Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS or
					Intent.FLAG_ACTIVITY_NO_USER_ACTION

			val bundle = bundleOf(
				ClockAppIntents.EXTRA_ALARMS_ALARMS_ID to alarm.id,
				ClockAppIntents.EXTRAS_ALARMS_LABEL_TEXT to alarm.label,
				ClockAppIntents.EXTRAS_ALARMS_TIME_IN_MILLIS to alarmDateTime,
				ClockAppIntents.EXTRAS_ALARM_BACKGROUND_IMAGE_URI to alarm.background
			)
			putExtras(bundle)
		}

		return PendingIntent.getActivity(
			context,
			IntentRequestCodes.SHOW_ALARMS_ACTIVITY.code,
			intent,
			PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
		)
	}


	fun createNotification(context: Context, alarm: AlarmsModel): Notification {

		val title = buildString {
			val time = alarm.time.format(LocalTime.Formats.HH_MM_A)
			append(time)
			append(" | ")
			append(context.getString(R.string.notification_text_swipe_to_dismiss))
		}

		val snoozePendingIntent = buildPendingIntentForegroundService(
			context = context,
			intent = Intent(context, AlarmsControllerService::class.java).apply {
				action = ClockAppIntents.ACTION_SNOOZE_ALARM
				// extras
				val bundle = bundleOf(ClockAppIntents.EXTRA_ALARMS_ALARMS_ID to alarm.id)
				putExtras(bundle)
			},
			intentCode = IntentRequestCodes.SNOOZE_ALARM,
		)

		val disMissPendingIntent = buildPendingIntentForegroundService(
			context = context,
			intent = Intent(context, AlarmsControllerService::class.java).apply {
				action = ClockAppIntents.ACTION_CANCEL_ALARM
				// extras
				val bundle = bundleOf(ClockAppIntents.EXTRA_ALARMS_ALARMS_ID to alarm.id)
				putExtras(bundle)
			},
			intentCode = IntentRequestCodes.STOP_ALARM,
		)

		val snoozeAction = buildNotificationAction(
			context = context,
			icon = R.drawable.ic_snooze,
			label = R.string.notification_action_snooze,
			pendingIntent = snoozePendingIntent
		)

		val stopAction = buildNotificationAction(
			context = context,
			icon = R.drawable.ic_cancel,
			label = R.string.notification_action_dismiss,
			pendingIntent = disMissPendingIntent,
		)

		val fullscreenIntent = createFullScreenIntent(alarm)

		return Notification.Builder(context, NotificationsConstants.ALARMS_NOTIFICATION_CHANNEL_ID)
			.setSmallIcon(R.drawable.ic_alam_clock)
			.setVisibility(Notification.VISIBILITY_PUBLIC)
			.setCategory(Notification.CATEGORY_ALARM)
			.setShowWhen(false)
			.setOnlyAlertOnce(true)
			.setColorized(true)
			.setColor(context.getColor(R.color.primary_container))
			.setContentTitle(title)
			.setContentText(alarm.label)
			.setSubText(context.getString(R.string.notification_title_alarms))
			.setDeleteIntent(disMissPendingIntent)
			.setActions(snoozeAction, stopAction)
			.setContentIntent(fullscreenIntent)
			.setFullScreenIntent(fullscreenIntent, true)
			.build()
	}

	fun createRescheduleNotification(
		@StringRes titleRes: Int = R.string.alarms_rescheduled_notification_title,
		@StringRes textRes: Int = R.string.alarms_rescheduled_notification_text,
	): Notification {
		return Notification
			.Builder(context, NotificationsConstants.CLOCK_EVENT_NOTIFICATION_CHANNEL_ID)
			.setSmallIcon(R.drawable.ic_upcoming_alarm)
			.setVisibility(Notification.VISIBILITY_PUBLIC)
			.setCategory(Notification.CATEGORY_EVENT)
			.setContentTitle(context.getString(titleRes))
			.setContentText(context.getString(textRes))
			.setOnlyAlertOnce(true)
			.setAutoCancel(true)
			.build()
	}

	fun createMissedAlarmNotification(alarm: AlarmsModel): Notification {

		val title = context.getString(R.string.missed_alarm_notification_title)
		val dateTimeString = alarm.time.format(LocalTime.Formats.HH_MM)
		val text = context.getString(R.string.missed_alarm_notification_text, dateTimeString)

		return Notification
			.Builder(context, NotificationsConstants.CLOCK_EVENT_NOTIFICATION_CHANNEL_ID)
			.setSmallIcon(R.drawable.ic_upcoming_alarm)
			.setVisibility(Notification.VISIBILITY_PUBLIC)
			.setCategory(Notification.CATEGORY_EVENT)
			.setContentTitle(title)
			.setContentText(text)
			.setShowWhen(false)
			.setAutoCancel(true)
			.build()
	}

	fun createUpcomingNotification(alarm: AlarmsModel): Notification {

		val today = java.time.LocalDateTime.now().toKotlinLocalDateTime()
		val dateTime = today.date.atTime(alarm.time)

		val action = buildNotificationAction(
			context = context,
			icon = R.drawable.ic_cancel,
			label = R.string.notification_action_dismiss,
			pendingIntent = buildPendingIntentReceiver(
				context = context,
				intent = Intent(context, UpcomingAlarmReceiver::class.java).apply {
					action = ClockAppIntents.ACTION_DISMISS_ALARM

					// extras
					val bundle = bundleOf(ClockAppIntents.EXTRA_ALARMS_ALARMS_ID to alarm.id)
					putExtras(bundle)
				},
				intentCode = IntentRequestCodes.DISMISS_ALARM,
			)
		)

		val title = context.getString(R.string.upcoming_notification_title)
		val text = dateTime.format(LocalDateTime.Formats.WEEK_DAY_AM_TIME)

		return Notification
			.Builder(context, NotificationsConstants.CLOCK_EVENT_NOTIFICATION_CHANNEL_ID)
			.setSmallIcon(R.drawable.ic_upcoming_alarm)
			.setVisibility(Notification.VISIBILITY_PUBLIC)
			.setCategory(Notification.CATEGORY_EVENT)
			.setContentTitle(title)
			.setContentText(text)
			.setShowWhen(false)
			.setAutoCancel(true)
			.setActions(action)
			.build()
	}
}