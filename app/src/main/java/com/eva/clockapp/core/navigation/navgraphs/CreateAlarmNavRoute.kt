package com.eva.clockapp.core.navigation.navgraphs

import kotlinx.serialization.Serializable

interface CreateAlarmNavRoute {

	@Serializable
	data object CreateRoute : CreateAlarmNavRoute

	@Serializable
	data object SelectVibrationRoute : CreateAlarmNavRoute

	@Serializable
	data object SelectSnoozeOptionRoute : CreateAlarmNavRoute

	@Serializable
	data object SelectSoundOptionRoute : CreateAlarmNavRoute

	@Serializable
	data object SelectBackgroundRoute : CreateAlarmNavRoute

	@Serializable
	data object GalleryRoute : CreateAlarmNavRoute
}