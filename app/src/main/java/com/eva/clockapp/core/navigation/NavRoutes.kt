package com.eva.clockapp.core.navigation

import kotlinx.serialization.Serializable

interface NavRoutes {

	@Serializable
	data object AlarmsRoute : NavRoutes

	@Serializable
	data class CreateOrUpdateAlarmRoute(val alarmId: Int? = null) : NavRoutes

	@Serializable
	data object SettingsRoute : NavRoutes
}
