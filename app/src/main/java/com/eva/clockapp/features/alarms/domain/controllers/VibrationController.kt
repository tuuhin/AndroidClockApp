package com.eva.clockapp.features.alarms.domain.controllers

import com.eva.clockapp.features.alarms.domain.models.VibrationPattern

interface VibrationController {

	fun startVibration(pattern: VibrationPattern, repeat: Boolean = false)

	fun stopVibration()
}