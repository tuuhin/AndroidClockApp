package com.eva.clockapp.features.alarms.presentation.create_alarm.state

import com.eva.clockapp.features.alarms.domain.models.RingtoneMusicFile

sealed interface AlarmSoundScreenEvent {
	data class OnMusicReadPermissionChange(val permission: Boolean) : AlarmSoundScreenEvent

	data class OnSelectRingtone(
		val ringtone: RingtoneMusicFile,
		val volume: Float,
	) : AlarmSoundScreenEvent

	data class OnMusicVolumeChange(val volume: Float) : AlarmSoundScreenEvent

	data class OnSoundEnableChange(val isEnabled: Boolean) : AlarmSoundScreenEvent
}