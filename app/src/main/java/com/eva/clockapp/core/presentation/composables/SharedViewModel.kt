package com.eva.clockapp.core.presentation.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.eva.clockapp.core.presentation.AppViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
inline fun <reified T : AppViewModel> NavBackStackEntry.sharedViewModel(controller:NavController): T {
	// if there is no parent then return a normal viewmodel
	val parent = destination.parent?.route ?: return koinViewModel<T>()

	val viewModelStoreOwner = remember(this) {
		controller.getBackStackEntry(parent)
	}
	// else return a viewmodel attach to the parent
	return koinViewModel<T>(viewModelStoreOwner = viewModelStoreOwner)
}