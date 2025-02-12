package com.eva.clockapp.features.alarms.presentation.create_alarm

import androidx.lifecycle.viewModelScope
import com.eva.clockapp.core.presentation.AppViewModel
import com.eva.clockapp.core.presentation.UiEvents
import com.eva.clockapp.features.alarms.domain.controllers.AppRingtoneProvider
import com.eva.clockapp.features.alarms.domain.controllers.ContentRingtoneProvider
import com.eva.clockapp.features.alarms.domain.controllers.VibrationController
import com.eva.clockapp.features.alarms.domain.exceptions.FileReadPermissionNotFound
import com.eva.clockapp.features.alarms.domain.models.AssociateAlarmFlags
import com.eva.clockapp.features.alarms.domain.models.RingtoneMusicFile
import com.eva.clockapp.features.alarms.presentation.create_alarm.state.AlarmSoundOptions
import com.eva.clockapp.features.alarms.presentation.create_alarm.state.CreateAlarmEvents
import com.eva.clockapp.features.alarms.presentation.create_alarm.state.CreateAlarmState
import com.eva.clockapp.features.alarms.presentation.create_alarm.state.AlarmFlagsChangeEvent
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime

private typealias Ringtones = List<RingtoneMusicFile>
private typealias WeekDays = Set<DayOfWeek>

class CreateAlarmViewModel(
	private val vibrationController: VibrationController,
	private val ringtoneProvider: ContentRingtoneProvider,
	private val localRingtoneProvider: AppRingtoneProvider,
) : AppViewModel() {

	private val _selectedDays = MutableStateFlow<WeekDays>(emptySet())
	private val _selectedTime = MutableStateFlow(LocalTime(0, 0))
	private val _alarmLabel = MutableStateFlow("")

	private val _selectedSound = MutableStateFlow(localRingtoneProvider.default)
	private val _deviceOptions = MutableStateFlow<Ringtones>(emptyList())
	private val _localOptions = MutableStateFlow<Ringtones>(emptyList())

	val soundOptions =
		combine(_deviceOptions, _localOptions, _selectedSound) { external, local, selected ->
			AlarmSoundOptions(
				external = external.toImmutableList(),
				local = local.toImmutableList(),
				selectedSound = selected
			)
		}.onStart {
			// load app ringtones
			loadAppRingtone()
			// load content ringtones
			loadContentRingtone()
		}.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5000),
			initialValue = AlarmSoundOptions(localRingtoneProvider.default)
		)

	val createAlarmState: StateFlow<CreateAlarmState>
		get() = combine(_selectedDays, _selectedTime, _alarmLabel) { weekDays, localTime, label ->
			CreateAlarmState(
				selectedDays = weekDays.toImmutableSet(),
				selectedTime = localTime,
				labelState = label,
			)
		}.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5000),
			initialValue = CreateAlarmState()
		)

	private val _alarmFlags = MutableStateFlow(AssociateAlarmFlags())
	val flagsState: StateFlow<AssociateAlarmFlags>
		get() = _alarmFlags

	private val _uiEvents = MutableSharedFlow<UiEvents>()
	override val uiEvents: SharedFlow<UiEvents>
		get() = _uiEvents


	fun onEvent(event: CreateAlarmEvents) {
		when (event) {
			is CreateAlarmEvents.OnAlarmTimeSelected -> _selectedTime.update { event.time }
			is CreateAlarmEvents.OnLabelValueChange -> _alarmLabel.update { event.newValue }

			is CreateAlarmEvents.OnAddOrRemoveWeekDay -> _selectedDays.update { days ->
				if (event.dayOfWeek in days) days.filterNot { it == event.dayOfWeek }.toSet()
				else days + event.dayOfWeek
			}
			CreateAlarmEvents.OnSaveAlarm -> onSaveAlarm()
			CreateAlarmEvents.LoadDeviceRingtoneFiles -> loadContentRingtone()
		}
	}

	fun onFlagsEvent(event: AlarmFlagsChangeEvent) {
		when (event) {
			is AlarmFlagsChangeEvent.OnIncreaseVolumeByStep -> _alarmFlags.update { state ->
				state.copy(isVolumeStepIncrease = event.isEnabled)
			}

			is AlarmFlagsChangeEvent.OnSnoozeEnabled -> _alarmFlags.update { state ->
				state.copy(isSnoozeEnabled = event.isEnabled)
			}

			is AlarmFlagsChangeEvent.OnSnoozeIntervalChange -> _alarmFlags.update { state ->
				state.copy(snoozeInterval = event.interval)
			}

			is AlarmFlagsChangeEvent.OnSnoozeRepeatModeChange -> _alarmFlags.update { state ->
				state.copy(snoozeRepeatMode = event.mode)
			}

			is AlarmFlagsChangeEvent.OnSoundOptionEnabled -> _alarmFlags.update { state ->
				state.copy(isSoundEnabled = event.isEnabled)
			}

			is AlarmFlagsChangeEvent.OnSoundSelected -> _selectedSound.update { event.sound }
			is AlarmFlagsChangeEvent.OnSoundVolumeChange -> _alarmFlags.update { state ->
				state.copy(alarmVolume = event.volume)
			}

			is AlarmFlagsChangeEvent.OnVibrationEnabled -> {
				val flags = _alarmFlags.updateAndGet { state ->
					state.copy(isVibrationEnabled = event.isEnabled)
				}
				// if it's not enabled stop the ongoing  vibration
				if (!flags.isVibrationEnabled) vibrationController.stopVibration()
			}

			is AlarmFlagsChangeEvent.OnVibrationPatternSelected -> {
				val flags = _alarmFlags.updateAndGet { state ->
					state.copy(vibrationPattern = event.pattern)
				}
				// make a vibration pattern
				vibrationController.startVibration(flags.vibrationPattern)
			}
		}
	}

	private fun loadContentRingtone() = ringtoneProvider.loadRingtonesAsFlow
		.onEach { result ->
			result.fold(
				onSuccess = { files -> _deviceOptions.update { files } },
				onFailure = { err ->
					if (err is FileReadPermissionNotFound) return@onEach
					_uiEvents.emit(
						UiEvents.ShowSnackBar(message = err.localizedMessage ?: "FAILED")
					)
				},
			)
		}.launchIn(viewModelScope)


	private fun loadAppRingtone() = viewModelScope.launch {
		localRingtoneProvider.ringtones.fold(
			onSuccess = { tones -> _localOptions.update { tones } },
			onFailure = { err ->
				_uiEvents.emit(
					UiEvents.ShowSnackBar(message = err.localizedMessage ?: "FAILED")
				)
			},
		)
	}

	private fun onSaveAlarm() {

	}
}