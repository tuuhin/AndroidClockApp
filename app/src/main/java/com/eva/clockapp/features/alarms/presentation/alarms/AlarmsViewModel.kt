package com.eva.clockapp.features.alarms.presentation.alarms

import androidx.lifecycle.viewModelScope
import com.eva.clockapp.core.presentation.AppViewModel
import com.eva.clockapp.core.presentation.UiEvents
import com.eva.clockapp.core.utils.Resource
import com.eva.clockapp.features.alarms.domain.models.AlarmsModel
import com.eva.clockapp.features.alarms.domain.repository.AlarmsRepository
import com.eva.clockapp.features.alarms.presentation.alarms.state.SelectableAlarmModel
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

	private val _alarms = MutableStateFlow<List<SelectableAlarmModel>>(emptyList())
	private val _isAlarmsLoaded = MutableStateFlow(false)

	val selectableAlarms = _alarms.map { it.toImmutableList() }
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
			is AlarmsScreenEvents.OnEnableOrDisAbleAlarm -> enableAlarms(event.enabled, event.model)
			is AlarmsScreenEvents.ToggleAlarmSelection -> updateAlarmSelection(event.alarm)
			AlarmsScreenEvents.DeleteSelectedAlarms -> deleteSelectedAlarms()
			AlarmsScreenEvents.DeSelectAllAlarms -> deSelectAllItems()
		}
	}

	private fun fillSavedAlarms() = repository.alarmsFlow.onEach { res ->
		when (res) {
			is Resource.Error -> res.message?.let { message ->
				_uiEvents.emit(UiEvents.ShowSnackBar(message))
			}

			Resource.Loading -> _isAlarmsLoaded.update { false }
			is Resource.Success -> updateAlarmsOnLoad(res.data)
		}
		_isAlarmsLoaded.update { true }
	}.launchIn(viewModelScope)


	private fun enableAlarms(isEnabled: Boolean, model: AlarmsModel) {
		viewModelScope.launch {
			when (val result = repository.toggleIsAlarmEnabled(isEnabled, model)) {
				is Resource.Error -> result.message?.let { message ->
					_uiEvents.emit(UiEvents.ShowSnackBar(message))
				}

				else -> {}
			}
		}
	}


	private fun updateAlarmsOnLoad(alarms: List<AlarmsModel>) = _alarms.update { oldAlarms ->
		alarms.map { model ->
			val isSelected = oldAlarms.find { it.model.id == model.id }?.isSelected ?: false
			SelectableAlarmModel(model = model, isSelected = isSelected)
		}
	}


	private fun updateAlarmSelection(alarm: AlarmsModel) = _alarms.update { oldAlarms ->
		oldAlarms.map { selectableModel ->
			if (selectableModel.model.id == alarm.id)
				selectableModel.copy(isSelected = !selectableModel.isSelected)
			else selectableModel
		}
	}

	private fun deSelectAllItems() = _alarms.update { oldAlarms ->
		oldAlarms.map { selectableModel -> selectableModel.copy(isSelected = false) }
	}

	private fun deleteSelectedAlarms() = viewModelScope.launch {

		val selectedAlarms = _alarms.value.filter { it.isSelected }.map { it.model }

		when (val result = repository.deleteAlarms(selectedAlarms)) {
			is Resource.Error -> result.message?.let { message ->
				_uiEvents.emit(UiEvents.ShowSnackBar(message))
			}

			else -> {}
		}
	}

}