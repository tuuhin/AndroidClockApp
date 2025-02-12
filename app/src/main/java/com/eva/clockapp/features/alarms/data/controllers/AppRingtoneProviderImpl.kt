package com.eva.clockapp.features.alarms.data.controllers

import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import androidx.annotation.RawRes
import com.eva.clockapp.R
import com.eva.clockapp.features.alarms.domain.controllers.AppRingtoneProvider
import com.eva.clockapp.features.alarms.domain.models.RingtoneMusicFile

class AppRingtoneProviderImpl(private val context: Context) : AppRingtoneProvider {

	private fun toResourceUri(@RawRes resourceId: Int) =
		Uri.parse("android.resource://${context.packageName}/$resourceId")

	override val default: RingtoneMusicFile
		get() {
			val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
			return RingtoneMusicFile(
				name = context.getString(R.string.alarm_sound_system_default),
				uri = uri.toString(),
			)
		}

	override val ringtones: Result<List<RingtoneMusicFile>>
		get() = try {
			val simple = RingtoneMusicFile(
				name = "Simple",
				uri = toResourceUri(R.raw.alarm_clock_simple).toString(),
			)

			val clock = RingtoneMusicFile(
				name = "Clock",
				uri = toResourceUri(R.raw.alarm_clock_sound).toString(),
			)
			val list = listOf(default, simple, clock)
			Result.success(list)
		} catch (e: Exception) {
			e.printStackTrace()
			Result.failure(e)
		}
}

