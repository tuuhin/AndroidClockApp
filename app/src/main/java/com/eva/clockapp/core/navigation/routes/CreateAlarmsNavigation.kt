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
import com.eva.clockapp.features.alarms.presentation.create_alarm.AlarmsBackgroundViewModel
import com.eva.clockapp.features.alarms.presentation.create_alarm.AlarmsSoundsViewmodel
import com.eva.clockapp.features.alarms.presentation.create_alarm.CreateAlarmViewModel
import com.eva.clockapp.features.alarms.presentation.create_alarm.screens.AlarmSnoozeScreen
import com.eva.clockapp.features.alarms.presentation.create_alarm.screens.AlarmSoundScreen
import com.eva.clockapp.features.alarms.presentation.create_alarm.screens.AlarmVibrationScreen
import com.eva.clockapp.features.alarms.presentation.create_alarm.screens.AlarmsBackgroundScreen
import com.eva.clockapp.features.alarms.presentation.create_alarm.screens.CreateAlarmScreen
import com.eva.clockapp.features.alarms.presentation.gallery.GalleryImageScreen
import com.eva.clockapp.features.alarms.presentation.gallery.GalleryScreenViewModel
import org.koin.compose.viewmodel.koinViewModel

fun NavGraphBuilder.creteAlarmsNavGraph(controller: NavController) =
	navigation<NavRoutes.CreateOrUpdateAlarmRoute>(startDestination = CreateAlarmNavRoute.CreateRoute) {

		animatedComposable<CreateAlarmNavRoute.CreateRoute> { backStack ->

			val viewModel = backStack.sharedViewModel<CreateAlarmViewModel>(controller)

			val state by viewModel.createAlarmState.collectAsStateWithLifecycle()
			val flagsState by viewModel.flagsState.collectAsStateWithLifecycle()

			UIEventsSideEffect(
				viewModel.uiEvents,
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
				onNavigateBackgroundScreen = dropUnlessResumed {
					controller.navigate(CreateAlarmNavRoute.SelectBackgroundRoute)
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

			UIEventsSideEffect(viewModel.uiEvents)

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

			UIEventsSideEffect(viewModel.uiEvents)

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

			val sharedViewmodel = backStack.sharedViewModel<CreateAlarmViewModel>(controller)
			val flags by sharedViewmodel.flagsState.collectAsStateWithLifecycle()
			val alarmState by sharedViewmodel.createAlarmState.collectAsStateWithLifecycle()

			val viewModel = koinViewModel<AlarmsSoundsViewmodel>()
			val soundOptions by viewModel.soundOptions.collectAsStateWithLifecycle()


			UIEventsSideEffect(sharedViewmodel.uiEvents, viewModel.uiEvents)

			AlarmSoundScreen(
				state = alarmState,
				flags = flags,
				ringtones = soundOptions,
				onEvent = viewModel::onEvent,
				onFlagsEvent = sharedViewmodel::onFlagsEvent,
				onCreateEvent = sharedViewmodel::onEvent,
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

		animatedComposable<CreateAlarmNavRoute.SelectBackgroundRoute> { backStack ->

			val viewModel = koinViewModel<AlarmsBackgroundViewModel>()

			val screenState by viewModel.screenState.collectAsStateWithLifecycle()

			val sharedViewmodel = backStack.sharedViewModel<CreateAlarmViewModel>(controller)
			val state by sharedViewmodel.createAlarmState.collectAsStateWithLifecycle()

			UIEventsSideEffect(sharedViewmodel.uiEvents, viewModel.uiEvents)

			AlarmsBackgroundScreen(
				wallpapersState = screenState,
				state = state,
				onEvent = sharedViewmodel::onEvent,
				onSelectionDone = dropUnlessResumed { controller.popBackStack() },
				onSelectFromDevice = dropUnlessResumed { controller.navigate(CreateAlarmNavRoute.GalleryRoute) },
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

		animatedComposable<CreateAlarmNavRoute.GalleryRoute> { backStack ->

			val viewModel = koinViewModel<GalleryScreenViewModel>()

			val screenState by viewModel.screenState.collectAsStateWithLifecycle()
			val sharedViewmodel = backStack.sharedViewModel<CreateAlarmViewModel>(controller)

			UIEventsSideEffect(
				viewModel.uiEvents,
				sharedViewmodel.uiEvents,
				onBack = dropUnlessResumed { controller.popBackStack() },
			)

			GalleryImageScreen(
				state = screenState,
				onEvent = viewModel::onEvent,
				onCreateEvent = sharedViewmodel::onEvent,
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
	}

