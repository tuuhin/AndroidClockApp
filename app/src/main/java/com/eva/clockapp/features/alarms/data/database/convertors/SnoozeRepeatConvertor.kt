package com.eva.clockapp.features.alarms.data.database.convertors

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.eva.clockapp.features.alarms.domain.models.SnoozeRepeatMode

@ProvidedTypeConverter
class SnoozeRepeatConvertor {

	@TypeConverter
	fun toRepeatMode(name: String): SnoozeRepeatMode = SnoozeRepeatMode.valueOf(name)

	@TypeConverter
	fun fromRepeatMode(mode: SnoozeRepeatMode): String = mode.name
}