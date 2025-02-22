package com.eva.clockapp.features.alarms.presentation.alarms

import androidx.lifecycle.viewModelScope
import com.eva.clockapp.core.presentation.AppViewModel
import com.eva.clockapp.core.presentation.UiEvents
import com.eva.clockapp.features.alarms.domain.models.AlarmsModel
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class AlarmsViewModel : AppViewModel() {

	private val _alarms = MutableStateFlow<List<AlarmsModel>>(emptyList())

	val alarms = _alarms.map { it.toImmutableList() }.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(200),
		initialValue = persistentListOf()
	)

	private val _uiEvents = MutableSharedFlow<UiEvents>()

	override val uiEvents: SharedFlow<UiEvents>
		get() = _uiEvents.asSharedFlow()

	fun onEvent(event: AlarmsScreenEvents) {

	}
}