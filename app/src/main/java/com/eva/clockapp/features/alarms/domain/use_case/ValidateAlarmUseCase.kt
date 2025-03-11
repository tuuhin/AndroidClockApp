package com.eva.clockapp.features.alarms.domain.use_case

import com.eva.clockapp.core.utils.Resource
import com.eva.clockapp.features.alarms.domain.models.AlarmsModel
import com.eva.clockapp.features.alarms.domain.models.CreateAlarmModel
import com.eva.clockapp.features.alarms.domain.models.WeekDays
import com.eva.clockapp.features.alarms.domain.repository.AlarmsRepository
import kotlinx.datetime.LocalTime

class ValidateAlarmUseCase(private val repository: AlarmsRepository) {

	suspend fun validateCreateAlarm(model: CreateAlarmModel): Validator {
		return when {
			model.weekDays.isEmpty() -> Validator(
				message = "At least one week day need to be selected",
				isValid = false
			)

			!checkIfAnyOtherWithSameConfigExists(
				time = model.time,
				weekDays = model.weekDays
			) -> Validator(message = "Intersecting with other alarm", isValid = false)

			else -> Validator(message = null, isValid = true)
		}
	}

	suspend fun validateUpdate(model: AlarmsModel): Validator {
		return when {
			model.weekDays.isEmpty() -> Validator(
				message = "At least one week day need to be selected",
				isValid = false
			)

			!checkIfAnyOtherWithSameConfigExists(
				time = model.time,
				weekDays = model.weekDays
			) -> Validator(message = "Intersecting with other alarm", isValid = false)

			else -> Validator(message = null, isValid = true)
		}
	}

	suspend fun checkIfAnyOtherWithSameConfigExists(time: LocalTime, weekDays: WeekDays): Boolean {
		val alarms = (repository.getAllAlarms() as? Resource.Success)?.data ?: return false
		return alarms.any { it.time == time && it.weekDays.intersect(weekDays).isNotEmpty() }
	}
}