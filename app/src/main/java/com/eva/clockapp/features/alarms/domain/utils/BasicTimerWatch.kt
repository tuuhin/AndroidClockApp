package com.eva.clockapp.features.alarms.domain.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class BasicTimerWatch {

	private val scope = CoroutineScope(Dispatchers.Default)

	private val _isRunning = MutableStateFlow(false)
	private val _timerDuration = MutableStateFlow(Duration.ZERO)

	private val _isCompleted = MutableStateFlow(false)
	val isCompleted = _isCompleted.asStateFlow()

	init {
		_isRunning.flatMapLatest(::runTimerWatch)
			.onEach { duration ->
				if (duration == Duration.ZERO) _isCompleted.update { true }
				_timerDuration.update { duration }
			}.launchIn(scope)
	}

	fun start(time: Duration) {
		_timerDuration.update { time }
		_isCompleted.update { false }
		// set running to true
		_isRunning.update { true }
	}

	fun stop() {
		_isRunning.update { false }
		_timerDuration.update { Duration.ZERO }
	}


	fun cleanUp() = scope.cancel()

	private fun runTimerWatch(running: Boolean) = flow<Duration> {
		while (running) {
			val subtracted = _timerDuration.value - 1.seconds
			if (subtracted.isNegative()) {
				emit(Duration.ZERO)
				break
			}
			emit(subtracted)
			delay(1.seconds)
		}
	}.flowOn(Dispatchers.Default)
}