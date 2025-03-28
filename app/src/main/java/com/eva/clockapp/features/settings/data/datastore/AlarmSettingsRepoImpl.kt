package com.eva.clockapp.features.settings.data.datastore

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.eva.clockapp.features.settings.domain.models.AlarmSettingsModel
import com.eva.clockapp.features.settings.domain.models.AlarmVolumeControlOption
import com.eva.clockapp.features.settings.domain.models.StartOfWeekOptions
import com.eva.clockapp.features.settings.domain.models.TimeFormatOptions
import com.eva.clockapp.features.settings.domain.models.UpcomingAlarmTimeOption
import com.eva.clockapp.features.settings.domain.repository.AlarmSettingsRepository
import com.eva.clockapp.features.settings.proto.AlarmSettingsProto
import com.eva.clockapp.features.settings.proto.StartOfWeekProto
import com.eva.clockapp.features.settings.proto.TimeFormatProto
import com.eva.clockapp.features.settings.proto.UpcomingNotificationTimeProto
import com.eva.clockapp.features.settings.proto.VolumeButtonControlProto
import com.eva.clockapp.features.settings.proto.alarmSettingsProto
import com.google.protobuf.InvalidProtocolBufferException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import java.io.InputStream
import java.io.OutputStream

class AlarmSettingsRepoImpl(private val context: Context) : AlarmSettingsRepository {

	override val settingsFlow: Flow<AlarmSettingsModel>
		get() = context.alarmSettings.data.map { it.toModel() }

	override val settingsValue: AlarmSettingsModel
		get() = runBlocking { settingsFlow.first() }

	override suspend fun onStartOfWeekChange(startOfWeek: StartOfWeekOptions) {
		context.alarmSettings.updateData { settings ->
			settings.toBuilder()
				.setStartOfWeek(startOfWeek.toProto)
				.build()
		}
	}

	override suspend fun onTimeFormatChange(timeFormat: TimeFormatOptions) {
		context.alarmSettings.updateData { settings ->
			settings.toBuilder()
				.setTimeFormat(timeFormat.toProto)
				.build()
		}
	}

	override suspend fun onVolumeControlChange(control: AlarmVolumeControlOption) {
		context.alarmSettings.updateData { settings ->
			settings.toBuilder()
				.setVolumeControl(control.toProto)
				.build()
		}
	}

	override suspend fun onUpcomingNotificationTimeChange(time: UpcomingAlarmTimeOption) {
		context.alarmSettings.updateData { settings ->
			settings.toBuilder()
				.setUpcomingAlarm(time.toProto)
				.build()
		}
	}
}


private val Context.alarmSettings: DataStore<AlarmSettingsProto> by dataStore(
	fileName = DataStoreConstants.ALARM_SETTINGS_FILE_NAME,
	serializer = object : Serializer<AlarmSettingsProto> {

		override val defaultValue: AlarmSettingsProto = alarmSettingsProto {
			timeFormat = TimeFormatProto.TIME_FORMAT_SYSTEM_DEFAULT
			startOfWeek = StartOfWeekProto.START_OF_WEEK_SYSTEM_DEFAULT
			upcomingAlarm = UpcomingNotificationTimeProto.DURATION_30_MINUTES
			volumeControl = VolumeButtonControlProto.SNOOZE_ALARM
		}

		override suspend fun readFrom(input: InputStream): AlarmSettingsProto {
			try {
				return AlarmSettingsProto.parseFrom(input)
			} catch (exception: InvalidProtocolBufferException) {
				throw CorruptionException("Cannot read .proto file", exception)
			}
		}

		override suspend fun writeTo(t: AlarmSettingsProto, output: OutputStream) =
			t.writeTo(output)
	}
)