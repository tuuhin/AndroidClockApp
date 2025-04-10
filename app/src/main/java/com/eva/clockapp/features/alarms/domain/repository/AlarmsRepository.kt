package com.eva.clockapp.features.alarms.domain.repository

import com.eva.clockapp.core.utils.Resource
import com.eva.clockapp.features.alarms.domain.models.AlarmsModel
import com.eva.clockapp.features.alarms.domain.models.CreateAlarmModel
import kotlinx.coroutines.flow.Flow

interface AlarmsRepository {

	val alarmsFlow: Flow<Resource<List<AlarmsModel>, Exception>>

	suspend fun getAllAlarms(): Resource<List<AlarmsModel>, Exception>

	suspend fun getAllEnabledAlarms(): Result<List<AlarmsModel>>

	suspend fun toggleIsAlarmEnabled(isEnabled: Boolean, model: AlarmsModel)
			: Resource<AlarmsModel, Exception>

	suspend fun toggleIsAlarmEnabledBuck(isEnabled: Boolean, models: List<AlarmsModel>)
			: Resource<Unit, Exception>

	suspend fun createAlarm(model: CreateAlarmModel): Resource<AlarmsModel, Exception>

	suspend fun updateAlarm(model: AlarmsModel): Resource<AlarmsModel, Exception>

	suspend fun deleteAlarm(model: AlarmsModel): Resource<Unit, Exception>

	suspend fun deleteAlarms(models: List<AlarmsModel>): Resource<Unit, Exception>

	suspend fun getAlarmFromId(id: Int): Resource<AlarmsModel, Exception>
}