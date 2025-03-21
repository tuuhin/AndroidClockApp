package com.eva.clockapp.features.alarms.presentation.create_alarm

import androidx.lifecycle.viewModelScope
import com.eva.clockapp.core.presentation.AppViewModel
import com.eva.clockapp.core.presentation.UiEvents
import com.eva.clockapp.features.alarms.data.repository.Ringtones
import com.eva.clockapp.features.alarms.domain.controllers.AlarmsSoundPlayer
import com.eva.clockapp.features.alarms.domain.exceptions.FileReadPermissionNotFound
import com.eva.clockapp.features.alarms.domain.models.AssociateAlarmFlags
import com.eva.clockapp.features.alarms.domain.models.RingtoneMusicFile
import com.eva.clockapp.features.alarms.domain.repository.RingtonesRepository
import com.eva.clockapp.features.alarms.presentation.create_alarm.state.AlarmSoundScreenEvent
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

typealias CategoricalRingtones =
		ImmutableMap<RingtoneMusicFile.RingtoneType, ImmutableList<RingtoneMusicFile>>

class AlarmsSoundsViewmodel(
	private val repository: RingtonesRepository,
	private val soundPlayer: AlarmsSoundPlayer,
) : AppViewModel() {

	private val _soundOptions = MutableStateFlow<Ringtones>(emptySet())
	val soundOptions: StateFlow<CategoricalRingtones> = _soundOptions
		.map { ringtones -> ringtones.toCategoricalRingtones() }
		.onStart { loadRingtones() }
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.Eagerly,
			initialValue = persistentMapOf()
		)

	private val _uiEvents = MutableSharedFlow<UiEvents>()
	override val uiEvents: SharedFlow<UiEvents>
		get() = _uiEvents

	private var _loadingJob: Job? = null

	private fun loadRingtones() {
		_loadingJob?.cancel()
		_loadingJob = repository.allRingtones
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
	}

	fun onEvent(event: AlarmSoundScreenEvent) {
		when (event) {
			is AlarmSoundScreenEvent.OnMusicReadPermissionChange -> onPermissionChange(event.permission)
			is AlarmSoundScreenEvent.OnMusicVolumeChange -> onSoundVolumeChange(event.volume)
			is AlarmSoundScreenEvent.OnSelectRingtone -> onSelectSound(event.ringtone, event.volume)
			is AlarmSoundScreenEvent.OnSoundEnableChange -> onSoundEnabled(event.isEnabled)
		}
	}

	private fun onPermissionChange(isAllowed: Boolean) {
		if (isAllowed) loadRingtones()
	}

	private fun onSelectSound(ringtone: RingtoneMusicFile, volume: Float) {
		val resource = soundPlayer.playSound(ringtone.uri, volume)
		// show toast on failure
		resource.fold(
			onError = { err, message ->
				val event = (message ?: err.message)
					?.let(UiEvents::ShowSnackBar) ?: return@fold

				viewModelScope.launch {
					_uiEvents.emit(event)
				}
			},
		)
	}

	private fun onSoundVolumeChange(volume: Float) {
		val alarmVolume = if (volume <= AssociateAlarmFlags.MIN_ALARM_SOUND)
			AssociateAlarmFlags.MIN_ALARM_SOUND
		else volume
		// change the volume
		soundPlayer.changeVolume(alarmVolume)
	}

	private fun onSoundEnabled(isEnabled: Boolean) {
		// turn off the player
		if (!isEnabled) soundPlayer.stopSound()
	}

	override fun onCleared() {
		// stop sound when the viewmodel is cleared
		soundPlayer.stopSound()
		super.onCleared()
	}
}

private fun Iterable<RingtoneMusicFile>.toCategoricalRingtones() = groupBy { it.type }
	.map { (key, ringtones) -> key to ringtones.toImmutableList() }
	.toMap()
	.toImmutableMap()
