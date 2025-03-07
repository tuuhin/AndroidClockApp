package com.eva.clockapp.features.alarms.data.controllers

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
			val simple = RingtoneMusicFile(
				name = "Simple",
				uri = toResourceUri(R.raw.alarm_clock_simple).toString(),
				type = RingtoneMusicFile.RingtoneType.APPLICATION_LOCAL
			)

			val clock = RingtoneMusicFile(
				name = "Clock",
				uri = toResourceUri(R.raw.alarm_clock_sound).toString(),
				type = RingtoneMusicFile.RingtoneType.APPLICATION_LOCAL
			)
			val list = listOf(default, simple, clock)
			Result.success(list)
		} catch (e: Exception) {
			e.printStackTrace()
			Result.failure(e)
		}
}

