package com.eva.clockapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import com.eva.clockapp.core.constants.NotificationsConstants.ALARMS_NOTIFICATION_CHANNEL_DESCRIPTION
import com.eva.clockapp.core.constants.NotificationsConstants.ALARMS_NOTIFICATION_CHANNEL_ID
import com.eva.clockapp.core.constants.NotificationsConstants.ALARMS_NOTIFICATION_CHANNEL_NAME
import com.eva.clockapp.core.di.commonModule
import com.eva.clockapp.features.alarms.di.alarmsModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class ClockApp : Application() {

	private val notificationManager by lazy { getSystemService<NotificationManager>() }

	override fun onCreate() {
		super.onCreate()
		// koin setup
		koinSetup()
		// create notification channels
		notificationChannelSetup()
	}


	private fun notificationChannelSetup() {
		val alarmsChannel = NotificationChannel(
			ALARMS_NOTIFICATION_CHANNEL_ID,
			ALARMS_NOTIFICATION_CHANNEL_NAME,
			NotificationManager.IMPORTANCE_HIGH
		).apply {
			description = ALARMS_NOTIFICATION_CHANNEL_DESCRIPTION
			lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
		}
		// creates channels
		notificationManager?.createNotificationChannels(listOf(alarmsChannel))
	}

	private fun koinSetup() {
		val modules = listOf(alarmsModule, commonModule)

		startKoin {
			androidLogger()
			androidContext(this@ClockApp)
			modules(modules)
		}
	}
}