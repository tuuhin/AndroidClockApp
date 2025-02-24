package com.eva.clockapp.features.alarms.presentation.alarms

import androidx.lifecycle.viewModelScope
import com.eva.clockapp.core.presentation.AppViewModel
import com.eva.clockapp.core.presentation.UiEvents
import com.eva.clockapp.core.utils.Resource
import com.eva.clockapp.features.alarms.domain.models.AlarmsModel
import com.eva.clockapp.features.alarms.domain.repository.AlarmsRepository
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AlarmsViewModel(
	private val repository: AlarmsRepository,
) : AppViewModel() {

	private val _alarms = MutableStateFlow<List<AlarmsModel>>(emptyList())
	private val _isAlarmsLoaded = MutableStateFlow(false)

	val alarms = _alarms.map { it.toImmutableList() }
		.onStart { fillSavedAlarms() }
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(200),
			initialValue = persistentListOf()
		)

	private val _uiEvents = MutableSharedFlow<UiEvents>()

	override val uiEvents: SharedFlow<UiEvents>
		get() = _uiEvents.asSharedFlow()

	fun onEvent(event: AlarmsScreenEvents) {
		when (event) {
			is AlarmsScreenEvents.OnEnableOrDisAbleAlarm ->
				updateEnableAlarm(event.isEnabled, event.alarmsModel)
		}
	}

	private fun fillSavedAlarms() = repository.alarmsFlow.onEach { res ->
		when (res) {
			is Resource.Error -> _uiEvents.emit(UiEvents.ShowSnackBar(res.message ?: "ERROR"))
			Resource.Loading -> _isAlarmsLoaded.update { false }
			is Resource.Success -> _alarms.update { res.data }
		}
		_isAlarmsLoaded.update { true }
	}.launchIn(viewModelScope)

	private fun updateEnableAlarm(isEnabled: Boolean, model: AlarmsModel) {
		viewModelScope.launch {
			when (val result = repository.toggleIsAlarmEnabled(isEnabled, model)) {
				is Resource.Error -> _uiEvents.emit(UiEvents.ShowSnackBar(result.message ?: ""))
				else -> {}
			}
		}
	}
}