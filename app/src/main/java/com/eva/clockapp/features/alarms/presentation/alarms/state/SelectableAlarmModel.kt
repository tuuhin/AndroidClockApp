package com.eva.clockapp.features.alarms.presentation.alarms.state

import androidx.compose.runtime.Immutable
import com.eva.clockapp.features.alarms.domain.models.AlarmsModel

@Immutable
data class SelectableAlarmModel(
	val model: AlarmsModel,
	val isSelected: Boolean = false,
)