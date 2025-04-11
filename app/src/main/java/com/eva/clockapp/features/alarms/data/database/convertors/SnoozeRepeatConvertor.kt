package com.eva.clockapp.features.alarms.data.database.convertors

import androidx.room.TypeConverter
import com.eva.clockapp.features.alarms.domain.models.SnoozeRepeatMode

class SnoozeRepeatConvertor {

	@TypeConverter
	fun toRepeatMode(name: String): SnoozeRepeatMode = SnoozeRepeatMode.valueOf(name)

	@TypeConverter
	fun fromRepeatMode(mode: SnoozeRepeatMode): String = mode.name
}