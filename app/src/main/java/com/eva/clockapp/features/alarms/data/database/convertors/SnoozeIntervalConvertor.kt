package com.eva.clockapp.features.alarms.data.database.convertors

import androidx.room.TypeConverter
import com.eva.clockapp.features.alarms.domain.models.SnoozeIntervalOption
import kotlin.time.DurationUnit

class SnoozeIntervalConvertor {

	@TypeConverter
	fun toSnoozeInterval(timeInMinutes: Int): SnoozeIntervalOption = when (timeInMinutes) {
		3 -> SnoozeIntervalOption.IntervalThreeMinutes
		10 -> SnoozeIntervalOption.IntervalTenMinutes
		15 -> SnoozeIntervalOption.IntervalFifteenMinutes
		30 -> SnoozeIntervalOption.IntervalThirtyMinutes
		else -> SnoozeIntervalOption.IntervalCustomMinutes(timeInMinutes)
	}

	@TypeConverter
	fun fromSnoozeInterval(intervalOption: SnoozeIntervalOption): Int =
		intervalOption.duration.toInt(DurationUnit.MINUTES)

}