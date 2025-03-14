package com.eva.clockapp.features.alarms.presentation.create_alarm.state

import com.eva.clockapp.features.alarms.domain.models.WallpaperPhoto
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class AlarmsBackgroundState(
	val isLoaded: Boolean = false,
	val options: ImmutableList<WallpaperPhoto> = persistentListOf(),
)
