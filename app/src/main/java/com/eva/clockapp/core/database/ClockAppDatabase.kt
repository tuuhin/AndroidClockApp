package com.eva.clockapp.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.eva.clockapp.core.database.convertors.LocalTimeConvertor
import com.eva.clockapp.features.alarms.data.database.convertors.SnoozeIntervalConvertor
import com.eva.clockapp.features.alarms.data.database.convertors.SnoozeRepeatConvertor
import com.eva.clockapp.features.alarms.data.database.convertors.VibrationPatternConvertor
import com.eva.clockapp.core.database.convertors.WeekdaysConvertor
import com.eva.clockapp.features.alarms.data.database.AlarmsDao
import com.eva.clockapp.features.alarms.data.database.AlarmsEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor

@Database(
	version = 1,
	exportSchema = true,
	entities = [
		AlarmsEntity::class
	],
)
@TypeConverters(
	value = [
		LocalTimeConvertor::class,
		SnoozeIntervalConvertor::class,
		SnoozeRepeatConvertor::class,
		VibrationPatternConvertor::class,
		WeekdaysConvertor::class
	]
)
abstract class ClockAppDatabase : RoomDatabase() {

	abstract fun alarmsDao(): AlarmsDao

	companion object {
		fun createDatabase(context: Context): ClockAppDatabase =
			Room.databaseBuilder(context, ClockAppDatabase::class.java, "APP_DATABASE")
				.addTypeConverter(LocalTimeConvertor())
				.addTypeConverter(SnoozeRepeatConvertor())
				.addTypeConverter(SnoozeIntervalConvertor())
				.addTypeConverter(VibrationPatternConvertor())
				.addTypeConverter(WeekdaysConvertor())
				.setQueryExecutor(Dispatchers.IO.asExecutor())
				.setTransactionExecutor(Dispatchers.IO.asExecutor())
				.build()
	}
}