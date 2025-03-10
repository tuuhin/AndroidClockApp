package com.eva.clockapp.features.alarms.data.services

import android.content.Intent
import android.util.Log
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
import kotlinx.coroutines.flow.filter
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
		basicTimerWatch.isCompleted
			.filter { result -> result == true }
			.onEach { completed ->
				// stop the watch
				basicTimerWatch.stop()

				_snoozeCount++
				Log.d(TAG, "UPDATING SNOOZE COUNT")

				_currentAlarm?.let { model ->

					val updatedTimeInSeconds = model.time.toSecondOfDay() +
							model.flags.snoozeInterval.duration.inWholeSeconds.toInt()

					val updatedAlarm = model
						.copy(time = LocalTime.fromSecondOfDay(updatedTimeInSeconds))

					startAlarmsForegroundService(
						NotificationsConstants.ALARMS_FOREGROUND_SERVICE_NOTIFICATION_ID,
						notifications.createNotification(applicationContext, updatedAlarm)
					)
				}
			}.launchIn(lifecycleScope)
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
		// then show foreground service
		startAlarmsForegroundService(
			NotificationsConstants.ALARMS_FOREGROUND_SERVICE_NOTIFICATION_ID,
			notifications.createNotification(applicationContext, alarm)
		)
		// start the alarms
		with(alarm) {
			if (flags.isVibrationEnabled) {
				// plain vibration
				vibrator.startVibration(flags.vibrationPattern, true)
			}
			if (flags.isSoundEnabled && soundUri != null) {

				val playVolume = if (flags.isVolumeStepIncrease)
					AssociateAlarmFlags.MIN_ALARM_SOUND
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
	}

	private fun onSnoozeAlarm() {

		val isSnoozeEnabled = _currentAlarm?.flags?.isSnoozeEnabled == true
		val snoozeDuration = _currentAlarm?.flags?.snoozeInterval?.duration ?: Duration.ZERO
		val snoozeRepeat = _currentAlarm?.flags?.snoozeRepeatMode?.times ?: 0

		if (!isSnoozeEnabled || snoozeDuration == Duration.ZERO || snoozeRepeat == 0) {
			Log.d(TAG, "SNOOZE WAS NOT ENABLED")
			stopForeground(STOP_FOREGROUND_REMOVE)
			stopSelf()
			return
		}
		if (_snoozeCount == snoozeRepeat) {
			// alarms missed notification
			Log.d(TAG, "CANNOT SNOOZE ALARM ANY MORE STOPPING SERVICE")
			stopSelf()
			return
		}
		// now we need to snooze the alarm
		basicTimerWatch.start(snoozeDuration)
		stopForeground(STOP_FOREGROUND_DETACH)
		stopRunningTasks()
		Log.d(TAG, "SERVICE IS RUNNING ALARM IS SNOOZED")
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
	}

	override fun onDestroy() {
		_currentAlarm = null
		stopRunningTasks()
		basicTimerWatch.cleanUp()

		Log.d(TAG, "SERVICE DESTROYED")
		super.onDestroy()
	}
}