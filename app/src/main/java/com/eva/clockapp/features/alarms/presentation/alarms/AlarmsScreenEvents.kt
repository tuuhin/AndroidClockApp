package com.eva.clockapp.features.alarms.presentation.alarms

import com.eva.clockapp.features.alarms.domain.models.AlarmsModel

sealed interface AlarmsScreenEvents {

	data class OnEnableOrDisAbleAlarm(
		val isEnabled: Boolean,
		val alarmsModel: AlarmsModel,
	) : AlarmsScreenEvents
}