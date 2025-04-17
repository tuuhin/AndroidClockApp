package com.eva.clockapp.core.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.eva.clockapp.R
import com.eva.clockapp.core.presentation.LocalSnackBarHostState
import com.eva.clockapp.features.alarms.presentation.alarms.navigation.alarmsRoute
import com.eva.clockapp.features.alarms.presentation.create_alarm.navigation.creteAlarmsNavGraph
import com.eva.clockapp.features.settings.presentation.navigation.settingsRoute
import com.eva.clockapp.features.timer.presentation.navigation.timerRoute

@Composable
fun AppNavHost(
	modifier: Modifier = Modifier,
	controller: NavHostController = rememberNavController(),
) {
	val context = LocalContext.current

	val bottomNavigationRoutes = remember {
		listOf(
			BottomNavigationRouteItem(
				name = context.getString(R.string.alarms_screen_title),
				icon = Icons.Outlined.Alarm,
				route = BottomBarNavRoutes.AlarmsRoute
			),
			BottomNavigationRouteItem(
				name = context.getString(R.string.timer_screen_title),
				icon = Icons.Outlined.Timer,
				route = BottomBarNavRoutes.TimerRoute
			)
		)
	}

	val backStackEntry by controller.currentBackStackEntryAsState()
	val canShowNavBar = remember(backStackEntry) {
		backStackEntry?.let {
			val itemRouteClass = bottomNavigationRoutes.map { item -> item.route::class }
			itemRouteClass.any { backStackEntry?.destination?.hasRoute(it) == true }
		} ?: return@remember false
	}

	Scaffold(
		bottomBar = {
			AnimatedVisibility(
				visible = canShowNavBar,
				enter = slideInVertically(initialOffsetY = { height -> height }),
				exit = slideOutVertically(targetOffsetY = { height -> height }),
			) {
				NavigationBar {
					bottomNavigationRoutes.forEach { navBarItem ->
						NavigationBarItem(
							selected = backStackEntry?.isBottomBarItemSelected(navBarItem.route) == true,
							onClick = {
								controller.navigate(navBarItem.route) {
									popUpTo(controller.graph.findStartDestination().id) {
										saveState = true
									}
									launchSingleTop = true
									restoreState = true
								}
							},
							icon = {
								Icon(
									imageVector = navBarItem.icon,
									contentDescription = navBarItem.name
								)
							},
							label = { Text(text = navBarItem.name) },
						)
					}
				}
			}
		},
		modifier = modifier,
	) { scPadding ->

		val snackBarProvider = remember { SnackbarHostState() }

		CompositionLocalProvider(
			LocalSnackBarHostState provides snackBarProvider,
		) {
			NavHost(
				navController = controller,
				startDestination = BottomBarNavRoutes.AlarmsRoute,
				modifier = Modifier
					.fillMaxSize()
					.padding(scPadding),
			) {
				// alarms route
				alarmsRoute(controller = controller)
				// timer route
				timerRoute(controller = controller)
				// create alarms nav graph
				creteAlarmsNavGraph(controller = controller)
				//settings route
				settingsRoute(controller = controller)
			}
		}
	}
}

private data class BottomNavigationRouteItem(
	val name: String,
	val icon: ImageVector,
	val route: BottomBarNavRoutes,
)