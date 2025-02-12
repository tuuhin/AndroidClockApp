package com.eva.clockapp.features.alarms.presentation.create_alarm.state

import com.eva.clockapp.features.alarms.domain.models.RingtoneMusicFile
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class AlarmSoundOptions(
	val selectedSound: RingtoneMusicFile,
	val external: ImmutableList<RingtoneMusicFile> = persistentListOf(),
	val local: ImmutableList<RingtoneMusicFile> = persistentListOf(),
)
