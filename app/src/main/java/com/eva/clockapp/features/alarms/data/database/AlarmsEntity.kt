package com.eva.clockapp.features.alarms.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.eva.clockapp.features.alarms.domain.models.SnoozeIntervalOption
import com.eva.clockapp.features.alarms.domain.models.SnoozeRepeatMode
import com.eva.clockapp.features.alarms.domain.models.VibrationPattern
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime

@Entity(tableName = "ALARMS_TABLE")
data class AlarmsEntity(

	@PrimaryKey(autoGenerate = true)
	@ColumnInfo(name = "_ID")
	val id: Int? = null,

	@ColumnInfo(name = "ALARM_TIME")
	val time: LocalTime,

	@ColumnInfo(name = "WEEK_DAYS")
	val weekDays: Set<DayOfWeek>,

	@ColumnInfo(name = "IS_ALARM_ENABLED")
	val isAlarmEnabled: Boolean = true,

	@ColumnInfo(name = "VIBRATION_PATTERN")
	val vibrationPattern: VibrationPattern = VibrationPattern.CALL_PATTERN,

	@ColumnInfo(name = "SNOOZE_INTERVAL")
	val snoozeInterval: SnoozeIntervalOption = SnoozeIntervalOption.IntervalThreeMinutes,

	@ColumnInfo(name = "SNOOZE_REPEAT_MODE")
	val snoozeRepeatMode: SnoozeRepeatMode = SnoozeRepeatMode.NO_REPEAT,

	@ColumnInfo(name = "IS_SNOOZE_ENABLED")
	val isSnoozeEnabled: Boolean = true,

	@ColumnInfo(name = "IS_VIBRATION_ENABLED")
	val isVibrationEnabled: Boolean = true,

	@ColumnInfo(name = "IS_SOUND_ENABLED")
	val isSoundEnabled: Boolean = true,

	@ColumnInfo("IS_INCREMENTAL_VOLUME_INCREASE")
	val isVolumeStepIncrease: Boolean = false,

	@ColumnInfo("ALARM_VOLUME")
	val alarmVolume: Float = 100f,

	@ColumnInfo(name = "ASSOCIATE_LABEL")
	val label: String? = null,

	@ColumnInfo(name = "ALARM_SOUND_FILE")
	val alarmSoundUri: String? = null,

	@ColumnInfo(name = "BACKGROUND_URI")
	val background: String? = null,
)
