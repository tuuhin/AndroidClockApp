package com.eva.clockapp.features.alarms.presentation.util

import com.eva.clockapp.features.alarms.domain.models.AlarmsModel
import com.eva.clockapp.features.alarms.domain.models.AssociateAlarmFlags
import com.eva.clockapp.features.alarms.domain.models.RingtoneMusicFile
import com.eva.clockapp.features.alarms.domain.models.VibrationPattern
import com.eva.clockapp.features.alarms.domain.models.WallpaperPhoto
import com.eva.clockapp.features.alarms.presentation.alarms.state.SelectableAlarmModel
import com.eva.clockapp.features.alarms.presentation.create_alarm.state.CreateAlarmState
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.datetime.LocalTime
import java.time.DayOfWeek

object AlarmPreviewFakes {

	private val FAKE_RINGTONE_MODEL = RingtoneMusicFile(
		name = "Some Fake one",
		uri = "",
		type = RingtoneMusicFile.RingtoneType.DEVICE_LOCAL
	)

	val FAKE_CREATE_ALARM_STATE = CreateAlarmState(ringtone = FAKE_RINGTONE_MODEL)

	val FAKE_RINGTONES_OPTIONS = List(20) { idx ->
		FAKE_RINGTONE_MODEL.copy(
			type = if (idx % 2 == 0) RingtoneMusicFile.RingtoneType.DEVICE_LOCAL
			else RingtoneMusicFile.RingtoneType.APPLICATION_LOCAL
		)
	}.groupBy { it.type }.toImmutableMap()

	val FAKE_ASSOCIATE_FLAGS_STATE = AssociateAlarmFlags(
		isVibrationEnabled = false,
		isSnoozeEnabled = true,
		vibrationPattern = VibrationPattern.CALL_PATTERN
	)

	private val FAKE_ALARMS_MODEL = AlarmsModel(
		id = -1,
		time = LocalTime(0, 0),
		weekDays = setOf(DayOfWeek.MONDAY, DayOfWeek.FRIDAY),
		flags = AssociateAlarmFlags(),
		isAlarmEnabled = true,
		label = "Lorem Ipsum is simply dummy text of the printing and typesetting industry."
	)

	val FAKE_SELECTABLE_ALARM_MODEL = SelectableAlarmModel(model = FAKE_ALARMS_MODEL)

	val FAKE_SELECTABLE_ALARM_MODEL_LIST =
		List(10) { FAKE_SELECTABLE_ALARM_MODEL }.toImmutableList()

	val FAKE_SELECTABLE_ALARMS_LIST_SELECTED =
		List(10) {
			if (it < 6) FAKE_SELECTABLE_ALARM_MODEL.copy(isSelected = true)
			else FAKE_SELECTABLE_ALARM_MODEL
		}.toImmutableList()

	val FAKE_ALARMS_MODEL_LIST_EMPTY = persistentListOf<SelectableAlarmModel>()

	val RANDOM_BACKGROUND_OPTIONS =
		List(10) { WallpaperPhoto(id = it.toLong(), placeholderColor = 0, uri = "") }.toImmutableList()
}