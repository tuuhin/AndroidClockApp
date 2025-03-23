package com.eva.clockapp.features.alarms.domain.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class BasicTimerWatch {

	private val scope = CoroutineScope(Dispatchers.Default)

	private val _isRunning = MutableStateFlow(false)
	private val _timerDuration = MutableStateFlow(Duration.ZERO)

	private val _isCompleted = _isRunning.map { !it }

	init {
		_isRunning.flatMapLatest(::runTimerWatch)
			.onEach { duration ->
				val newDuration = _timerDuration.updateAndGet { duration }
				if (newDuration == Duration.ZERO) _isRunning.update { false }
			}.launchIn(scope)
	}

	fun start(time: Duration) {
		// reset it
		reset()
		// then start
		_timerDuration.update { time }
		// set running to true
		_isRunning.update { true }
	}

	private fun reset() {
		_isRunning.update { false }
		_timerDuration.update { Duration.ZERO }
	}

	fun runIfCompletedAsync(callback: () -> Unit) {
		_isCompleted.filter { result -> result == true }
			// make sure its true
			.onEach { callback() }
			.launchIn(scope)
	}

	fun cleanUp() = scope.cancel()

	private fun runTimerWatch(running: Boolean) = flow<Duration> {
		while (running) {
			val subtracted = _timerDuration.value - 1.seconds
			if (subtracted.isNegative() || subtracted == Duration.ZERO) {
				emit(Duration.ZERO)
				break
			}
			emit(subtracted)
			delay(1.seconds)
		}
	}.flowOn(Dispatchers.Default)
}