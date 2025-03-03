package com.eva.clockapp.features.alarms.domain.controllers

import com.eva.clockapp.features.alarms.domain.models.AlarmsModel

interface AlarmsController {

	fun createAlarm(model: AlarmsModel, showToast: Boolean = true): Result<Unit>

	fun cancelAlarm(model: AlarmsModel): Result<Unit>
}