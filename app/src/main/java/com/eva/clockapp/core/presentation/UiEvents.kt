package com.eva.clockapp.core.presentation

import androidx.compose.runtime.Stable

@Stable
sealed interface UiEvents {

	data class ShowSnackBar(val message: String) : UiEvents

	data class ShowToast(val message: String) : UiEvents

	data object NavigateBack : UiEvents
}