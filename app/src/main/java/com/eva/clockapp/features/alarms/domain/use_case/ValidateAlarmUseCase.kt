package com.eva.clockapp.features.alarms.domain.use_case

import com.eva.clockapp.core.utils.Resource
import com.eva.clockapp.features.alarms.domain.models.AlarmsModel
import com.eva.clockapp.features.alarms.domain.models.CreateAlarmModel
import com.eva.clockapp.features.alarms.domain.repository.AlarmsRepository

class ValidateAlarmUseCase(private val repository: AlarmsRepository) {

	suspend fun validateCreateAlarm(model: CreateAlarmModel): Validator {
		return when {
			model.weekDays.isEmpty() -> Validator(
				message = "At least one week day need to be selected",
				isValid = false
			)

			checkIfAlarmSameSpecs(model) -> Validator(
				message = "Already have an alarm on that time for a weekday",
				isValid = false
			)

			else -> Validator(message = null, isValid = true)
		}
	}

	suspend fun validateUpdate(model: AlarmsModel): Validator {
		return when {
			model.weekDays.isEmpty() -> Validator(
				message = "At least one week day need to be selected",
				isValid = false
			)

			checkIfAlarmKindOfExistsExceptThis(model) -> Validator(
				message = "Some other alarm have same weekday or time selected",
				isValid = false
			)

			else -> Validator(message = null, isValid = true)
		}
	}

	suspend fun checkIfAlarmSameSpecs(alarmModel: CreateAlarmModel): Boolean {
		val alarms = (repository.getAllAlarms() as? Resource.Success)?.data ?: return false
		return alarms.any {
			val isTimeSame = it.time == alarmModel.time
			val isAnyWeekdaySame = it.weekDays.intersect(alarmModel.weekDays).isNotEmpty()
			isAnyWeekdaySame && isTimeSame
		}
	}

	suspend fun checkIfAlarmKindOfExistsExceptThis(alarmModel: AlarmsModel): Boolean {
		val alarms = (repository.getAllAlarms() as? Resource.Success)?.data ?: return false
		// filters of this alarm and check with respect to others
		return alarms.filter { it.id != alarmModel.id }
			.any {
				val isTimeSame = it.time == alarmModel.time
				val isAnyWeekdaySame = it.weekDays.intersect(alarmModel.weekDays).isNotEmpty()
				isAnyWeekdaySame && isTimeSame
			}
	}
}