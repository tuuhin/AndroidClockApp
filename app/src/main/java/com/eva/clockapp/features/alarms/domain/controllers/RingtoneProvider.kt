package com.eva.clockapp.features.alarms.domain.controllers

import com.eva.clockapp.features.alarms.domain.models.RingtoneMusicFile

interface RingtoneProvider {

	suspend fun loadRingtones(): Result<List<RingtoneMusicFile>>

	suspend fun getRingtoneFromId(id: Long): Result<RingtoneMusicFile>
}