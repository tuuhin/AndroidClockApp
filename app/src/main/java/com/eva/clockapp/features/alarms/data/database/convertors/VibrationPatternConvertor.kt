package com.eva.clockapp.features.alarms.data.database.convertors

import androidx.room.TypeConverter
import com.eva.clockapp.features.alarms.domain.models.VibrationPattern

class VibrationPatternConvertor {

	@TypeConverter
	fun toVibrationPattern(name: String): VibrationPattern = VibrationPattern.valueOf(name)

	@TypeConverter
	fun fromVibrationPattern(pattern: VibrationPattern): String = pattern.name

}