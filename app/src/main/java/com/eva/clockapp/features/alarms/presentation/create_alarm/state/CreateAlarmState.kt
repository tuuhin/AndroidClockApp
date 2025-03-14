package com.eva.clockapp.features.alarms.presentation.create_alarm.state

import androidx.compose.runtime.Stable
import com.eva.clockapp.features.alarms.domain.models.RingtoneMusicFile
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime

@Stable
data class CreateAlarmState(
	val startTime: LocalTime = LocalTime(0, 0),
	val selectedTime: LocalTime = LocalTime(0, 0),
	val selectedDays: ImmutableSet<DayOfWeek> = persistentSetOf(),
	val labelState: String = "",
	val ringtone: RingtoneMusicFile,
	val isAlarmCreate: Boolean = true,
	val backgroundImageUri: String? = null,
)
