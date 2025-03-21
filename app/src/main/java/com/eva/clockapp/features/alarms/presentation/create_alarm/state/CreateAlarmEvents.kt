package com.eva.clockapp.features.alarms.presentation.create_alarm.state

import com.eva.clockapp.features.alarms.domain.models.GalleryImageModel
import com.eva.clockapp.features.alarms.domain.models.RingtoneMusicFile
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime

sealed interface CreateAlarmEvents {
	data class OnAlarmTimeChange(val time: LocalTime) : CreateAlarmEvents
	data object SetStartTimeAsSelectedTime : CreateAlarmEvents

	data class OnAddOrRemoveWeekDay(val dayOfWeek: DayOfWeek) : CreateAlarmEvents
	data class OnSelectUriForBackground(val background: String?) : CreateAlarmEvents
	data class OnSelectGalleryImage(val model: GalleryImageModel) : CreateAlarmEvents

	data class OnLabelValueChange(val newValue: String) : CreateAlarmEvents
	data class OnSoundSelected(val sound: RingtoneMusicFile) : CreateAlarmEvents

	data object OnSaveAlarm : CreateAlarmEvents
	data object OnUpdateAlarm : CreateAlarmEvents
}