package com.eva.clockapp.features.alarms.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmsDao {

	@Upsert
	suspend fun insertOrUpdateAlarm(entity: AlarmsEntity): Long

	@Insert(onConflict = OnConflictStrategy.IGNORE)
	suspend fun insertAlarmBulk(entities: List<AlarmsEntity>)

	@Query("SELECT * FROM ALARMS_TABLE")
	fun getAllAlarmsAsFlow(): Flow<List<AlarmsEntity>>

	@Query("SELECT * FROM ALARMS_TABLE")
	suspend fun getAllAlarmsAsList(): List<AlarmsEntity>

	@Query("UPDATE ALARMS_TABLE SET IS_ALARM_ENABLED=:isEnabled WHERE _ID=:alarmId")
	suspend fun switchIsEnableAlarm(alarmId: Int, isEnabled: Boolean)

	@Query("SELECT * FROM ALARMS_TABLE WHERE _ID=:id")
	suspend fun getAlarmFromId(id: Int): AlarmsEntity?

	@Delete
	suspend fun deleteAlarm(entity: AlarmsEntity)

	@Delete
	suspend fun deleteAlarmBulk(entities: List<AlarmsEntity>)
}