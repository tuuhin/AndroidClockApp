package com.eva.clockapp.core.database.convertors

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import kotlinx.datetime.LocalTime
import kotlinx.datetime.format

@ProvidedTypeConverter
class LocalTimeConvertor {

	@TypeConverter
	fun toLocaltime(value: String): LocalTime =
		LocalTime.parse(value, format = LocalTime.Formats.ISO)

	@TypeConverter
	fun fromLocaltime(time: LocalTime): String = time.format(LocalTime.Formats.ISO)

}