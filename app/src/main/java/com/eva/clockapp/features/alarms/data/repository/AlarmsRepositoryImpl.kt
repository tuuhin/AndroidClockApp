package com.eva.clockapp.features.alarms.data.repository

import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteException
import com.eva.clockapp.core.utils.Resource
import com.eva.clockapp.features.alarms.data.database.AlarmsDao
import com.eva.clockapp.features.alarms.data.util.toEntity
import com.eva.clockapp.features.alarms.data.util.toModel
import com.eva.clockapp.features.alarms.domain.exceptions.NoMatchingAlarmFoundException
import com.eva.clockapp.features.alarms.domain.models.AlarmsModel
import com.eva.clockapp.features.alarms.domain.models.CreateAlarmModel
import com.eva.clockapp.features.alarms.domain.repository.AlarmsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class AlarmsRepositoryImpl(private val alarmsDao: AlarmsDao) : AlarmsRepository {

	override val alarmsFlow: Flow<Resource<List<AlarmsModel>, Exception>>
		get() = flow {
			emit(Resource.Loading)
			try {
				val modelsFlow = alarmsDao.getAllAlarmsAsFlow().map { entities ->
					val models = entities.map { it.toModel() }
					Resource.Success<List<AlarmsModel>, Exception>(models)
				}
				emitAll(modelsFlow)
			} catch (e: SQLiteConstraintException) {
				emit(Resource.Error(e, message = "Constraint Error"))
			} catch (e: SQLiteException) {
				emit(Resource.Error(e, "SQLITE exception"))
			} catch (e: Exception) {
				emit(Resource.Error(e, message = "SOME KIND OF EXCEPTION"))
			} catch (e: Exception) {
				emit(Resource.Error(e, message = "Some Exception"))
			}
		}

	override suspend fun toggleIsAlarmEnabled(isEnabled: Boolean, model: AlarmsModel)
			: Resource<AlarmsModel, Exception> {
		return try {
			alarmsDao.switchIsEnableAlarm(alarmId = model.id, isEnabled = isEnabled)
			val alarm = alarmsDao.getAlarmFromId(model.id)
				?: return Resource.Error(NoMatchingAlarmFoundException())
			Resource.Success(alarm.toModel())
		} catch (e: SQLiteConstraintException) {
			Resource.Error(e, message = "Constraint Error")
		} catch (e: SQLiteException) {
			Resource.Error(e, "SQLITE exception")
		} catch (e: Exception) {
			Resource.Error(e, message = "SOME KIND OF EXCEPTION")
		}
	}

	override suspend fun createAlarm(model: CreateAlarmModel): Resource<AlarmsModel, Exception> {
		return try {
			val id = alarmsDao.insertOrUpdateAlarm(model.toEntity())
			val alarm = alarmsDao.getAlarmFromId(id.toInt())
				?: return Resource.Error(NoMatchingAlarmFoundException())
			Resource.Success(alarm.toModel())
		} catch (e: SQLiteConstraintException) {
			Resource.Error(e, message = "Constraint Error")
		} catch (e: SQLiteException) {
			Resource.Error(e, "SQLITE exception")
		} catch (e: Exception) {
			Resource.Error(e, message = "SOME KIND OF EXCEPTION")
		}
	}
}