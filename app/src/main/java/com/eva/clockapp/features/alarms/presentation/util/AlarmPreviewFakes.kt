package com.eva.clockapp.features.alarms.presentation.util

import com.eva.clockapp.features.alarms.domain.models.AlarmsModel
import com.eva.clockapp.features.alarms.domain.models.AssociateAlarmFlags
import com.eva.clockapp.features.alarms.domain.models.RingtoneMusicFile
import com.eva.clockapp.features.alarms.domain.models.VibrationPattern
import com.eva.clockapp.features.alarms.presentation.create_alarm.state.AlarmSoundOptions
import com.eva.clockapp.features.alarms.presentation.create_alarm.state.CreateAlarmState
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.datetime.LocalTime
import java.time.DayOfWeek

object AlarmPreviewFakes {

	private val FAKE_RINGTONE_MODEL = RingtoneMusicFile(name = "Some Fake one", uri = "")

	val FAKE_CREATE_ALARM_STATE = CreateAlarmState()

	val FAKE_ALARM_SOUND_STATE = AlarmSoundOptions(
		local = List(2) { RingtoneMusicFile(name = "Local:$it", uri = "") }.toImmutableList(),
		external = List(10) { RingtoneMusicFile(name = "Device:$it", uri = "") }.toImmutableList(),
		selectedSound = FAKE_RINGTONE_MODEL,
	)

	val FAKE_ALARM_SOUND_STATE_NO_EXTERNAL = AlarmSoundOptions(
		local = List(2) { RingtoneMusicFile(name = "Local:$it", uri = "") }.toImmutableList(),
		external = persistentListOf(),
		selectedSound = FAKE_RINGTONE_MODEL,
	)

	val FAKE_ASSOCIATE_FLAGS_STATE = AssociateAlarmFlags(
		isVibrationEnabled = false,
		isSnoozeEnabled = true,
		vibrationPattern = VibrationPattern.CALL_PATTERN
	)

	val FAKE_ALARMS_MODEL = AlarmsModel(
		id = -1,
		time = LocalTime(0, 0),
		weekDays = setOf(DayOfWeek.MONDAY, DayOfWeek.FRIDAY),
		flags = AssociateAlarmFlags(),
		isAlarmEnabled = true,
		label = "Something..."
	)

	val FAKE_ALARMS_MODEL_LIST = List(10) {
		FAKE_ALARMS_MODEL.copy(id = it)
	}.toImmutableList()

	val FAKE_ALARMS_MODEL_LIST_EMPTY = persistentListOf<AlarmsModel>()
}