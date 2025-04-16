package com.eva.clockapp.features.timer.domain

import app.cash.turbine.turbineScope
import com.eva.clockapp.rules.MainDispatchersRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class TimerWatchTest {

	@get:Rule
	val testDispatcher = MainDispatchersRule()

	private lateinit var timerWatch: TimerWatch

	@Before
	fun setUp() {
		timerWatch = TimerWatch()
	}

	@After
	fun tearDown() = timerWatch.cleanUp()

	@Test
	fun `start the timer play and pause to  check if state changes`() = runTest {
		turbineScope(timeout = 10.seconds) {
			val timerState = timerWatch.timerState.testIn(this)

			assertEquals(TimerWatchState.IDLE, timerState.awaitItem())

			// start the timer
			timerWatch.prepareAndPlay(5.seconds)
			advanceTimeBy(1.seconds)
			assertEquals(TimerWatchState.RUNNING, timerState.awaitItem())

			// stop the timer
			timerWatch.pause()
			advanceUntilIdle()
			assertEquals(TimerWatchState.PAUSED, timerState.awaitItem())

			// start the timer and wait it to finish
			timerWatch.play()
			advanceTimeBy(1.seconds)
			assertEquals(TimerWatchState.RUNNING, timerState.awaitItem())

			// finish the timer
			advanceTimeBy(5.seconds)
			assertEquals(TimerWatchState.FINISHED, timerState.awaitItem())

			timerState.cancelAndIgnoreRemainingEvents()
		}
	}

	@Test
	fun `check the running clock timings`() = runTest {
		turbineScope(timeout = 10.seconds) {
			val timerDuration = 30.seconds
			val durationState = timerWatch.actualTimerDuration.testIn(this)

			// initially it should be zero
			assertEquals(Duration.ZERO, durationState.awaitItem())

			// start the timer
			timerWatch.prepareAndPlay(timerDuration)
			assertEquals(timerDuration, durationState.awaitItem())

			advanceTimeBy(5.seconds)
			durationState.skipItems(4)

			val durationAfter5Sec = durationState.awaitItem()
			assertEquals(timerDuration - 5.seconds, durationAfter5Sec)

			// pause the timer
			timerWatch.pause()
			advanceTimeBy(2.seconds)
			// now check the time
			assertEquals(timerDuration - 5.seconds, durationAfter5Sec)

			//again play the timer
			timerWatch.play()
			val durationAfter6Sec = durationState.awaitItem()
			assertEquals(timerDuration - 6.seconds, durationAfter6Sec)

			advanceTimeBy(20.seconds)
			durationState.skipItems(19)

			val durationAfter20MoreSeconds = durationState.awaitItem()
			assertEquals(timerDuration - 26.seconds, durationAfter20MoreSeconds)

			durationState.cancelAndIgnoreRemainingEvents()
		}
	}
}