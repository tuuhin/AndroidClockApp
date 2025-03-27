package com.eva.clockapp.features.alarms.data.worker

import android.app.Notification
import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ForegroundInfo
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.eva.clockapp.R
import com.eva.clockapp.core.constants.NotificationsConstants
import com.eva.clockapp.features.alarms.domain.controllers.AlarmsController
import com.eva.clockapp.features.alarms.domain.models.AlarmsModel
import com.eva.clockapp.features.alarms.domain.repository.AlarmsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toKotlinLocalDateTime
import kotlinx.datetime.toLocalDateTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.time.Duration.Companion.days
import kotlin.time.toJavaDuration

private const val TAG = "ALARM_RE_ENQUEUE_WORKER"

class EnqueueDailyAlarmWorker(
	context: Context,
	params: WorkerParameters,
) : CoroutineWorker(context, params), KoinComponent {

	val repository by inject<AlarmsRepository>()
	val controller by inject<AlarmsController>()

	override suspend fun doWork(): Result {

		val datetime = LocalDateTime.now()
		Log.i(TAG, "ENQUEUE ALARMS AGAIN AT :${datetime.toKotlinLocalDateTime()}")

		// show foreground info
		setForegroundAsync(rescheduleAlarmsForegroundInfo)

		return withContext(Dispatchers.IO) {
			try {
				val resource = repository.getAllAlarms()
				resource.fold(
					onSuccess = { alarms ->
						val enabledAlarms = alarms.filter { it.isAlarmEnabled }

						val noOfAlarms = reEnqueueAlarms(enabledAlarms)
						Log.i(TAG, "NO OF ALARMS ENQUEUED $noOfAlarms")

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

	val rescheduleAlarmsForegroundInfo: ForegroundInfo
		get() {

			val title = applicationContext
				.getString(R.string.reschedule_alarm_for_next_day)

			val notification = Notification.Builder(
				applicationContext,
				NotificationsConstants.CLOCK_EVENT_NOTIFICATION_CHANNEL_ID
			)
				.setSmallIcon(R.drawable.ic_upcoming_alarm)
				.setContentTitle(title)
				.build()

			return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
				ForegroundInfo(
					NotificationsConstants.ALARMS_SYNC_FOREGROUND_NOTIFICATION_ID,
					notification,
					ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC,
				)
			} else ForegroundInfo(
				NotificationsConstants.ALARMS_SYNC_FOREGROUND_NOTIFICATION_ID,
				notification
			)
		}


	private suspend fun reEnqueueAlarms(alarms: List<AlarmsModel>): Int {
		return supervisorScope {
			val operation = alarms.filter { it.isAlarmEnabled }
				.map { alarm -> async { controller.createAlarm(alarm) } }
			val res = operation.awaitAll()
			res.filter { it.isSuccess }.size
		}
	}

	companion object {

		// DO-NOT CHANGE THE NAME
		private const val UNIQUE_NAME = "ALARM_RE_ENQUEUE_WORKER"

		fun startWorker(
			context: Context,
			policy: ExistingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.KEEP,
		) {

			val currentDateTime = Clock.System.now()
				.toLocalDateTime(TimeZone.currentSystemDefault())

			val tomorrowMidnight = currentDateTime.date.plus(DatePeriod(days = 1))
				.atTime(LocalTime(0, 0))
				.toInstant(TimeZone.currentSystemDefault())

			val durationDifference =
				tomorrowMidnight - currentDateTime.toInstant(TimeZone.currentSystemDefault())

			val periodicWorker = PeriodicWorkRequestBuilder<EnqueueDailyAlarmWorker>(
				repeatInterval = 1.days.toJavaDuration(),
			)
				.setInitialDelay(durationDifference.toJavaDuration())
				.build()


			val startTime = LocalDateTime.now()
				.plus(durationDifference.inWholeMilliseconds, ChronoUnit.MILLIS)
				.toKotlinLocalDateTime()

			Log.d(TAG, "WORK REQUEST ENQUEUED, WILL RUN AT :$startTime")

			val workManager = WorkManager.getInstance(context)

			workManager.enqueueUniquePeriodicWork(UNIQUE_NAME, policy, periodicWorker)
		}

		fun cancelWorker(context: Context) {
			val workManager = WorkManager.getInstance(context)
			workManager.cancelUniqueWork(UNIQUE_NAME)
		}
	}
}