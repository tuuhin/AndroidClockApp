package com.eva.clockapp.features.alarms.data.worker

object AlarmWorkParams {
	const val WORK_RESULT_FAILED = "DAILY_ALARM_WORKER_WORK_RESULT_FAILED"
	const val WORK_RESULT_SUCCESS = "DAILY_ALARM_WORKER_WORK_RESULT_SUCCESS"
	const val WORK_RESULT_SUCCESS_MESSAGE = "ALL_ENABLED_ALARMS_ENQUEUED"
	const val WORK_RESULT_FAILED_INCOMPLETE = "WORKER_COULD_NOT_COMPLETE_THE_RESULT"


	// other work constants
	const val TAG = "ALARMS_WORKER_TAGS"
}