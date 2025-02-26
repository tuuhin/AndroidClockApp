package com.eva.clockapp.core.presentation

import android.widget.Toast
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow

@Composable
fun UIEventsSideEffect(eventsFlow: Flow<UiEvents>, onBack: () -> Unit = {}) {

	val context = LocalContext.current
	val lifecyleOwner = LocalLifecycleOwner.current
	val snackBarState = LocalSnackBarHostState.current

	LaunchedEffect(key1 = lifecyleOwner) {
		lifecyleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
			eventsFlow.collect { event ->
				when (event) {
					is UiEvents.ShowSnackBar -> snackBarState.showSnackbar(
						message = event.message,
						duration = SnackbarDuration.Short
					)

					is UiEvents.ShowToast -> Toast.makeText(
						context,
						event.message,
						Toast.LENGTH_SHORT
					)
						.show()

					UiEvents.NavigateBack -> onBack()
				}
			}
		}
	}
}