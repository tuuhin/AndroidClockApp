package com.eva.clockapp.features.alarms.data.recievers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class DeviceBootReceiver : BroadcastReceiver() {

	private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

	override fun onReceive(context: Context, intent: Intent) {

		val supportedActions = arrayOf(
			Intent.ACTION_BOOT_COMPLETED,
			Intent.ACTION_LOCKED_BOOT_COMPLETED,
		)
		if (intent.action !in supportedActions) return

		val pendingResult = goAsync()
		scope.launch {
			try {
				// TODO: Recreate the alarms
			} catch (e: Exception) {
				e.printStackTrace()
			} finally {
				// finish the pending result and cancel the scope
				pendingResult.finish()
				scope.cancel()
			}
		}
	}
}