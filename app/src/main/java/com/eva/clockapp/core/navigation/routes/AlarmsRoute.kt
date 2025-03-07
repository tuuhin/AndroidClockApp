package com.eva.clockapp.core.navigation.routes

import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.dropUnlessResumed
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.eva.clockapp.core.navigation.navgraphs.NavRoutes
import com.eva.clockapp.core.presentation.UIEventsSideEffect
import com.eva.clockapp.features.alarms.presentation.alarms.AlarmScreen
import com.eva.clockapp.features.alarms.presentation.alarms.AlarmsViewModel
import org.koin.compose.viewmodel.koinViewModel

fun NavGraphBuilder.alarmsRoute(controller: NavController) =
	composable<NavRoutes.AlarmsRoute> {

		val viewModel = koinViewModel<AlarmsViewModel>()
		val lifecyleOwner = LocalLifecycleOwner.current

		val alarms by viewModel.selectableAlarms.collectAsStateWithLifecycle()
		val nextAlarmDuration by viewModel.nextAlarmTime.collectAsStateWithLifecycle()
		val lifecyle by lifecyleOwner.lifecycle.currentStateFlow.collectAsStateWithLifecycle()

		UIEventsSideEffect(eventsFlow = viewModel.uiEvents)

		AlarmScreen(
			selectableAlarms = alarms,
			nextAlarmScheduledAfter = nextAlarmDuration,
			onEvent = viewModel::onEvent,
			onCreateNewAlarm = dropUnlessResumed {
				controller.navigate(NavRoutes.CreateOrUpdateAlarmRoute())
			},
			onSelectAlarm = { alarm ->
				if (lifecyle.isAtLeast(Lifecycle.State.RESUMED)) {
					controller.navigate(NavRoutes.CreateOrUpdateAlarmRoute(alarm.id))
				}
			}
		)
	}