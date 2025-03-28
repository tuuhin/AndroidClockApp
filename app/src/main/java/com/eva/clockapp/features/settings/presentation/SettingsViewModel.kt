package com.eva.clockapp.features.settings.presentation

import androidx.lifecycle.viewModelScope
import com.eva.clockapp.core.presentation.AppViewModel
import com.eva.clockapp.core.presentation.UiEvents
import com.eva.clockapp.features.settings.domain.models.AlarmSettingsModel
import com.eva.clockapp.features.settings.domain.repository.AlarmSettingsRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: AlarmSettingsRepository) : AppViewModel() {

	val alarmSettings = repository.settingsFlow.stateIn(
		scope = viewModelScope,
		started = SharingStarted.Eagerly,
		initialValue = AlarmSettingsModel()
	)

	private val _uiEvents = MutableSharedFlow<UiEvents>()
	override val uiEvents: SharedFlow<UiEvents>
		get() = _uiEvents

	fun onEvent(event: ChangeAlarmSettingsEvent) = viewModelScope.launch {
		when (event) {
			is ChangeAlarmSettingsEvent.OnStartOfWeekChange -> repository.onStartOfWeekChange(event.startOfWeek)
			is ChangeAlarmSettingsEvent.OnTimeFormatChange -> repository.onTimeFormatChange(event.format)
			is ChangeAlarmSettingsEvent.OnUpcomingNotificationTimeChange ->
				repository.onUpcomingNotificationTimeChange(event.time)

			is ChangeAlarmSettingsEvent.OnVolumeControlChange ->
				repository.onVolumeControlChange(event.control)
		}
	}
}