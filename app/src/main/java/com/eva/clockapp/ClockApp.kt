package com.eva.clockapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.core.app.NotificationCompat
import androidx.core.content.getSystemService
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.disk.DiskCache
import coil3.disk.directory
import coil3.request.CachePolicy
import coil3.request.crossfade
import coil3.size.Precision
import coil3.util.DebugLogger
import com.eva.clockapp.core.constants.NotificationsConstants
import com.eva.clockapp.core.di.commonModule
import com.eva.clockapp.features.alarms.data.worker.EnqueueDailyAlarmWorker
import com.eva.clockapp.features.alarms.di.alarmsModule
import com.eva.clockapp.features.settings.di.settingsModule
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class ClockApp : Application(), SingletonImageLoader.Factory {

	private val notificationManager by lazy { getSystemService<NotificationManager>() }

	override fun onCreate() {
		super.onCreate()
		// koin setup
		koinSetup()
		// create notification channels
		notificationChannelSetup()
		// add workers
		addWorkers()
	}


	private fun notificationChannelSetup() {
		val alarmsChannel = NotificationChannel(
			NotificationsConstants.ALARMS_NOTIFICATION_CHANNEL_ID,
			NotificationsConstants.ALARMS_NOTIFICATION_CHANNEL_NAME,
			NotificationManager.IMPORTANCE_HIGH
		).apply {
			description = NotificationsConstants.ALARMS_NOTIFICATION_CHANNEL_DESCRIPTION
			lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
		}

		val clockEventChannel = NotificationChannel(
			NotificationsConstants.CLOCK_EVENT_NOTIFICATION_CHANNEL_ID,
			NotificationsConstants.CLOCK_EVENT_NOTIFICATION_CHANNEL_NAME,
			NotificationManager.IMPORTANCE_DEFAULT
		).apply {
			description = NotificationsConstants.CLOCK_EVENT_NOTIFICATION_DESC
			lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
		}
		// creates channels
		notificationManager?.createNotificationChannels(listOf(alarmsChannel, clockEventChannel))
	}


	private fun koinSetup() {
		val modules = listOf(alarmsModule, settingsModule, commonModule)

		startKoin {
			androidLogger()
			androidContext(this@ClockApp)
			modules(modules)
		}
	}

	private fun addWorkers() {
		// enqueue alarms at midnight
		EnqueueDailyAlarmWorker.startWorker(applicationContext)
	}

	override fun newImageLoader(context: PlatformContext): ImageLoader {

		val diskCache = DiskCache.Builder()
			.directory(cacheDir)
			.minimumMaxSizeBytes(1024 * 1024L)
			.maxSizePercent(.7)
			.build()

		val debugLogger = DebugLogger()

		return ImageLoader.Builder(context)
			.crossfade(true)
			.crossfade(400)
			.decoderCoroutineContext(Dispatchers.Default)
			.memoryCachePolicy(CachePolicy.DISABLED)
			.precision(Precision.EXACT)
			.diskCachePolicy(CachePolicy.ENABLED)
			.diskCache(diskCache)
			.logger(debugLogger)
			.build()
	}
}