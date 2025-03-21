package com.eva.clockapp.features.alarms.data.repository

import com.eva.clockapp.features.alarms.domain.controllers.AppRingtoneProvider
import com.eva.clockapp.features.alarms.domain.controllers.ContentRingtoneProvider
import com.eva.clockapp.features.alarms.domain.models.RingtoneMusicFile
import com.eva.clockapp.features.alarms.domain.repository.RingtonesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapNotNull

typealias Ringtones = Set<RingtoneMusicFile>

class RingtonesRepositoryImpl(
	private val contentProvider: ContentRingtoneProvider,
	private val localProvider: AppRingtoneProvider,
) : RingtonesRepository {

	override val default: RingtoneMusicFile
		get() = localProvider.default

	private val localRingtones: Ringtones
		get() = localProvider.ringtones.toSet()

	override val allRingtones: Flow<Result<Ringtones>>
		get() = flow {
			// load the local ones initially
			emit(Result.success(localRingtones))

			val newFlow = contentProvider.loadRingtonesAsFlow.mapNotNull { result ->
				if (result.isSuccess) {
					val external = result.getOrThrow()
					Result.success(localRingtones + external)
				} else {
					result.exceptionOrNull()?.let { Result.failure(it) }
				}
			}
			emitAll(newFlow)
		}

	override suspend fun getRingtoneFromUri(uri: String): RingtoneMusicFile? {
		return try {

			if (uri == default.uri) return default

			val type = when {
				uri.startsWith("android.resource://") -> RingtoneMusicFile.RingtoneType.APPLICATION_LOCAL
				uri.startsWith("content://") -> RingtoneMusicFile.RingtoneType.DEVICE_LOCAL
				else -> return null
			}

			when (type) {
				RingtoneMusicFile.RingtoneType.APPLICATION_LOCAL ->
					localProvider.getRingtoneFromUri(uri)

				RingtoneMusicFile.RingtoneType.DEVICE_LOCAL ->
					contentProvider.getRingtoneFromUri(uri).getOrDefault(null)
			}

		} catch (e: Exception) {
			e.printStackTrace()
			null
		}
	}
}