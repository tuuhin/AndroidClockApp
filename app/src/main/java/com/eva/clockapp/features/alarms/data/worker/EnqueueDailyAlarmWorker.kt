package com.eva.clockapp.features.alarms.data.worker

import android.app.Notification
import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import android.util.Log
import androidx.work.Constraints
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
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.toJavaDuration

private const val TAG = "ALARM_RE_ENQUEUE_WORKER"

class EnqueueDailyAlarmWorker(
	context: Context,
	params: WorkerParameters,
	private val repository: AlarmsRepository,
	private val controller: AlarmsController,
) : CoroutineWorker(context, params) {

	override suspend fun doWork(): Result {

		Log.i(TAG, "WORKER RUNNING AT :${currentDateTime}")

		// show foreground info
		setForegroundAsync(rescheduleAlarmsForegroundInfo)

		return withContext(Dispatchers.IO) {
			try {
				val result = repository.getAllEnabledAlarms()
				if (result.isSuccess) {
					// get all the enabled alarms and re enqueue them
					val alarms = result.getOrThrow()
					val noOfAlarms = reEnqueueAlarms(alarms)
					Log.i(TAG, "NO OF ALARMS ENQUEUED $noOfAlarms")
					// work data
					val workData = workDataOf(
						AlarmWorkParams.WORK_RESULT_SUCCESS to AlarmWorkParams.WORK_RESULT_SUCCESS_MESSAGE,
					)
					Result.success(workData)
				} else {
					val exception = result.exceptionOrNull()
					val message = exception?.message ?: "Unknown error occurred"
					val workData = workDataOf(
						AlarmWorkParams.WORK_RESULT_FAILED to message,
					)
					Result.failure(workData)
				}
			} catch (e: Exception) {
				val message = e.message ?: "Unknown error occurred"
				val workData = workDataOf(
					AlarmWorkParams.WORK_RESULT_FAILED to message,
				)
				Result.failure(workData)
			}
		}
	}

	val rescheduleAlarmsForegroundInfo: ForegroundInfo
		get() {

			val title = applicationContext.getString(R.string.reschedule_alarm_for_next_day)
			val text = applicationContext.getString(R.string.reschedule_alarm_for_next_day_text)

			val notification = Notification.Builder(
				applicationContext,
				NotificationsConstants.CLOCK_EVENT_NOTIFICATION_CHANNEL_ID
			)
				.setSmallIcon(R.drawable.ic_upcoming_alarm)
				.setOngoing(true)
				.setContentTitle(title)
				.setContentText(text)
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
			// a second check to get only the enabled ones
			val operation = alarms.filter { it.isAlarmEnabled }
				.map { alarm -> async { controller.createAlarm(alarm) } }
			val res = operation.awaitAll()
			res.filter { it.isSuccess }.size
		}
	}

	companion object {

		// DO-NOT CHANGE THE NAME
		private const val UNIQUE_NAME = "DAILY_ALARMS_WORKER"

		val timeZone: TimeZone
			get() = TimeZone.currentSystemDefault()

		val currentDateTime: LocalDateTime
			get() = Clock.System.now().toLocalDateTime(timeZone)

		fun startPeriodicWorker(
			context: Context,
			policy: ExistingPeriodicWorkPolicy = ExistingPeriodicWorkPolicy.KEEP,
		) {

			val nextRescheduleTime = getClosestDateTime(currentDateTime)
			val initialDelay = nextRescheduleTime.toInstant(timeZone) - Clock.System.now()

			val periodicWorker = PeriodicWorkRequestBuilder<EnqueueDailyAlarmWorker>(
				repeatInterval = 6.hours.toJavaDuration(),
				flexTimeInterval = 15.minutes.toJavaDuration()
			)
				.setInitialDelay(duration = initialDelay.toJavaDuration())
				.addTag(AlarmWorkParams.TAG)
				.setConstraints(Constraints.NONE)
				.build()

			val workManager = WorkManager.getInstance(context)

			Log.d(TAG, "ALARMS TO BE SCHEDULED AT :$nextRescheduleTime AFTER:$initialDelay")
			workManager.enqueueUniquePeriodicWork(UNIQUE_NAME, policy, periodicWorker)
		}
	}
}

private fun getClosestDateTime(dateTime: LocalDateTime): LocalDateTime {
	val currentTime = dateTime.time
	val currentDate = dateTime.date

	val sixAM = LocalTime(6, 0)
	val twelvePM = LocalTime(12, 0)
	val sixPM = LocalTime(18, 0)
	val twelveAM = LocalTime(0, 0)

	return when {
		currentTime < sixAM -> LocalDateTime(currentDate, sixAM)
		currentTime < twelvePM -> LocalDateTime(currentDate, twelvePM)
		currentTime < sixPM -> LocalDateTime(currentDate, sixPM)
		else -> LocalDateTime(currentDate.plus(DatePeriod(days = 1)), twelveAM)
	}
}
