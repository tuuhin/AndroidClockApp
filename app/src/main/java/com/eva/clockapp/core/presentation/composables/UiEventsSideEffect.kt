package com.eva.clockapp.core.presentation.composables

import android.widget.Toast
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.eva.clockapp.core.presentation.LocalSnackBarHostState
import com.eva.clockapp.core.presentation.UiEvents
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.merge

@Composable
fun UIEventsSideEffect(vararg eventFlow: Flow<UiEvents>, onBack: () -> Unit = {}) {

	val context = LocalContext.current
	val lifecyleOwner = LocalLifecycleOwner.current
	val snackBarState = LocalSnackBarHostState.current

	LaunchedEffect(key1 = lifecyleOwner) {
		lifecyleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
			merge(*eventFlow).collect { event ->
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