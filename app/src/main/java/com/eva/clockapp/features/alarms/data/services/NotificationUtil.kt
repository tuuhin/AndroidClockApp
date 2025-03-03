package com.eva.clockapp.features.alarms.data.services

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.graphics.drawable.IconCompat
import androidx.core.os.bundleOf
import com.eva.clockapp.R
import com.eva.clockapp.core.constants.ClockAppIntents
import com.eva.clockapp.core.constants.IntentRequestCodes
import com.eva.clockapp.core.constants.NotificationsConstants
import com.eva.clockapp.core.utils.HH_MM_A
import com.eva.clockapp.features.alarms.domain.models.AlarmsModel
import com.eva.clockapp.features.alarms.presentation.AlarmsActivity
import kotlinx.datetime.LocalTime
import kotlinx.datetime.format

class NotificationUtil(private val context: Context) {

	private val stopPendingIntent: PendingIntent
		get() = buildPendingIntentForegroundService(
			intent = Intent(context, AlarmsControllerService::class.java).apply {
				action = ClockAppIntents.ACTION_CANCEL_ALARM
			},
			intentCode = IntentRequestCodes.STOP_ALARM,
		)

	private val snoozePendingIntent: PendingIntent
		get() = buildPendingIntentForegroundService(
			intent = Intent(context, AlarmsControllerService::class.java).apply {
				action = ClockAppIntents.ACTION_SNOOZE_ALARM
			},
			intentCode = IntentRequestCodes.SNOOZE_ALARM,
		)

	private fun buildNotificationAction(
		@DrawableRes icon: Int,
		@StringRes label: Int,
		pendingIntent: PendingIntent?,
	): Notification.Action = Notification.Action.Builder(
		IconCompat.createWithResource(context, icon).toIcon(context),
		context.getString(label),
		pendingIntent
	).build()


	private fun buildPendingIntentForegroundService(
		intent: Intent,
		intentCode: IntentRequestCodes,
		flags: Int = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT,
	): PendingIntent = PendingIntent.getForegroundService(context, intentCode.code, intent, flags)

	private fun createFullScreenIntent(alarm: AlarmsModel): PendingIntent {
		val intent = Intent(context, AlarmsActivity::class.java).apply {

			data = ClockAppIntents.alarmIntentData(alarm.id)
			action = ClockAppIntents.ACTION_SHOW_ALARMS_ACTIVITY

			flags = Intent.FLAG_ACTIVITY_NEW_TASK or
					Intent.FLAG_ACTIVITY_CLEAR_TOP or
					Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS or
					Intent.FLAG_ACTIVITY_NO_USER_ACTION

			val bundle = bundleOf(ClockAppIntents.EXTRA_ALARMS_ALARMS_ID to alarm.id)
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

		val title = context.getString(R.string.notification_title_alarms)
		val text = buildString {
			val time = alarm.time.format(LocalTime.Formats.HH_MM_A)
			append(time)
			append(" | ")
			append(context.getString(R.string.notification_text_swipe_to_dismiss))
		}

		val snoozeAction = buildNotificationAction(
			icon = R.drawable.ic_snooze,
			label = R.string.notification_action_snooze,
			pendingIntent = snoozePendingIntent
		)
		val stopAction = buildNotificationAction(
			icon = R.drawable.ic_cancel,
			label = R.string.notification_action_dismiss,
			pendingIntent = stopPendingIntent
		)

		val fullscreenIntent = createFullScreenIntent(alarm)

		return Notification.Builder(context, NotificationsConstants.ALARMS_NOTIFICATION_CHANNEL_ID)
			.setSmallIcon(R.drawable.ic_alam_clock)
			.setVisibility(Notification.VISIBILITY_PUBLIC)
			.setCategory(Notification.CATEGORY_ALARM)
			.setColorized(true)
			.setColor(context.getColor(R.color.primary_container))
			.setContentTitle(title)
			.setContentText(text)
			.setShowWhen(false)
			.setOnlyAlertOnce(true)
			.setSubText(alarm.label)
			.setDeleteIntent(stopPendingIntent)
			.setActions(snoozeAction, stopAction)
			.setContentIntent(fullscreenIntent)
			.setFullScreenIntent(fullscreenIntent, true)
			.build()

	}
}