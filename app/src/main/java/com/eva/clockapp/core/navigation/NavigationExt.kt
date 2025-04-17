package com.eva.clockapp.core.navigation

import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy

fun NavBackStackEntry.isBottomBarItemSelected(route: BottomBarNavRoutes): Boolean =
	destination.hierarchy.any { it.hasRoute(route::class) } == true
