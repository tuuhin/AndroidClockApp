package com.eva.clockapp.features.alarms.data.providers

import android.content.Context
import android.media.RingtoneManager
import androidx.annotation.RawRes
import androidx.core.net.toUri
import com.eva.clockapp.R
import com.eva.clockapp.features.alarms.domain.controllers.AppRingtoneProvider
import com.eva.clockapp.features.alarms.domain.models.RingtoneMusicFile

class AppRingtoneProviderImpl(private val context: Context) : AppRingtoneProvider {

	private fun toResourceUri(@RawRes resourceId: Int) =
		"android.resource://${context.packageName}/$resourceId".toUri()

	override val default: RingtoneMusicFile
		get() {
			val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
			return RingtoneMusicFile(
				name = context.getString(R.string.alarm_sound_system_default),
				uri = uri.toString(),
				type = RingtoneMusicFile.RingtoneType.APPLICATION_LOCAL
			)
		}

	override val ringtones: Result<List<RingtoneMusicFile>>
		get() = try {
			val listOfAlarms = buildList {
				add(default)
				add(buildRingtone(name = "Simple", audio = R.raw.alarm_clock_simple))
				add(buildRingtone(name = "Clock", audio = R.raw.alarm_clock_sound))
				add(buildRingtone(name = "Bird Sound", audio = R.raw.birds_alarm))
				add(buildRingtone(name = "Whistle", audio = R.raw.happy_whistle))
			}
			Result.success(listOfAlarms)
		} catch (e: Exception) {
			e.printStackTrace()
			Result.failure(e)
		}

	private fun buildRingtone(name: String, @RawRes audio: Int) = RingtoneMusicFile(
		name = name,
		uri = toResourceUri(audio).toString(),
		type = RingtoneMusicFile.RingtoneType.APPLICATION_LOCAL
	)
}

