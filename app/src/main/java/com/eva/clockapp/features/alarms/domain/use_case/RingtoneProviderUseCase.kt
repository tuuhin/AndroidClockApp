package com.eva.clockapp.features.alarms.domain.use_case

import com.eva.clockapp.features.alarms.domain.controllers.AppRingtoneProvider
import com.eva.clockapp.features.alarms.domain.controllers.ContentRingtoneProvider
import com.eva.clockapp.features.alarms.domain.models.RingtoneMusicFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.mapNotNull

typealias Ringtones = Set<RingtoneMusicFile>

class RingtoneProviderUseCase(
	private val ringtoneProvider: ContentRingtoneProvider,
	private val localRingtoneProvider: AppRingtoneProvider,
) {

	val default: RingtoneMusicFile
		get() = localRingtoneProvider.default

	private val localTones = mutableSetOf<RingtoneMusicFile>()

	operator fun invoke(): Flow<Result<Ringtones>> = flow {
		// load the local ones initially
		localRingtoneProvider.ringtones.fold(
			onSuccess = { tones ->
				localTones.addAll(tones)
				emit(Result.success(tones.toSet()))
			},
			onFailure = { err -> emit(Result.failure(err)) },
		)

		val newFlow = ringtoneProvider.loadRingtonesAsFlow.mapNotNull { result ->
			if (result.isSuccess) {
				val external = result.getOrThrow()
				Result.success(localTones + external)
			} else {
				result.exceptionOrNull()?.let { Result.failure(it) }
			}
		}
		emitAll(newFlow)
	}
}