package com.eva.clockapp.features.settings.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.eva.clockapp.R
import com.eva.clockapp.core.navigation.NavRoutes
import com.eva.clockapp.core.navigation.animatedComposable
import com.eva.clockapp.core.presentation.composables.UIEventsSideEffect
import com.eva.clockapp.features.settings.presentation.AlarmSettingsScreen
import com.eva.clockapp.features.settings.presentation.SettingsViewModel
import org.koin.compose.viewmodel.koinViewModel


fun NavGraphBuilder.settingsRoute(
	controller: NavController,
) = animatedComposable<NavRoutes.SettingsRoute> {

	val viewModel = koinViewModel<SettingsViewModel>()

	val alarmSettings by viewModel.alarmSettings.collectAsStateWithLifecycle()

	UIEventsSideEffect(viewModel.uiEvents)

	AlarmSettingsScreen(
		settings = alarmSettings,
		onEvent = viewModel::onEvent,
		navigation = {
			IconButton(onClick = dropUnlessResumed(block = controller::popBackStack)) {
				Icon(
					imageVector = Icons.AutoMirrored.Default.ArrowBack,
					contentDescription = stringResource(R.string.back_arrow)
				)
			}
		},
	)
}