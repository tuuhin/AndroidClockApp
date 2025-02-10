package com.eva.clockapp.features.alarms.data.controllers

import android.content.Context
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationAttributes
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.content.getSystemService
import com.eva.clockapp.features.alarms.domain.controllers.VibrationController
import com.eva.clockapp.features.alarms.domain.models.VibrationPattern

class VibrationControllerImpl(private val context: Context) : VibrationController {

	private val manager by lazy { context.getSystemService<VibratorManager>() }
	private val vibrator by lazy { context.getSystemService<Vibrator>() }


	override fun startVibration(pattern: VibrationPattern, repeat: Boolean) {
		try {
			val repeatInterval = if (repeat) 0 else -1
			val effect = VibrationEffect.createWaveform(pattern.patterns, repeatInterval)
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

				val vibration = CombinedVibration.createParallel(effect)
				val attributes = VibrationAttributes.Builder()
					.setUsage(VibrationAttributes.USAGE_ALARM)
					.build()
				manager?.vibrate(vibration, attributes)
			} else {
				vibrator?.vibrate(effect)
			}
		} catch (e: Exception) {
			e.printStackTrace()
		}
	}

	override fun stopVibration() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
			manager?.cancel()
		else vibrator?.cancel()
	}
}