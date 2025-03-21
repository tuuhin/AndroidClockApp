package com.eva.clockapp.features.alarms.domain.repository

import com.eva.clockapp.features.alarms.data.repository.Ringtones
import com.eva.clockapp.features.alarms.domain.models.RingtoneMusicFile
import kotlinx.coroutines.flow.Flow

interface RingtonesRepository {

	val default: RingtoneMusicFile

	val allRingtones: Flow<Result<Ringtones>>

	suspend fun getRingtoneFromUri(uri: String): RingtoneMusicFile?
}