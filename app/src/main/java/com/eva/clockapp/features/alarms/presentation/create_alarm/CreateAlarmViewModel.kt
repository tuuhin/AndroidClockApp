package com.eva.clockapp.features.alarms.presentation.create_alarm

import androidx.lifecycle.viewModelScope
import com.eva.clockapp.core.presentation.AppViewModel
import com.eva.clockapp.core.presentation.UiEvents
import com.eva.clockapp.core.utils.Resource
import com.eva.clockapp.features.alarms.domain.controllers.AlarmsSoundPlayer
import com.eva.clockapp.features.alarms.domain.controllers.AppRingtoneProvider
import com.eva.clockapp.features.alarms.domain.controllers.ContentRingtoneProvider
import com.eva.clockapp.features.alarms.domain.controllers.VibrationController
import com.eva.clockapp.features.alarms.domain.exceptions.FileReadPermissionNotFound
import com.eva.clockapp.features.alarms.domain.models.AssociateAlarmFlags
import com.eva.clockapp.features.alarms.domain.models.CreateAlarmModel
import com.eva.clockapp.features.alarms.domain.models.RingtoneMusicFile
import com.eva.clockapp.features.alarms.domain.models.VibrationPattern
import com.eva.clockapp.features.alarms.domain.repository.AlarmsRepository
import com.eva.clockapp.features.alarms.presentation.create_alarm.state.AlarmFlagsChangeEvent
import com.eva.clockapp.features.alarms.presentation.create_alarm.state.AlarmSoundOptions
import com.eva.clockapp.features.alarms.presentation.create_alarm.state.CreateAlarmEvents
import com.eva.clockapp.features.alarms.presentation.create_alarm.state.CreateAlarmState
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
	private val soundPlayer: AlarmsSoundPlayer,
	private val repository: AlarmsRepository,
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
				if (event.dayOfWeek in days)
					days.filterNot { it == event.dayOfWeek }.toSet()
				else days + event.dayOfWeek
			}

			CreateAlarmEvents.OnSaveAlarm -> onSaveAlarm()
			CreateAlarmEvents.LoadDeviceRingtoneFiles -> loadContentRingtone()
			CreateAlarmEvents.OnExitAlarmSoundScreen -> soundPlayer.stopSound()
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

			is AlarmFlagsChangeEvent.OnSoundOptionEnabled -> onSoundEnabled(event.isEnabled)
			is AlarmFlagsChangeEvent.OnSoundSelected -> onSelectSound(event.sound)
			is AlarmFlagsChangeEvent.OnSoundVolumeChange -> onSoundVolumeChange(event.volume)
			is AlarmFlagsChangeEvent.OnVibrationEnabled -> onVibrationPatternEnabled(event.isEnabled)
			is AlarmFlagsChangeEvent.OnVibrationPatternSelected -> onSelectVibration(event.pattern)
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

	private fun onSelectSound(ringtone: RingtoneMusicFile) {
		val newSound = _selectedSound.updateAndGet { ringtone }
		val volume = _alarmFlags.value.alarmVolume
		val result = soundPlayer.playSound(newSound.uri, volume)
		// show toast on failure
		result.onFailure {
			viewModelScope.launch {
				_uiEvents.emit(UiEvents.ShowToast(it.message ?: "Some error"))
			}
		}
	}

	private fun onSoundVolumeChange(volume: Float) {
		val flags = _alarmFlags.updateAndGet { state -> state.copy(alarmVolume = volume) }
		// change the volume
		soundPlayer.changeVolume(flags.alarmVolume)
	}

	private fun onSoundEnabled(isEnabled: Boolean) {
		val flags = _alarmFlags.updateAndGet { state -> state.copy(isSoundEnabled = isEnabled) }
		// turn off the player
		if (!flags.isSoundEnabled) soundPlayer.stopSound()
	}

	private fun onSelectVibration(pattern: VibrationPattern) {
		val flags = _alarmFlags.updateAndGet { state -> state.copy(vibrationPattern = pattern) }
		// make a vibration pattern
		vibrationController.startVibration(flags.vibrationPattern)
	}

	private fun onVibrationPatternEnabled(isEnabled: Boolean) {
		val flags = _alarmFlags.updateAndGet { state -> state.copy(isVibrationEnabled = isEnabled) }
		// if it's not enabled stop the ongoing  vibration
		if (!flags.isVibrationEnabled) vibrationController.stopVibration()
	}

	private fun onSaveAlarm() {

		val model = CreateAlarmModel(
			time = _selectedTime.value,
			weekDays = _selectedDays.value,
			flags = _alarmFlags.value,
			label = _alarmLabel.value,
			ringtone = _selectedSound.value
		)

		viewModelScope.launch {
			when (val result = repository.createAlarm(model)) {
				is Resource.Error -> {
					val event = UiEvents.ShowSnackBar(result.message ?: "")
					_uiEvents.emit(event)
				}

				is Resource.Success -> _uiEvents.emit(UiEvents.NavigateBack)
				else -> {}
			}
		}
	}

}