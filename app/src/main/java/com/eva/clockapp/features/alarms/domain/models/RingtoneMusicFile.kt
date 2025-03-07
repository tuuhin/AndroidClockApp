package com.eva.clockapp.features.alarms.domain.models

data class RingtoneMusicFile(
	val name: String,
	val uri: String,
	val type: RingtoneType,
) {
	enum class RingtoneType {
		APPLICATION_LOCAL,
		DEVICE_LOCAL,
	}
}