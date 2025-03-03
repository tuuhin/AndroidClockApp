package com.eva.clockapp.features.alarms.data.services

import android.app.ForegroundServiceStartNotAllowedException
import android.app.ForegroundServiceTypeException
import android.app.Notification
import android.app.Service
import android.content.pm.ServiceInfo
import android.os.Build
import android.util.Log

private const val LOGGER_TAG = "ALARMS_FOREGROUND_SERVICE"

fun Service.startAlarmsForegroundService(id: Int, notification: Notification) {
	if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
		try {
			startForeground(id, notification)
		} catch (e: Exception) {
			Log.e(LOGGER_TAG, "UNABLE TO LAUNCH FOREGROUND SERVICE", e)
		}
	} else {
		val serviceType = ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
			try {
				startForeground(id, notification, serviceType)
			} catch (e: ForegroundServiceTypeException) {
				Log.e(LOGGER_TAG, "WRONG FG-SERVICE TYPE", e)
			} catch (e: ForegroundServiceStartNotAllowedException) {
				Log.e(LOGGER_TAG, "FG-SERVICE NOT ALLOWED TO START", e)
			} catch (e: Exception) {
				Log.e(LOGGER_TAG, "UNABLE TO LAUNCH FOREGROUND SERVICE", e)
			}
		} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
			try {
				startForeground(id, notification, serviceType)
			} catch (e: ForegroundServiceStartNotAllowedException) {
				Log.e(LOGGER_TAG, "FG-SERVICE NOT ALLOWED TO START", e)
			} catch (e: Exception) {
				Log.e(LOGGER_TAG, "UNABLE TO LAUNCH FOREGROUND SERVICE", e)
			}
		} else {
			try {
				startForeground(id, notification, serviceType)
			} catch (e: Exception) {
				Log.e(LOGGER_TAG, "UNABLE TO LAUNCH FOREGROUND SERVICE", e)
			}
		}
	}
}