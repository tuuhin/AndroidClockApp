package com.eva.clockapp.features.alarms.presentation.alarms.navigation

import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.eva.clockapp.core.navigation.NavRoutes
import com.eva.clockapp.core.presentation.composables.UIEventsSideEffect
import com.eva.clockapp.features.alarms.presentation.alarms.AlarmScreen
import com.eva.clockapp.features.alarms.presentation.alarms.AlarmsViewModel
import org.koin.compose.viewmodel.koinViewModel

fun NavGraphBuilder.alarmsRoute(controller: NavController) =
	composable<NavRoutes.AlarmsRoute> {

		val viewModel = koinViewModel<AlarmsViewModel>()
		val lifecyleOwner = LocalLifecycleOwner.current

		val alarms by viewModel.selectableAlarms.collectAsStateWithLifecycle()
		val isLoaded by viewModel.isLoaded.collectAsStateWithLifecycle()
		val nextAlarmDuration by viewModel.nextAlarmTime.collectAsStateWithLifecycle()
		val settings by viewModel.alarmSettings.collectAsStateWithLifecycle()

		val lifecyle by lifecyleOwner.lifecycle.currentStateFlow.collectAsStateWithLifecycle()

		UIEventsSideEffect(viewModel.uiEvents)

		AlarmScreen(
			isContentReady = isLoaded,
			selectableAlarms = alarms,
			nextAlarmScheduled = nextAlarmDuration,
			settings = settings,
			onEvent = viewModel::onEvent,
			onCreateNewAlarm = dropUnlessResumed {
				controller.navigate(NavRoutes.CreateOrUpdateAlarmRoute())
			},
			onSelectAlarm = { alarm ->
				if (lifecyle.isAtLeast(Lifecycle.State.RESUMED)) {
					controller.navigate(NavRoutes.CreateOrUpdateAlarmRoute(alarm.id))
				}
			},
			onNavigateToSettings = dropUnlessResumed {
				controller.navigate(NavRoutes.SettingsRoute)
			},
		)
	}