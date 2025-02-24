package com.eva.clockapp.core.database.convertors

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import kotlinx.datetime.DayOfWeek
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@ProvidedTypeConverter
class WeekdaysConvertor {

	private val json = Json { ignoreUnknownKeys = true }

	@TypeConverter
	fun toWeekDays(dayOfWeekAsJsonString: String): Set<DayOfWeek> =
		json.decodeFromString<List<DayOfWeek>>(dayOfWeekAsJsonString).toSet()

	@TypeConverter
	fun fromWeekDays(weekDays: Set<DayOfWeek>): String = json.encodeToString(weekDays)

}