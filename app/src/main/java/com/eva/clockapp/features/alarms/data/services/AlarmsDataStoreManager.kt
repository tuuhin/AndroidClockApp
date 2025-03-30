package com.eva.clockapp.features.alarms.data.services

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

private val Context.snoozeController by preferencesDataStore("alarms_datastore_manager")

class AlarmsDataStoreManager(private val context: Context) {

	suspend fun getSnoozeCount(): Int {
		return context.snoozeController.data.map { it[SNOOZE_COUNT_KEY] ?: 0 }
			.flowOn(Dispatchers.IO)
			.first()
	}

	suspend fun setSnoozeCount(count: Int = 0) {
		withContext(Dispatchers.IO) {
			context.snoozeController.edit { prefs -> prefs[SNOOZE_COUNT_KEY] = count }
		}
	}

	companion object {
		private val SNOOZE_COUNT_KEY = intPreferencesKey("SNOOZE_COUNT_KEY")
	}
}