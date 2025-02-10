package com.eva.clockapp.features.alarms.presentation.create_alarm

import androidx.lifecycle.viewModelScope
import com.eva.clockapp.core.presentation.AppViewModel
import com.eva.clockapp.core.presentation.UiEvents
import com.eva.clockapp.features.alarms.domain.controllers.VibrationController
import com.eva.clockapp.features.alarms.domain.models.AssociateAlarmFlags
import com.eva.clockapp.features.alarms.presentation.create_alarm.state.CreateAlarmEvents
import com.eva.clockapp.features.alarms.presentation.create_alarm.state.CreateAlarmState
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime

class CreateAlarmViewModel(
	private val vibrationController: VibrationController,
) : AppViewModel() {

	private val _selectedDays = MutableStateFlow<Set<DayOfWeek>>(emptySet())
	private val _selectedTime = MutableStateFlow(LocalTime(0, 0))
	private val _alarmFlags = MutableStateFlow(AssociateAlarmFlags())
	private val _alarmLabel = MutableStateFlow("")

	val newAlarmState: StateFlow<CreateAlarmState>
		get() = combine(
			_selectedDays,
			_selectedTime,
			_alarmLabel,
			_alarmFlags
		) { weekDays, localTime, label, flags ->
			CreateAlarmState(
				selectedDays = weekDays.toImmutableSet(),
				selectedTime = localTime,
				labelState = label,
				flags = flags
			)
		}.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5000),
			initialValue = CreateAlarmState()
		)

	val flagsState: StateFlow<AssociateAlarmFlags>
		get() = _alarmFlags

	private val _uiEvents = MutableSharedFlow<UiEvents>()
	override val uiEvents: SharedFlow<UiEvents>
		get() = _uiEvents.asSharedFlow()


	fun onEvent(event: CreateAlarmEvents) {
		when (event) {
			is CreateAlarmEvents.OnAlarmTimeSelected -> _selectedTime.update { event.time }
			is CreateAlarmEvents.OnLabelValueChange -> _alarmLabel.update { event.newValue }

			is CreateAlarmEvents.OnAddOrRemoveWeekDay -> _selectedDays.update { days ->
				if (event.dayOfWeek in days) days.filterNot { it == event.dayOfWeek }.toSet()
				else days + event.dayOfWeek
			}

			is CreateAlarmEvents.OnSnoozeEnabled ->
				_alarmFlags.update { state -> state.copy(isSnoozeEnabled = event.isEnabled) }

			is CreateAlarmEvents.OnSnoozeIntervalChange ->
				_alarmFlags.update { state -> state.copy(snoozeInterval = event.intervalOptions) }

			is CreateAlarmEvents.OnSnoozeRepeatModeChange ->
				_alarmFlags.update { state -> state.copy(snoozeRepeatMode = event.mode) }

			is CreateAlarmEvents.OnVibrationEnabled -> {
				val flags = _alarmFlags.updateAndGet { state ->
					state.copy(isVibrationEnabled = event.isEnabled)
				}
				// if it's not enabled stop the vibration
				if (!flags.isVibrationEnabled) vibrationController.stopVibration()
			}

			is CreateAlarmEvents.OnVibrationPatternSelected -> {
				val flags = _alarmFlags.updateAndGet { state ->
					state.copy(vibrationPattern = event.pattern)
				}
				// make a vibration pattern
				vibrationController.startVibration(flags.vibrationPattern)
			}

			CreateAlarmEvents.OnSaveAlarm -> onSaveAlarm()
		}
	}


	private fun onSaveAlarm() {

	}
}