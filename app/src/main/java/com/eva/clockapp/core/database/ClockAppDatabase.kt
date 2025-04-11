package com.eva.clockapp.core.database

import android.content.Context
import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.eva.clockapp.core.database.convertors.LocalTimeConvertor
import com.eva.clockapp.core.database.convertors.WeekdaysConvertor
import com.eva.clockapp.features.alarms.data.database.AlarmsDao
import com.eva.clockapp.features.alarms.data.database.AlarmsEntity
import com.eva.clockapp.features.alarms.data.database.convertors.SnoozeIntervalConvertor
import com.eva.clockapp.features.alarms.data.database.convertors.SnoozeRepeatConvertor
import com.eva.clockapp.features.alarms.data.database.convertors.VibrationPatternConvertor

@Database(
	version = 2,
	exportSchema = true,
	entities = [
		AlarmsEntity::class
	],
	autoMigrations = [
		AutoMigration(from = 1, to = 2)
	]
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
				.build()
	}
}