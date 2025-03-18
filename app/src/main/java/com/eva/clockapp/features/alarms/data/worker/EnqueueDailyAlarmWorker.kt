package com.eva.clockapp.features.alarms.data.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.eva.clockapp.features.alarms.domain.controllers.AlarmsController
import com.eva.clockapp.features.alarms.domain.models.AlarmsModel
import com.eva.clockapp.features.alarms.domain.repository.AlarmsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toKotlinLocalDateTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.LocalDateTime
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration

private const val TAG = "DAILY_ALARM_WORKER"

class EnqueueDailyAlarmWorker(
	context: Context,
	params: WorkerParameters,
) : CoroutineWorker(context, params), KoinComponent {

	val repository by inject<AlarmsRepository>()
	val controller by inject<AlarmsController>()

	override suspend fun doWork(): Result {
		return withContext(Dispatchers.IO) {
			try {
				val resource = repository.getAllAlarms()
				resource.fold(
					onSuccess = { alarms ->
						val enabledAlarms = alarms.filter { it.isAlarmEnabled }

						createAlarms(enabledAlarms)
						// work data
						val workData = workDataOf(
							AlarmWorkParams.WORK_RESULT_SUCCESS to
									AlarmWorkParams.WORK_RESULT_SUCCESS_MESSAGE
						)
						return@withContext Result.success(workData)
					},
				)
				val workData = workDataOf(
					AlarmWorkParams.WORK_RESULT_FAILED to
							AlarmWorkParams.WORK_RESULT_FAILED_INCOMPLETE
				)
				Result.success(workData)
			} catch (e: Exception) {
				val message = e.message ?: "Unknown error occurred"
				Result.failure(workDataOf(AlarmWorkParams.WORK_RESULT_FAILED to message))
			}
		}
	}

	private suspend fun createAlarms(alarms: List<AlarmsModel>) {
		supervisorScope {
			val operation = alarms.filter { it.isAlarmEnabled }
				.map { alarm -> async(Dispatchers.Main) { controller.createAlarm(alarm) } }
			val res = operation.awaitAll()
			val numberOfAlarmsAdded = res.filter { it.isSuccess }.size

			Log.i(TAG, "NO OF ALARMS ENQUEUED $numberOfAlarmsAdded")
		}
	}

	companion object {

		private const val UNIQUE_NAME = "DAILY_ALARM_ENQUEUE_WORKER"

		val currentDate = LocalDateTime.now().toKotlinLocalDateTime()

		val tomorrowMidnight = currentDate.date.plus(DatePeriod(days = 1))
			.atTime(LocalTime(0, 0))
			.toInstant(TimeZone.currentSystemDefault())

		val durationDifference =
			tomorrowMidnight - currentDate.toInstant(TimeZone.currentSystemDefault())

		val periodicWorker = PeriodicWorkRequestBuilder<EnqueueDailyAlarmWorker>(
			repeatInterval = 1.days.toJavaDuration(),
			flexTimeInterval = 5.minutes.toJavaDuration()
		)
			.setInitialDelay(durationDifference.toJavaDuration())
			.build()

		fun startWorker(context: Context) {
			val workManager = WorkManager.getInstance(context)

			Log.d(TAG,"WORK REQUEST ENQUEUED, WILL RUN AFTER :$durationDifference")

			workManager.enqueueUniquePeriodicWork(
				UNIQUE_NAME,
				ExistingPeriodicWorkPolicy.KEEP,
				periodicWorker
			)
		}

		fun cancelWorker(context: Context) {
			val workManager = WorkManager.getInstance(context)
			workManager.cancelUniqueWork(UNIQUE_NAME)
		}
	}
}