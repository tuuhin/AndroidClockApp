package com.eva.clockapp.features.alarms.domain.controllers

import com.eva.clockapp.features.alarms.domain.models.RingtoneMusicFile
import kotlinx.coroutines.flow.Flow

interface ContentRingtoneProvider {

	val loadRingtonesAsFlow: Flow<Result<List<RingtoneMusicFile>>>

	suspend fun loadRingtones(): Result<List<RingtoneMusicFile>>

	suspend fun getRingtoneFromId(id: Long): Result<RingtoneMusicFile>
}