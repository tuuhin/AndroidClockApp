package com.eva.clockapp.features.alarms.domain.controllers

import com.eva.clockapp.core.utils.Resource
import kotlinx.coroutines.flow.Flow

interface AlarmsSoundPlayer {

	val isPlaying: Flow<Boolean>

	val canSetVolume: Boolean

	fun playSound(musicUri: String, soundVolume: Float, loop: Boolean = true)
			: Resource<Unit, Exception>

	fun changeVolume(soundVolume: Float): Resource<Unit, Exception>

	fun stopSound()
}