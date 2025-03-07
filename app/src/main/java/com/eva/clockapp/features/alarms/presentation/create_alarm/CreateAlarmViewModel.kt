package com.eva.clockapp.features.alarms.presentation.create_alarm

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.eva.clockapp.core.navigation.navgraphs.NavRoutes
import com.eva.clockapp.core.presentation.AppViewModel
import com.eva.clockapp.core.presentation.UiEvents
import com.eva.clockapp.core.utils.Resource
import com.eva.clockapp.features.alarms.domain.controllers.AlarmsSoundPlayer
import com.eva.clockapp.features.alarms.domain.controllers.VibrationController
import com.eva.clockapp.features.alarms.domain.exceptions.FileReadPermissionNotFound
import com.eva.clockapp.features.alarms.domain.models.AssociateAlarmFlags
import com.eva.clockapp.features.alarms.domain.models.RingtoneMusicFile
import com.eva.clockapp.features.alarms.domain.models.VibrationPattern
import com.eva.clockapp.features.alarms.domain.repository.AlarmsRepository
import com.eva.clockapp.features.alarms.domain.use_case.RingtoneProviderUseCase
import com.eva.clockapp.features.alarms.domain.use_case.Ringtones
import com.eva.clockapp.features.alarms.presentation.create_alarm.state.AlarmFlagsChangeEvent
import com.eva.clockapp.features.alarms.presentation.create_alarm.state.CreateAlarmEvents
import com.eva.clockapp.features.alarms.presentation.create_alarm.state.CreateAlarmState
import com.eva.clockapp.features.alarms.presentation.util.toAlarmModel
import com.eva.clockapp.features.alarms.presentation.util.toCreateModel
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime

typealias WeekDays = Set<DayOfWeek>
typealias CategoricalRingtones = ImmutableMap<RingtoneMusicFile.RingtoneType, List<RingtoneMusicFile>>

class CreateAlarmViewModel(
	private val vibrationController: VibrationController,
	private val ringtonesUseCase: RingtoneProviderUseCase,
	private val soundPlayer: AlarmsSoundPlayer,
	private val repository: AlarmsRepository,
	private val savedStateHandle: SavedStateHandle,
) : AppViewModel() {

	private val _selectedDays = MutableStateFlow<WeekDays>(setOf())
	private val _selectedTime = MutableStateFlow(LocalTime(0, 0))
	private val _alarmLabel = MutableStateFlow("")

	private val _selectedSound = MutableStateFlow(ringtonesUseCase.default)
	private val _soundOptions = MutableStateFlow<Ringtones>(emptySet())
	private val _isCreateAlarm = MutableStateFlow(true)

	private val _alarmFlags = MutableStateFlow(AssociateAlarmFlags())
	val flagsState = _alarmFlags.asStateFlow()

	private val _uiEvents = MutableSharedFlow<UiEvents>()
	override val uiEvents: SharedFlow<UiEvents>
		get() = _uiEvents

	private val route: NavRoutes.CreateOrUpdateAlarmRoute
		get() = savedStateHandle.toRoute<NavRoutes.CreateOrUpdateAlarmRoute>()

	val createAlarmState: StateFlow<CreateAlarmState> = combine(
		_selectedDays,
		_selectedTime,
		_alarmLabel,
		_selectedSound,
		_isCreateAlarm
	) { weekDays, localTime, label, sound, isCreate ->
		CreateAlarmState(
			selectedDays = weekDays.toImmutableSet(),
			selectedTime = localTime,
			labelState = label,
			ringtone = sound,
			isAlarmCreate = isCreate
		)
	}.onStart { updateParametersForAlarm() }
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5000),
			initialValue = CreateAlarmState(ringtone = ringtonesUseCase.default)
		)

	val soundOptions: StateFlow<CategoricalRingtones> = _soundOptions
		.map { ringtones -> ringtones.groupBy { it.type }.toImmutableMap() }
		.onStart { loadContentRingtone() }
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.Eagerly,
			initialValue = persistentMapOf()
		)


	fun onEvent(event: CreateAlarmEvents) {
		when (event) {
			is CreateAlarmEvents.OnAlarmTimeSelected -> _selectedTime.update { event.time }
			is CreateAlarmEvents.OnLabelValueChange -> _alarmLabel.update { event.newValue }

			is CreateAlarmEvents.OnAddOrRemoveWeekDay -> _selectedDays.update { days ->
				if (event.dayOfWeek in days)
					days.filterNot { it == event.dayOfWeek }.toSet()
				else days + event.dayOfWeek
			}

			CreateAlarmEvents.LoadDeviceRingtoneFiles -> loadContentRingtone()
			CreateAlarmEvents.OnExitAlarmSoundScreen -> soundPlayer.stopSound()
			CreateAlarmEvents.OnSaveAlarm -> onCreateNewAlarm()
			CreateAlarmEvents.OnUpdateAlarm -> onUpdateAlarm()
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

	private fun loadContentRingtone() = ringtonesUseCase.invoke()
		.onEach { result ->
			result.fold(
				onSuccess = { files -> _soundOptions.update { files } },
				onFailure = { err ->
					if (err is FileReadPermissionNotFound) return@onEach
					_uiEvents.emit(
						UiEvents.ShowSnackBar(message = err.localizedMessage ?: "FAILED")
					)
				},
			)
		}.launchIn(viewModelScope)


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

	private fun onCreateNewAlarm() {
		val model = createAlarmState.value.toCreateModel(flagsState.value)
		// TODO: Add a validator
		viewModelScope.launch {
			when (val result = repository.createAlarm(model)) {
				is Resource.Error -> {
					val message = result.message ?: result.message
					message?.let {
						_uiEvents.emit(UiEvents.ShowSnackBar(message))
					}
				}

				is Resource.Success -> _uiEvents.emit(UiEvents.NavigateBack)
				else -> {}
			}
		}
	}

	private fun onUpdateAlarm() {
		val alarmId = route.alarmId ?: return
		val updateModel = createAlarmState.value.toAlarmModel(alarmId = alarmId, flagsState.value)
		// TODO: Add a validator
		viewModelScope.launch {
			when (val result = repository.updateAlarm(updateModel)) {
				is Resource.Error -> {
					val message = result.message ?: result.message
					message?.let {
						_uiEvents.emit(UiEvents.ShowSnackBar(message))
					}
				}

				is Resource.Success -> _uiEvents.emit(UiEvents.NavigateBack)
				else -> {}
			}
		}
	}

	private fun updateParametersForAlarm() {
		val alarmId = route.alarmId ?: return
		_isCreateAlarm.update { false }

		// Update the alarm parameters
		viewModelScope.launch {
			val resource = repository.getAlarmFromId(alarmId)
			when (resource) {
				is Resource.Success -> {
					val alarm = resource.data

					_selectedTime.update { alarm.time }
					_selectedDays.update { alarm.weekDays }
					_alarmFlags.update { alarm.flags }
					_alarmLabel.update { alarm.label ?: "" }

					val sound = _soundOptions.value.find { it.uri == alarm.soundUri }
						?: ringtonesUseCase.default
					_selectedSound.update { sound }
				}

				is Resource.Error -> {
					val message = resource.message
					val err = resource.error
					_uiEvents.emit(UiEvents.ShowToast(message ?: err.message ?: ""))
					_uiEvents.emit(UiEvents.NavigateBack)
				}

				else -> {}
			}
		}
	}
}