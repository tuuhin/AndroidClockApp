package com.eva.clockapp.features.alarms.data.services

import android.content.Context
import android.os.PowerManager
import android.util.Log
import androidx.core.content.getSystemService
import com.eva.clockapp.core.constants.ClockAppIntents
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.DurationUnit

private const val TAG = "ALARM_WAKE_LOCK"

class AlarmsWakeLockManager(
	private val context: Context,
	private val duration: Duration = 5.minutes,
) {

	private val _powerManager by lazy { context.getSystemService<PowerManager>() }

	private var _wakeLock: PowerManager.WakeLock? = null

	fun acquireWakeLock() {
		val isSupported = _powerManager
			?.isWakeLockLevelSupported(PowerManager.PARTIAL_WAKE_LOCK)
			?: return

		if (!isSupported) {
			Log.d(TAG, "PARTIAL WAKE LOCK UNSUPPORTED")
			return
		}
		if (_wakeLock != null) {
			Log.d(TAG, "WAKE LOCK IS ALREADY ACQUIRED!")
			return
		}

		_wakeLock = _powerManager?.newWakeLock(
			PowerManager.PARTIAL_WAKE_LOCK,
			ClockAppIntents.ALARMS_WAKE_LOCK_TAG
		)

		_wakeLock?.acquire(duration.toLong(DurationUnit.MILLISECONDS))
		Log.d(TAG, "WAKE LOCK ACQUIRED FOR 10 MINUTES")
	}

	fun releaseWakeLock() {
		Log.d(TAG, "ALARM WAKE LOCK RELEASED")
		_wakeLock?.release()
		_wakeLock = null
	}
}