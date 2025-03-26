package com.eva.clockapp.features.alarms.presentation.alarms.state

sealed interface ContentState {
	data object Loading : ContentState
	data object Empty : ContentState
	data object Content : ContentState
}