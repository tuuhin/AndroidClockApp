package com.eva.clockapp.features.alarms.domain.controllers

import com.eva.clockapp.features.alarms.domain.enums.VibrationPattern

fun interface VibrationController {

	fun invoke(pattern: VibrationPattern)
}