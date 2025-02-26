package com.eva.clockapp.features.alarms.data.controllers

import android.content.Context
import android.media.AudioAttributes
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.net.toUri
import com.eva.clockapp.features.alarms.domain.controllers.AlarmsSoundPlayer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.update

private const val TAG = "ALARM_SOUND_PLAYER"

class AlarmSoundPlayerImpl(private val context: Context) : AlarmsSoundPlayer {

	private var ringtone: Ringtone? = null
	private var _checkTheLoop = MutableStateFlow(false)

	@OptIn(ExperimentalCoroutinesApi::class)
	override val isPlaying: Flow<Boolean>
		get() = _checkTheLoop.flatMapLatest(::checkIfRingtonePlaying)
			.distinctUntilChanged()

	override fun playSound(musicUri: String, soundVolume: Float, loop: Boolean): Result<Unit> {
		return try {
			val uri = musicUri.toUri()
			// stop the ringtone if its being played
			ringtone?.stop()
			_checkTheLoop.update { false }
			Log.d(TAG, "STOPPING THE PLAYER")
			// set up the ringtone
			ringtone = RingtoneManager.getRingtone(context, uri)?.apply {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
					volume = soundVolume / 100f
					isLooping = loop
				}
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
					isHapticGeneratorEnabled = false
				}
				audioAttributes = AudioAttributes.Builder()
					.setUsage(AudioAttributes.USAGE_ALARM)
					.setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
					.build()

			}
			// play the ringtone
			ringtone?.play()
			_checkTheLoop.update { true }
			Log.d(TAG, "STARTING PLAY SOUND FOR :$uri :LOOPING:$loop :VOLUME:$soundVolume")
			// success
			Result.success(Unit)
		} catch (e: Exception) {
			e.printStackTrace()
			// some error
			Result.failure(e)
		}
	}

	override fun changeVolume(soundVolume: Float): Result<Boolean> {
		return try {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
				ringtone?.volume = soundVolume / 100f
				Log.d(TAG, "SOUND VOLUME SET TO :${ringtone?.volume ?: 0}")
				return Result.success(true)
			}
			Log.d(TAG, "CANNOT CHANGE VOLUME API NOT SUPPORTED")
			Result.success(false)
		} catch (e: Exception) {
			e.printStackTrace()
			Result.failure(e)
		}
	}

	override fun stopSound() {
		ringtone?.stop()
		ringtone = null
		_checkTheLoop.update { false }
		Log.d(TAG, "CLEANUP AND STOP SOUND")
	}

	private fun checkIfRingtonePlaying(shouldPoll: Boolean) = flow {
		while (shouldPoll) {
			val isPLaying = ringtone?.isPlaying ?: false
			emit(isPLaying)
			delay(100)
		}
	}.flowOn(Dispatchers.Default)
}