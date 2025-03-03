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
import com.eva.clockapp.features.alarms.domain.repository.AlarmsRepository
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private const val TAG = "ALARM_SERVICE"

class AlarmsControllerService : LifecycleService(), KoinComponent {

	private val vibrator by inject<VibrationController>()
	private val player by inject<AlarmsSoundPlayer>()
	private val repository by inject<AlarmsRepository>()

	private val _notificationUtil by lazy { NotificationUtil(applicationContext) }
	private val _wakelockUtils by lazy { AlarmsWakeLockManager(applicationContext) }

	override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
		when (intent?.action) {
			ClockAppIntents.ACTION_START_ALARM -> {
				val alarmId = intent.getIntExtra(ClockAppIntents.EXTRA_ALARMS_ALARMS_ID, -1)
				if (alarmId != -1) prepareAlarm(alarmId)
			}

			ClockAppIntents.ACTION_SNOOZE_ALARM -> onSnoozeAlarm()
			ClockAppIntents.ACTION_CANCEL_ALARM -> onStopAlarm()
		}
		return super.onStartCommand(intent, flags, startId)
	}

	private fun prepareAlarm(alarmId: Int) = lifecycleScope.launch {
		val resource = repository.getAlarmFromId(alarmId)
		resource.fold(onSuccess = ::onPlayAlarm)
	}

	private fun onPlayAlarm(alarm: AlarmsModel) {
		Log.d(TAG, "ALARM FOUND:$alarm")
		// acquire the wake lock
		_wakelockUtils.acquireWakeLock()
		// then show foreground service
		startAlarmsForegroundService(
			NotificationsConstants.ALARMS_FOREGROUND_SERVICE_NOTIFICATION_ID,
			_notificationUtil.createNotification(applicationContext, alarm)
		)
	}

	private fun onSnoozeAlarm() {
		Log.d(TAG, "SNOOZE ALARM")
		stopForeground(STOP_FOREGROUND_DETACH)
		// release the lock
		_wakelockUtils.releaseWakeLock()
	}

	private fun onStopAlarm() {
		Log.d(TAG, "CANCEL ALARM")
		//clean-up code
		_wakelockUtils.releaseWakeLock()
		vibrator.stopVibration()
		player.stopSound()
		applicationContext.sendBroadcast(Intent(ClockAppIntents.ACTION_FINISH_ALARMS_ACTIVITY))

		stopSelf()
	}

	override fun onDestroy() {
		Log.d(TAG, "SERVICE DESTROYED")
		super.onDestroy()
	}
}