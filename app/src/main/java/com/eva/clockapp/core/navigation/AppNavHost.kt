package com.eva.clockapp.core.navigation

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.eva.clockapp.core.presentation.LocalSnackBarHostState
import com.eva.clockapp.features.alarms.presentation.alarms.navigation.alarmsRoute
import com.eva.clockapp.features.alarms.presentation.create_alarm.navigation.creteAlarmsNavGraph
import com.eva.clockapp.features.settings.presentation.navigation.settingsRoute
import org.koin.compose.KoinContext

@Composable
fun AppNavHost(
	modifier: Modifier = Modifier,
	controller: NavHostController = rememberNavController(),
) {
	val snackBarProvider = remember { SnackbarHostState() }

	KoinContext {
		CompositionLocalProvider(
			LocalSnackBarHostState provides snackBarProvider,
		) {
			NavHost(
				navController = controller,
				startDestination = NavRoutes.AlarmsRoute,
				modifier = modifier,
			) {
				// show alarms
				alarmsRoute(controller = controller)
				// create alarms nav graph
				creteAlarmsNavGraph(controller = controller)
				//settings route
				settingsRoute(controller = controller)
			}
		}
	}
}