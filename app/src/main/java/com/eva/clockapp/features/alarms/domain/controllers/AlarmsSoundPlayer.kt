package com.eva.clockapp.features.alarms.domain.controllers

import kotlinx.coroutines.flow.Flow

interface AlarmsSoundPlayer {

	val isPlaying: Flow<Boolean>

	fun playSound(musicUri: String, soundVolume: Float, loop: Boolean = true): Result<Unit>

	fun changeVolume(soundVolume: Float): Result<Boolean>

	fun stopSound()
}