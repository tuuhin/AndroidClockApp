package com.eva.clockapp.features.alarms.presentation.alarms.state

import com.eva.clockapp.features.alarms.domain.models.AlarmsModel

data class SelectableAlarmModel(
	val model: AlarmsModel,
	val isSelected: Boolean = false,
)