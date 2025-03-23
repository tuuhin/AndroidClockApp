package com.eva.clockapp.features.alarms.data.services

import android.app.NotificationManager
import android.content.Intent
import android.util.Log
import androidx.core.content.getSystemService
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.eva.clockapp.core.constants.ClockAppIntents
import com.eva.clockapp.core.constants.NotificationsConstants
import com.eva.clockapp.features.alarms.domain.controllers.AlarmsSoundPlayer
import com.eva.clockapp.features.alarms.domain.controllers.VibrationController
import com.eva.clockapp.features.alarms.domain.models.AlarmsModel
import com.eva.clockapp.features.alarms.domain.models.AssociateAlarmFlags
import com.eva.clockapp.features.alarms.domain.repository.AlarmsRepository
import com.eva.clockapp.features.alarms.domain.utils.BasicTimerWatch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

private const val TAG = "ALARM_SERVICE"

class AlarmsControllerService : LifecycleService(), KoinComponent {

	private val vibrator by inject<VibrationController>()
	private val player by inject<AlarmsSoundPlayer>()
	private val repository by inject<AlarmsRepository>()
	private val notifications by inject<AlarmsNotificationProvider>()

	private val _wakeLock by lazy { AlarmsWakeLockManager(applicationContext) }
	private val _notificationManager by lazy { getSystemService<NotificationManager>() }

	private val basicTimerWatch = BasicTimerWatch()
	private var _snoozeCount: Int = 0
	private var _currentAlarm: AlarmsModel? = null

	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
		when (intent?.action) {
			ClockAppIntents.ACTION_PLAY_ALARM -> {
				val alarmId = intent.getIntExtra(ClockAppIntents.EXTRA_ALARMS_ALARMS_ID, -1)
				if (alarmId != -1) prepareAlarm(alarmId)
			}

			ClockAppIntents.ACTION_SNOOZE_ALARM -> onSnoozeAlarm()
			ClockAppIntents.ACTION_CANCEL_ALARM -> onStopAlarm()
		}
		return super.onStartCommand(intent, flags, startId)
	}

	override fun onCreate() {
		super.onCreate()

		// check timer watch is completed
		basicTimerWatch.runIfCompletedAsync {
			_snoozeCount++
			Log.d(TAG, "UPDATING SNOOZE COUNT")

			val alarm = _currentAlarm ?: return@runIfCompletedAsync

			val snoozeDuration = alarm.flags.snoozeInterval.duration
			val updatedTime = alarm.time.toSecondOfDay() + snoozeDuration.inWholeSeconds.toInt()

			val updatedAlarm = alarm.copy(time = LocalTime.fromSecondOfDay(updatedTime))

			_wakeLock.acquireLock()

			// we cannot use start foreground again as it need to done from startForeground
			// so we show the notification only and the service will be run until it cancels or
			// stop is placed.
			_notificationManager?.notify(
				NotificationsConstants.ALARMS_FOREGROUND_SERVICE_NOTIFICATION_ID,
				notifications.createNotification(applicationContext, updatedAlarm)
			)

			alarm.playSoundAndVibration()
		}
	}


	private fun prepareAlarm(alarmId: Int) = lifecycleScope.launch {
		val resource = repository.getAlarmFromId(alarmId)
		resource.fold(onSuccess = ::onPlayAlarm)
	}

	private fun onPlayAlarm(alarm: AlarmsModel) {
		if (_currentAlarm != null) {
			Log.d(TAG, "ALREADY AN ALARM IS IN RUNNING STATE NEED TO CANCEL IT TO CONTINUE")
			return
		}
		_currentAlarm = alarm
		Log.d(TAG, "ALARM :$alarm")
		// acquire the wake lock
		_wakeLock.acquireLock()

		//cancel upcoming notification
		cancelIfUpcomingNotificationShown()
		// then show foreground service
		startAlarmsForegroundService(
			NotificationsConstants.ALARMS_FOREGROUND_SERVICE_NOTIFICATION_ID,
			notifications.createNotification(applicationContext, alarm)
		)
		// start the alarms
		alarm.playSoundAndVibration()
	}

	private fun onSnoozeAlarm() = _currentAlarm?.let { alarm ->

		val isSnoozeEnabled = alarm.flags.isSnoozeEnabled
		val snoozeDuration = alarm.flags.snoozeInterval.duration
		// times to repeat = repeat + 1
		val snoozeRepeat = alarm.flags.snoozeRepeatMode.times + 1

		if (!isSnoozeEnabled || snoozeDuration == Duration.ZERO) {
			Log.d(TAG, "SNOOZE WAS NOT ENABLED OR SNOOZE DURATION IS ZERO")
			stopForeground(STOP_FOREGROUND_REMOVE)
			stopSelf()
			return@let
		}
		if (_snoozeCount == snoozeRepeat) {
			// Missed alarm notification
			val notificationId = NotificationsConstants.notificationIdFromModel(alarm)
			val missedNotification = notifications.createMissedAlarmNotification(alarm)
			_notificationManager?.notify(notificationId, missedNotification)

			Log.d(TAG, "CANNOT SNOOZE ALARM ANY MORE STOPPING SERVICE")
			stopSelf()
			return@let
		}
		// now we need to snooze the alarm
		basicTimerWatch.start(snoozeDuration)

		// remove the service
		stopForeground(STOP_FOREGROUND_REMOVE)
		// stop the running tasks
		stopRunningTasks()
		Log.d(TAG, "SERVICE IS RUNNING ALARM IS SNOOZED")
	} ?: run {
		Log.d(TAG, "ALARM WASN'T SET CANNOT DO ANYTHING")
		stopSelf()
	}


	private fun onStopAlarm() {
		Log.d(TAG, "CANCEL ALARM")
		stopSelf()
	}

	private fun incrementalFloatSequence(
		maxValue: Float,
		delayEmit: Duration = 2.seconds,
		incrementBy: Float = 5f,
	): Flow<Float> = flow {
		var current = 1f
		while (current < maxValue) {
			emit(current)
			current += incrementBy
			delay(delayEmit)
		}
	}.flowOn(Dispatchers.Default)


	private fun stopRunningTasks() {
		_wakeLock.releaseLock()
		vibrator.stopVibration()
		player.stopSound()
		// the broadcast receiver at the alarm activity to finish the alarm
		sendBroadcast(Intent(ClockAppIntents.ACTION_FINISH_ALARM_ACTIVITY))
		Log.d(TAG, "RUNNING TASKS DISMISSED")
	}

	private fun AlarmsModel.playSoundAndVibration() {
		if (flags.isVibrationEnabled) {
			// plain vibration
			vibrator.startVibration(flags.vibrationPattern, true)
		}
		if (flags.isSoundEnabled && soundUri != null) {

			val playVolume = if (flags.isVolumeStepIncrease) AssociateAlarmFlags.MIN_ALARM_SOUND
			else flags.alarmVolume
			// start playing the sound
			player.playSound(soundUri, playVolume, true)
			// if incremental increase
			if (flags.isVolumeStepIncrease) {
				val flow = incrementalFloatSequence(flags.alarmVolume)
				// step-by-step increase the volume
				flow.onEach { volume -> player.changeVolume(volume) }
					.launchIn(lifecycleScope)
			}
		}
	}

	private fun cancelIfUpcomingNotificationShown() {
		val alarmModel = _currentAlarm ?: return
		val notificationIfPresent = _notificationManager?.activeNotifications
			?.find { it.id == NotificationsConstants.notificationIdFromModel(alarmModel) }
			?: return
		// cancel the notification if shown
		_notificationManager?.cancel(notificationIfPresent.id)
	}

	override fun onDestroy() {
		_currentAlarm = null
		stopRunningTasks()
		basicTimerWatch.cleanUp()

		Log.d(TAG, "SERVICE DESTROYED")
		super.onDestroy()
	}
}