package com.eva.clockapp.features.alarms.domain.controllers

import com.eva.clockapp.features.alarms.domain.models.AlarmsModel
import kotlinx.datetime.LocalDateTime

interface AlarmsController {

	fun createAlarm(model: AlarmsModel): Result<LocalDateTime>

	fun cancelAlarm(model: AlarmsModel): Result<Unit>
}