package com.eva.clockapp.features.alarms.data.repository

import android.content.Context
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteException
import com.eva.clockapp.R
import com.eva.clockapp.core.utils.Resource
import com.eva.clockapp.features.alarms.data.database.AlarmsDao
import com.eva.clockapp.features.alarms.data.database.AlarmsEntity
import com.eva.clockapp.features.alarms.data.database.toEntity
import com.eva.clockapp.features.alarms.data.database.toModel
import com.eva.clockapp.features.alarms.domain.controllers.AlarmsController
import com.eva.clockapp.features.alarms.domain.exceptions.NoMatchingAlarmFoundException
import com.eva.clockapp.features.alarms.domain.models.AlarmsModel
import com.eva.clockapp.features.alarms.domain.models.CreateAlarmModel
import com.eva.clockapp.features.alarms.domain.repository.AlarmsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

class AlarmsRepositoryImpl(
	private val alarmsDao: AlarmsDao,
	private val controller: AlarmsController,
	private val context: Context,
) : AlarmsRepository {

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
				e.printStackTrace()
				emit(Resource.Error(e, message = "SOME KIND OF EXCEPTION"))
			}
		}.flowOn(Dispatchers.IO)

	override suspend fun getAllAlarms(): Resource<List<AlarmsModel>, Exception> {
		return withContext(Dispatchers.IO) {
			checkAndReturnDbError {
				val result = alarmsDao.getAllAlarmsAsList().map(AlarmsEntity::toModel)
				Resource.Success(result)
			}
		}
	}

	override suspend fun toggleIsAlarmEnabled(isEnabled: Boolean, model: AlarmsModel)
			: Resource<AlarmsModel, Exception> {
		return withContext(Dispatchers.IO) {
			checkAndReturnDbError {
				alarmsDao.switchIsEnableAlarm(alarmId = model.id, isEnabled = isEnabled)
				val entity = alarmsDao.getAlarmFromId(model.id)
					?: return@withContext Resource.Error(NoMatchingAlarmFoundException())
				val model = entity.toModel()
				// turn on off alarm
				if (model.isAlarmEnabled) {
					val result = controller.createAlarm(model)

					val message = if (result.isSuccess) {
						val alarmTime = result.getOrThrow()
						context.createAlarmToastMessage(alarmTime)
					} else null

					return@withContext Resource.Success(model, message)
				}
				controller.cancelAlarm(model)
				Resource.Success(model)
			}
		}
	}

	override suspend fun createAlarm(model: CreateAlarmModel): Resource<AlarmsModel, Exception> {
		return withContext(Dispatchers.IO) {
			checkAndReturnDbError {
				val id = alarmsDao.insertOrUpdateAlarm(model.toEntity())
				val alarm = alarmsDao.getAlarmFromId(id.toInt())
					?: return@withContext Resource.Error(NoMatchingAlarmFoundException())

				val model = alarm.toModel()
				val result = controller.createAlarm(model, true)

				val message = if (result.isSuccess) {
					val alarmTime = result.getOrThrow()
					context.createAlarmToastMessage(alarmTime)
				} else null

				Resource.Success(model, message)
			}
		}
	}

	override suspend fun updateAlarm(model: AlarmsModel): Resource<AlarmsModel, Exception> {
		return withContext(Dispatchers.IO) {
			checkAndReturnDbError {
				alarmsDao.insertOrUpdateAlarm(model.toEntity())
				val entity = alarmsDao.getAlarmFromId(model.id)
					?: return@withContext Resource.Error(NoMatchingAlarmFoundException())
				val model = entity.toModel()
				// update alarm
				if (model.isAlarmEnabled) {
					val result = controller.createAlarm(model, true)

					val message = if (result.isSuccess) {
						val alarmTime = result.getOrThrow()
						context.createAlarmToastMessage(alarmTime)
					} else null

					return@withContext Resource.Success(model, message)
				}
				controller.cancelAlarm(model)
				Resource.Success(model)
			}
		}
	}

	override suspend fun deleteAlarm(model: AlarmsModel): Resource<Unit, Exception> {
		return withContext(Dispatchers.IO) {
			checkAndReturnDbError {
				alarmsDao.deleteAlarm(model.toEntity())
				Resource.Success(Unit)
			}

		}
	}

	override suspend fun deleteAlarms(models: List<AlarmsModel>): Resource<Unit, Exception> {
		return withContext(Dispatchers.IO) {
			checkAndReturnDbError {
				val entities = models.map { it.toEntity() }
				alarmsDao.deleteAlarmBulk(entities)
				Resource.Success(Unit)
			}
		}
	}

	override suspend fun getAlarmFromId(id: Int): Resource<AlarmsModel, Exception> {
		return withContext(Dispatchers.IO) {
			checkAndReturnDbError {
				val alarm = alarmsDao.getAlarmFromId(id)?.toModel()
					?: return@withContext Resource.Error(NoMatchingAlarmFoundException())
				Resource.Success(alarm)
			}
		}
	}

	private suspend inline fun <T> checkAndReturnDbError(
		code: suspend () -> Resource.Success<T, Exception>,
	): Resource<T, Exception> {
		return try {
			code()
		} catch (e: SQLiteConstraintException) {
			Resource.Error(e, message = context.getString(R.string.error_db_constraint))
		} catch (e: SQLiteException) {
			Resource.Error(e, context.getString(R.string.error_db_exception))
		} catch (e: Exception) {
			Resource.Error(e, message = context.getString(R.string.error_unknown))
		}
	}

	private fun Context.createAlarmToastMessage(alarmTime: LocalDateTime): String {
		val timeZone = TimeZone.currentSystemDefault()
		val duration = alarmTime.toInstant(timeZone) - Clock.System.now()

		val days = duration.inWholeDays
		val hours = duration.inWholeHours % 24
		val minutes = duration.inWholeMinutes % 60

		val alarmText = buildString {
			when {
				days > 0 -> append("$days d")
				hours > 0 -> append("$hours h")
				minutes > 0 -> append("$minutes m")
				else -> return getString(R.string.next_alarm_within_one_min)
			}
		}
		return getString(R.string.alarm_set_after_time, alarmText)
	}

}