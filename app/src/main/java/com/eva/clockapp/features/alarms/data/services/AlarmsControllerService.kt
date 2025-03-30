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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalTime
import kotlinx.datetime.atTime
import kotlinx.datetime.toKotlinLocalDateTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.LocalDateTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

private const val TAG = "ALARM_SERVICE"

class AlarmsControllerService : LifecycleService(), KoinComponent {

	private val vibrator by inject<VibrationController>()
	private val player by inject<AlarmsSoundPlayer>()
	private val repository by inject<AlarmsRepository>()
	private val _notificationProvider by inject<AlarmsNotificationProvider>()

	private val _wakeLock by lazy { AlarmsWakeLockManager(applicationContext) }
	private val _controller by lazy { SnoozeAlarmController(applicationContext) }
	private val _store by lazy { AlarmsDataStoreManager(applicationContext) }

	override fun onCreate() {
		super.onCreate()
		Log.i(TAG, "SERVICE CREATED")
	}

	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

		Log.i(TAG, "SERVICE INTENT ACTION ${intent?.action}")

		when (intent?.action) {
			ClockAppIntents.ACTION_PLAY_ALARM -> {
				val alarmId = intent.getIntExtra(ClockAppIntents.EXTRA_ALARMS_ALARMS_ID, -1)
				evaluateAlarmAndPerformOperation(
					alarmId = alarmId,
					operation = { alarm -> onPlayAlarm(alarm) },
				)
			}

			ClockAppIntents.ACTION_SNOOZE_ALARM -> {
				val alarmId = intent.getIntExtra(ClockAppIntents.EXTRA_ALARMS_ALARMS_ID, -1)
				evaluateAlarmAndPerformOperation(alarmId = alarmId, ::onSnoozeAlarm)
			}

			ClockAppIntents.ACTION_PLAY_ALARM_AFTER_SNOOZE -> {
				val alarmId = intent.getIntExtra(ClockAppIntents.EXTRA_ALARMS_ALARMS_ID, -1)
				evaluateAlarmAndPerformOperation(
					alarmId,
					operation = { alarm -> onPlayAlarm(alarm, true) },
				)
			}

			ClockAppIntents.ACTION_CANCEL_ALARM -> onStopAlarm()

			ClockAppIntents.ACTION_CHANGE_ALARM_VOLUME -> {
				val isIncrease = intent
					.getBooleanExtra(ClockAppIntents.EXTRAS_ALARM_VOLUME_INCREASE, false)

				onChangeAlarmSound(isIncrease)
			}

		}
		return super.onStartCommand(intent, flags, startId)
	}

	private suspend fun onPlayAlarm(alarm: AlarmsModel, isSnoozed: Boolean = false) {

		var mutableAlarm = alarm

		if (isSnoozed) {
			val triggerTimeAfterActual = _store.getSnoozeCount() *
					alarm.flags.snoozeInterval.duration.inWholeSeconds.toInt()

			val updatedTime = alarm.time.toSecondOfDay() + triggerTimeAfterActual

			val updatedDateTime = LocalDateTime.now().toKotlinLocalDateTime().date
				.atTime(LocalTime.fromSecondOfDay(updatedTime))

			mutableAlarm = alarm.copy(time = updatedDateTime.time)
		}

		// acquire the wake lock
		_wakeLock.acquireLock()
		//cancel upcoming notification
		_notificationProvider.cancelAlarmNotificationIfActive(mutableAlarm)
		// then show foreground service
		startAlarmsForegroundService(
			NotificationsConstants.ALARMS_FOREGROUND_SERVICE_NOTIFICATION_ID,
			_notificationProvider.createAlarmNotification(mutableAlarm)
		)
		playSoundAndVibration(mutableAlarm)
	}


	private suspend fun onSnoozeAlarm(alarm: AlarmsModel) {

		val isSnoozeEnabled = alarm.flags.isSnoozeEnabled
		val snoozeDuration = alarm.flags.snoozeInterval.duration
		// times to repeat = repeat + 1
		val snoozeRepeat = alarm.flags.snoozeRepeatMode.times
		val snoozeCount = _store.getSnoozeCount()

		Log.d(TAG, "SNOOZE COUNT CURRENT :$snoozeCount TOTAL :$snoozeRepeat")
		Log.d(TAG, "SNOOZE DURATION :$snoozeDuration")

		if (!isSnoozeEnabled || snoozeDuration == Duration.ZERO) {
			Log.d(TAG, "SNOOZE WAS NOT ENABLED OR SNOOZE DURATION IS ZERO")
			stopForeground(STOP_FOREGROUND_REMOVE)
			onStopAlarm()
			return
		}
		if (snoozeCount == snoozeRepeat) {
			// clear the snooze count
			_store.setSnoozeCount(0)
			// Missed alarm notification
			_notificationProvider.showAlarmMissedNotification(alarm)
			Log.d(TAG, "CANNOT SNOOZE ALARM ANY MORE STOPPING SERVICE")
			stopForeground(STOP_FOREGROUND_REMOVE)
			stopSelf()
			return
		}
		_store.setSnoozeCount(snoozeCount + 1)
		// Alarm is snoozed so remove the notification and set alarm to be played
		// after snooze duration
		_controller.startAlarmAfterSnooze(alarm, _store.getSnoozeCount())
		// remove the notification
		stopForeground(STOP_FOREGROUND_REMOVE)
		// stop the running tasks
		stopRunningTasks()
		Log.d(TAG, "SERVICE IS RUNNING ALARM IS SNOOZED")

	}


	private fun onChangeAlarmSound(isIncrease: Boolean = false) {
		// TODO: Unimplemented
		Log.d(TAG, "IS_INCREASE:$isIncrease")
	}

	private fun onStopAlarm() {
		Log.d(TAG, "ALARMS IS REQUESTED TO BE CANCELLED")
		lifecycleScope.launch {
			_store.setSnoozeCount(0)
			stopSelf()
		}
	}

	private fun stopRunningTasks() {
		_wakeLock.releaseLock()
		vibrator.stopVibration()
		player.stopSound()
		// the broadcast receiver at the alarm activity to finish the alarm
		sendBroadcast(Intent(ClockAppIntents.ACTION_FINISH_ALARM_ACTIVITY))
		Log.d(TAG, "RUNNING TASKS DISMISSED")
	}

	private fun playSoundAndVibration(alarm: AlarmsModel) {

		val flags = alarm.flags
		val soundUri = alarm.soundUri

		val playVolume = if (flags.isVolumeStepIncrease)
			AssociateAlarmFlags.MIN_ALARM_SOUND
		else flags.alarmVolume

		if (flags.isVibrationEnabled) {
			// plain vibration
			vibrator.startVibration(flags.vibrationPattern, true)
		}
		if (flags.isSoundEnabled && soundUri != null) {
			// start playing the sound
			player.playSound(soundUri, playVolume, true)
			// if incremental increase
			if (flags.isVolumeStepIncrease) {
				val flow = incrementalFloatSequence(
					maxValue = flags.alarmVolume,
					startValue = AssociateAlarmFlags.MIN_ALARM_SOUND.toFloat()
				)
				// step-by-step increase the volume
				flow.onEach { volume -> player.changeVolume(volume) }
					.launchIn(lifecycleScope)
			}
		}
	}


	override fun onDestroy() {
		stopRunningTasks()

		Log.d(TAG, "SERVICE DESTROYED")
		super.onDestroy()
	}

	private fun evaluateAlarmAndPerformOperation(
		alarmId: Int,
		operation: suspend (AlarmsModel) -> Unit,
	) {
		//Note:
		// For most of the intent action an associated alarmId is needed to query the alarm
		// from the database, querying it everytime as the service might be killed by
		// the system, so we cannot hold any state in the service thus querying it is most
		// efficient, if the alarm not found the service is stopped.
		lifecycleScope.launch {
			repository.getAlarmFromId(alarmId)
				.fold(
					onSuccess = { operation(it) },
					onError = { err, _ ->
						err.printStackTrace()
						Log.d(TAG, "ALARM NOT FOUND STOPPING SERVICE")
						stopSelf()
					},
				)
		}
	}
}

private fun incrementalFloatSequence(
	maxValue: Float,
	delay: Duration = 3.seconds,
	incrementBy: Float = 5f,
	startValue: Float = 1f,
): Flow<Float> {
	return flow {
		var current = startValue
		while (current < maxValue) {
			emit(current)
			current += incrementBy
			delay(delay)
		}
	}.flowOn(Dispatchers.Default)
}