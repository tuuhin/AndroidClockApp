package com.eva.clockapp.features.alarms.presentation.create_alarm

import android.util.Log
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
import com.eva.clockapp.features.alarms.domain.models.WeekDays
import com.eva.clockapp.features.alarms.domain.repository.AlarmsRepository
import com.eva.clockapp.features.alarms.domain.use_case.RingtoneProviderUseCase
import com.eva.clockapp.features.alarms.domain.use_case.Ringtones
import com.eva.clockapp.features.alarms.domain.use_case.ValidateAlarmUseCase
import com.eva.clockapp.features.alarms.domain.utils.AlarmUtils
import com.eva.clockapp.features.alarms.presentation.create_alarm.state.AlarmFlagsChangeEvent
import com.eva.clockapp.features.alarms.presentation.create_alarm.state.CreateAlarmEvents
import com.eva.clockapp.features.alarms.presentation.create_alarm.state.CreateAlarmState
import com.eva.clockapp.features.alarms.presentation.create_alarm.state.DateTimePickerState
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
import kotlinx.datetime.LocalTime

typealias CategoricalRingtones = ImmutableMap<RingtoneMusicFile.RingtoneType, List<RingtoneMusicFile>>

class CreateAlarmViewModel(
	private val vibrationController: VibrationController,
	private val ringtonesUseCase: RingtoneProviderUseCase,
	private val soundPlayer: AlarmsSoundPlayer,
	private val repository: AlarmsRepository,
	private val validator: ValidateAlarmUseCase,
	private val savedStateHandle: SavedStateHandle,
) : AppViewModel() {

	// core alarm state
	private val _selectedDays = MutableStateFlow<WeekDays>(setOf())
	private val _alarmTime = MutableStateFlow(LocalTime(0, 0))
	private val _startTime = MutableStateFlow(LocalTime(0, 0))

	// additional states
	private val _alarmLabel = MutableStateFlow("")
	private val _selectedSound = MutableStateFlow(ringtonesUseCase.default)
	private val _soundOptions = MutableStateFlow<Ringtones>(emptySet())
	private val _background = MutableStateFlow<String?>(null)

	// alarms flags
	private val _alarmFlags = MutableStateFlow(AssociateAlarmFlags())
	val flagsState = _alarmFlags.asStateFlow()

	private val _uiEvents = MutableSharedFlow<UiEvents>()
	override val uiEvents: SharedFlow<UiEvents>
		get() = _uiEvents

	private val route: NavRoutes.CreateOrUpdateAlarmRoute
		get() = savedStateHandle.toRoute<NavRoutes.CreateOrUpdateAlarmRoute>()

	private val _pickerState =
		combine(_selectedDays, _startTime, _alarmTime) { weekDays, startTime, selectedTime ->
			DateTimePickerState(
				weekDays = weekDays,
				startTime = startTime,
				selectedTime = selectedTime
			)
		}.stateIn(
			scope = viewModelScope,
			started = SharingStarted.Eagerly,
			initialValue = DateTimePickerState()
		)

	val createAlarmState: StateFlow<CreateAlarmState> = combine(
		_pickerState, _alarmLabel, _selectedSound, _background
	) { pickerState, label, sound, background ->
		CreateAlarmState(
			selectedDays = pickerState.weekDays.toImmutableSet(),
			startTime = pickerState.startTime,
			selectedTime = pickerState.selectedTime,
			labelState = label,
			ringtone = sound,
			backgroundImageUri = background,
			isAlarmCreate = route.alarmId == null,
		)
	}.onStart { updateParametersForAlarm() }
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5000),
			initialValue = CreateAlarmState(
				ringtone = ringtonesUseCase.default,
				isAlarmCreate = route.alarmId == null
			)
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
			is CreateAlarmEvents.OnAlarmTimeChange -> {
				Log.d("ALARMS", "${event.time}")
				_alarmTime.update { event.time }
			}

			is CreateAlarmEvents.OnLabelValueChange -> _alarmLabel.update { event.newValue }
			is CreateAlarmEvents.OnSelectUriForBackground -> _background.update { event.background }
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
		val alarmVolume = if (flags.alarmVolume <= AssociateAlarmFlags.MIN_ALARM_SOUND)
			AssociateAlarmFlags.MIN_ALARM_SOUND
		else flags.alarmVolume
		// change the volume
		soundPlayer.changeVolume(alarmVolume)
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
		viewModelScope.launch {
			val model = createAlarmState.value.toCreateModel(
				flags = flagsState.value,
			)

			val result = validator.validateCreateAlarm(model)
			if (!result.isValid && result.message != null) {
				_uiEvents.emit(UiEvents.ShowSnackBar(result.message))
				return@launch
			}

			// TODO: Add a validator
			when (val result = repository.createAlarm(model)) {
				is Resource.Error -> (result.message ?: result.message)?.let { message ->
					_uiEvents.emit(UiEvents.ShowSnackBar(message))
				}

				is Resource.Success -> _uiEvents.emit(UiEvents.NavigateBack)
				else -> {}
			}
		}
	}

	private fun onUpdateAlarm() {
		val alarmId = route.alarmId ?: return
		viewModelScope.launch {
			val updateModel = createAlarmState.value.toAlarmModel(
				alarmId = alarmId,
				flags = flagsState.value,
			)

			val result = validator.validateUpdate(updateModel)
			if (!result.isValid && result.message != null) {
				_uiEvents.emit(UiEvents.ShowSnackBar(result.message))
				return@launch
			}

			when (val result = repository.updateAlarm(updateModel)) {
				is Resource.Error -> (result.message ?: result.message)?.let { message ->
					_uiEvents.emit(UiEvents.ShowSnackBar(message))
				}

				is Resource.Success -> {
					result.message?.let { message ->
						_uiEvents.emit(UiEvents.ShowToast(message))
					}
					_uiEvents.emit(UiEvents.NavigateBack)
				}

				else -> {}
			}
		}
	}

	private fun updateParametersForAlarm() = viewModelScope.launch {

		val alarmId = route.alarmId ?: run {
			updateTimeOnStart(time = AlarmUtils.calculateNextAlarmTime())
			return@launch
		}

		val resource = repository.getAlarmFromId(alarmId)
		resource.fold(
			onSuccess = { alarm ->
				updateTimeOnStart(alarm.time)
				// Update the alarm parameters

				_selectedDays.update { alarm.weekDays }
				_alarmFlags.update { alarm.flags }
				_alarmLabel.update { alarm.label ?: "" }
				_background.update { alarm.background }

				val sound = _soundOptions.value.find { it.uri == alarm.soundUri }
					?: ringtonesUseCase.default
				_selectedSound.update { sound }
			},
			onError = { err, message ->
				(message ?: err.message)?.let { message ->
					_uiEvents.emit(UiEvents.ShowSnackBar(message))
					_uiEvents.emit(UiEvents.NavigateBack)
				}
			},
		)
	}

	private fun updateTimeOnStart(time: LocalTime) {
		_startTime.update { time }
		_alarmTime.update { time }
	}
}