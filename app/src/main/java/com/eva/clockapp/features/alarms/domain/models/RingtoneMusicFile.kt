package com.eva.clockapp.features.alarms.domain.models

import kotlin.time.Duration

data class RingtoneMusicFile(
	val name: String,
	val uri: String,
	val duration: Duration,
)
