package com.eva.clockapp.core.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface NavRoutes {

	@Serializable
	data class CreateOrUpdateAlarmRoute(val alarmId: Int? = null) : NavRoutes

	@Serializable
	data object SettingsRoute : NavRoutes
}
