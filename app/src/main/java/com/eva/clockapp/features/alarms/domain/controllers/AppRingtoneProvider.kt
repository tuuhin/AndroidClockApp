package com.eva.clockapp.features.alarms.domain.controllers

import com.eva.clockapp.features.alarms.domain.models.RingtoneMusicFile

interface AppRingtoneProvider {

	val default: RingtoneMusicFile

	val ringtones: Result<List<RingtoneMusicFile>>
}