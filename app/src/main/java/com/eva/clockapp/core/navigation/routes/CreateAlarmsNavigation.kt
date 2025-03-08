package com.eva.clockapp.core.navigation.routes

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
import androidx.navigation.compose.navigation
import com.eva.clockapp.R
import com.eva.clockapp.core.navigation.animatedComposable
import com.eva.clockapp.core.navigation.navgraphs.CreateAlarmNavRoute
import com.eva.clockapp.core.navigation.navgraphs.NavRoutes
import com.eva.clockapp.core.presentation.composables.UIEventsSideEffect
import com.eva.clockapp.core.presentation.composables.sharedViewModel
import com.eva.clockapp.features.alarms.presentation.create_alarm.CreateAlarmViewModel
import com.eva.clockapp.features.alarms.presentation.create_alarm.screens.AlarmSnoozeScreen
import com.eva.clockapp.features.alarms.presentation.create_alarm.screens.AlarmSoundScreen
import com.eva.clockapp.features.alarms.presentation.create_alarm.screens.AlarmVibrationScreen
import com.eva.clockapp.features.alarms.presentation.create_alarm.screens.CreateAlarmScreen

fun NavGraphBuilder.creteAlarmsNavGraph(controller: NavController) =
	navigation<NavRoutes.CreateOrUpdateAlarmRoute>(startDestination = CreateAlarmNavRoute.CreateRoute) {

		animatedComposable<CreateAlarmNavRoute.CreateRoute> { backStack ->

			val viewModel = backStack.sharedViewModel<CreateAlarmViewModel>(controller)

			val state by viewModel.createAlarmState.collectAsStateWithLifecycle()
			val flagsState by viewModel.flagsState.collectAsStateWithLifecycle()

			UIEventsSideEffect(
				eventsFlow = viewModel.uiEvents,
				onBack = dropUnlessResumed { controller.popBackStack() },
			)

			CreateAlarmScreen(
				state = state,
				flags = flagsState,
				onEvent = viewModel::onEvent,
				onFlagsEvent = viewModel::onFlagsEvent,
				onNavigateSnoozeScreen = dropUnlessResumed {
					controller.navigate(CreateAlarmNavRoute.SelectSnoozeOptionRoute)
				},
				onNavigateVibrationScreen = dropUnlessResumed {
					controller.navigate(CreateAlarmNavRoute.SelectVibrationRoute)
				},
				onNavigateSoundScreen = dropUnlessResumed {
					controller.navigate(CreateAlarmNavRoute.SelectSoundOptionRoute)
				},
				navigation = {
					IconButton(
						onClick = dropUnlessResumed(block = controller::popBackStack)
					) {
						Icon(
							Icons.AutoMirrored.Default.ArrowBack,
							contentDescription = stringResource(R.string.back_arrow)
						)
					}
				},
			)
		}

		animatedComposable<CreateAlarmNavRoute.SelectVibrationRoute> { backStack ->

			val viewModel = backStack.sharedViewModel<CreateAlarmViewModel>(controller)
			val flagsState by viewModel.flagsState.collectAsStateWithLifecycle()

			UIEventsSideEffect(eventsFlow = viewModel.uiEvents)

			AlarmVibrationScreen(
				state = flagsState,
				onEvent = viewModel::onFlagsEvent,
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

		animatedComposable<CreateAlarmNavRoute.SelectSnoozeOptionRoute> { backStack ->

			val viewModel = backStack.sharedViewModel<CreateAlarmViewModel>(controller)
			val flagsState by viewModel.flagsState.collectAsStateWithLifecycle()

			UIEventsSideEffect(eventsFlow = viewModel.uiEvents)

			AlarmSnoozeScreen(
				state = flagsState,
				onEvent = viewModel::onFlagsEvent,
				navigation = {
					IconButton(onClick = dropUnlessResumed(block = controller::popBackStack)) {
						Icon(
							imageVector = Icons.AutoMirrored.Default.ArrowBack,
							contentDescription = stringResource(R.string.back_arrow)
						)
					}
				}
			)
		}

		animatedComposable<CreateAlarmNavRoute.SelectSoundOptionRoute> { backStack ->

			val viewModel = backStack.sharedViewModel<CreateAlarmViewModel>(controller)
			val flags by viewModel.flagsState.collectAsStateWithLifecycle()
			val ringtoneOptions by viewModel.soundOptions.collectAsStateWithLifecycle()
			val alarmState by viewModel.createAlarmState.collectAsStateWithLifecycle()

			UIEventsSideEffect(eventsFlow = viewModel.uiEvents)


			AlarmSoundScreen(
				state = alarmState,
				flags = flags,
				ringtones = ringtoneOptions,
				onFlagsEvent = viewModel::onFlagsEvent,
				onEvent = viewModel::onEvent,
				navigation = {
					IconButton(onClick = dropUnlessResumed(block = controller::popBackStack)) {
						Icon(
							imageVector = Icons.AutoMirrored.Default.ArrowBack,
							contentDescription = stringResource(R.string.back_arrow)
						)
					}
				}
			)
		}
	}

