package com.eva.clockapp.core.navigation.routes

import androidx.compose.runtime.getValue
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

		val alarms by viewModel.alarms.collectAsStateWithLifecycle()

		UIEventsSideEffect(eventsFlow = viewModel.uiEvents)

		AlarmScreen(
			alarms = alarms,
			onEvent = viewModel::onEvent,
			onCreateNewAlarm = dropUnlessResumed {
				controller.navigate(NavRoutes.CreateAlarmRoute)
			},
		)
	}