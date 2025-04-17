package com.eva.clockapp.core.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface BottomBarNavRoutes {

	@Serializable
	data object AlarmsRoute : BottomBarNavRoutes

	@Serializable
	data object TimerRoute : BottomBarNavRoutes
}
