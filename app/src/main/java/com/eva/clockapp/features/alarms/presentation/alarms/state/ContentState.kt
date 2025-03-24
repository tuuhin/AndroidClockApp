package com.eva.clockapp.features.alarms.presentation.alarms.state

sealed interface ContentState<out T> {
	data object Loading : ContentState<Nothing>
	data object Empty : ContentState<Nothing>
	data class Content<T>(val data: T) : ContentState<T>
}