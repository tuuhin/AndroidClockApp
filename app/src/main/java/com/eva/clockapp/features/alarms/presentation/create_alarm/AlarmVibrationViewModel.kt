package com.eva.clockapp.features.alarms.presentation.create_alarm

import androidx.lifecycle.ViewModel
import com.eva.clockapp.features.alarms.domain.controllers.VibrationController
import com.eva.clockapp.features.alarms.presentation.create_alarm.state.AlarmVibrationEvents

class AlarmVibrationViewModel(
	private val controller: VibrationController,
) : ViewModel() {

	fun onEvent(event: AlarmVibrationEvents) {
		when (event) {
			is AlarmVibrationEvents.OnVibrationEnabled -> {
				// if not enable stop the vibration
				if (!event.isEnabled) controller.stopVibration()
			}

			is AlarmVibrationEvents.OnVibrationSelected -> {
				//play the vibration pattern
				controller.startVibration(event.pattern)
			}
		}
	}

	override fun onCleared() {
		// stop any running vibration
		controller.stopVibration()
		super.onCleared()
	}
}