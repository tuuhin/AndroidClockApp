package com.eva.clockapp.features.alarms.presentation.create_alarm

import androidx.lifecycle.viewModelScope
import com.eva.clockapp.core.presentation.AppViewModel
import com.eva.clockapp.core.presentation.UiEvents
import com.eva.clockapp.features.alarms.domain.controllers.WallpaperOptions
import com.eva.clockapp.features.alarms.domain.controllers.WallpaperProvider
import com.eva.clockapp.features.alarms.presentation.create_alarm.state.AlarmsBackgroundState
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class AlarmsBackgroundViewModel(
	private val provider: WallpaperProvider,
) : AppViewModel() {

	private val _wallpaperUris = MutableStateFlow<WallpaperOptions>(emptyList())
	private val _isLoaded = MutableStateFlow(false)

	private val _uiEvents = MutableSharedFlow<UiEvents>()
	override val uiEvents: SharedFlow<UiEvents>
		get() = _uiEvents

	val screenState = combine(_isLoaded, _wallpaperUris) { isLoaded, wallpapers ->
		AlarmsBackgroundState(
			isLoaded = isLoaded,
			options = wallpapers.toImmutableList()
		)
	}.onStart { loadWallpapers() }
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5000),
			initialValue = AlarmsBackgroundState()
		)

	private fun loadWallpapers() = provider.loadWallpapers()
		.onEach { res ->
			res.fold(
				onSuccess = { uris ->
					_wallpaperUris.update { uris }
					_isLoaded.update { true }
				},
				onError = { err, message ->
					(message ?: err.message)?.let { message ->
						_uiEvents.emit(UiEvents.ShowSnackBar(message))
					}
					_isLoaded.update { true }
				},
				onLoading = { _isLoaded.update { false } }
			)

		}.launchIn(viewModelScope)

}