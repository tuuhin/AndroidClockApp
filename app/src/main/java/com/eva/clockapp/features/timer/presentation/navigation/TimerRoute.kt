package com.eva.clockapp.features.timer.presentation.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.eva.clockapp.core.navigation.BottomBarNavRoutes
import com.eva.clockapp.core.navigation.tabAnimatedComposable
import com.eva.clockapp.core.presentation.composables.UIEventsSideEffect
import com.eva.clockapp.features.timer.presentation.TimerScreen
import com.eva.clockapp.features.timer.presentation.TimerViewModel
import org.koin.compose.viewmodel.koinViewModel

fun NavGraphBuilder.timerRoute(controller: NavController) =
	tabAnimatedComposable<BottomBarNavRoutes.TimerRoute> {

		val viewModel = koinViewModel<TimerViewModel>()

		UIEventsSideEffect(viewModel.uiEvents)

		TimerScreen()
	}