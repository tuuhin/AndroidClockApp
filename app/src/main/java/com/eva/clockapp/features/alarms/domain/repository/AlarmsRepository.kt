package com.eva.clockapp.features.alarms.domain.repository

import com.eva.clockapp.core.utils.Resource
import com.eva.clockapp.features.alarms.domain.models.AlarmsModel
import com.eva.clockapp.features.alarms.domain.models.CreateAlarmModel
import kotlinx.coroutines.flow.Flow

interface AlarmsRepository {

	val alarmsFlow: Flow<Resource<List<AlarmsModel>, Exception>>

	suspend fun toggleIsAlarmEnabled(isEnabled: Boolean, model: AlarmsModel)
			: Resource<AlarmsModel, Exception>

	suspend fun createAlarm(model: CreateAlarmModel): Resource<AlarmsModel, Exception>
}