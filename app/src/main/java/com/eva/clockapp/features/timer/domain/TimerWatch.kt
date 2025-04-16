package com.eva.clockapp.features.timer.domain

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class TimerWatch(private val onFinishedCallback: () -> Unit = {}) {

	private val _scope = CoroutineScope(Dispatchers.Default)

	private val _currentState = MutableStateFlow(TimerWatchState.IDLE)
	private val _timerDuration = MutableStateFlow(Duration.ZERO)

	val timerState = _currentState.asStateFlow()
	val actualTimerDuration = _timerDuration.asStateFlow()

	val positiveTimerDuration = _timerDuration
		.filter { it >= Duration.ZERO }
		.stateIn(
			scope = _scope,
			started = SharingStarted.Eagerly,
			initialValue = Duration.ZERO
		)

	init {
		_currentState.flatMapLatest { state -> runTimerWatch(state == TimerWatchState.RUNNING) }
			.onEach { duration ->
				val newDuration = _timerDuration.updateAndGet { duration }
				if (newDuration < Duration.ZERO) {
					_currentState.update { TimerWatchState.FINISHED }
				}
			}
			.launchIn(_scope)

		_currentState.filter { state -> state == TimerWatchState.FINISHED }
			.onEach { onFinishedCallback() }
			.launchIn(_scope)
	}

	fun prepareAndPlay(duration: Duration) {
		_timerDuration.update { duration }
		_currentState.update { TimerWatchState.RUNNING }
	}

	fun pause() = _currentState.update { TimerWatchState.PAUSED }

	fun play() = _currentState.update { TimerWatchState.RUNNING }

	fun addExtraDuration(duration: Duration) = _timerDuration.update { duration + it }

	fun cleanUp() {
		_timerDuration.update { Duration.ZERO }
		_currentState.update { TimerWatchState.IDLE }
		_scope.cancel()
	}

	private fun runTimerWatch(isRunning: Boolean) = flow {
		while (isRunning) {
			delay(1.seconds)
			val reduced = _timerDuration.value - 1.seconds
			emit(reduced)
		}
	}
}