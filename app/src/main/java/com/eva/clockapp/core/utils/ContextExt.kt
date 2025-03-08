package com.eva.clockapp.core.utils

import android.Manifest
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.core.graphics.drawable.IconCompat
import com.eva.clockapp.core.constants.IntentRequestCodes

val Context.checkMusicReadPermission: Boolean
	get() {
		val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
			ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO)
		else
			ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
		return permission == PermissionChecker.PERMISSION_GRANTED
	}

val Context.checkPostNotificationPermission: Boolean
	get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
		ContextCompat.checkSelfPermission(
			this,
			Manifest.permission.POST_NOTIFICATIONS
		) == PermissionChecker.PERMISSION_GRANTED
	else true


fun buildNotificationAction(
	context: Context,
	@DrawableRes icon: Int,
	@StringRes label: Int,
	pendingIntent: PendingIntent?,
): Notification.Action = Notification.Action.Builder(
	IconCompat.createWithResource(context, icon).toIcon(context),
	context.getString(label),
	pendingIntent
).build()

fun buildPendingIntentForegroundService(
	context: Context,
	intent: Intent,
	intentCode: IntentRequestCodes,
	flags: Int = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT,
): PendingIntent = PendingIntent.getForegroundService(context, intentCode.code, intent, flags)

fun buildPendingIntentReceiver(
	context: Context,
	intent: Intent,
	intentCode: IntentRequestCodes,
	flags: Int = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT,
): PendingIntent = PendingIntent.getBroadcast(context, intentCode.code, intent, flags)