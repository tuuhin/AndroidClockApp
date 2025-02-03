package com.eva.clockapp.features.alarms.data.controllers

import android.Manifest
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.core.os.bundleOf
import com.eva.clockapp.features.alarms.domain.controllers.RingtoneProvider
import com.eva.clockapp.features.alarms.domain.exceptions.FileReadPermissionNotFound
import com.eva.clockapp.features.alarms.domain.exceptions.NoMatchingIdInDatabaseException
import com.eva.clockapp.features.alarms.domain.models.RingtoneMusicFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.seconds

class RingtoneProviderImpl(private val context: Context) : RingtoneProvider {

	private val checkPermission: Boolean
		get() {
			val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
				Manifest.permission.READ_MEDIA_AUDIO
			else Manifest.permission.READ_EXTERNAL_STORAGE

			return ContextCompat.checkSelfPermission(context, permission) ==
					PermissionChecker.PERMISSION_GRANTED
		}

	private val volume: Uri
		get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
			MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
		else MediaStore.Audio.Media.EXTERNAL_CONTENT_URI


	override suspend fun loadRingtones(): Result<List<RingtoneMusicFile>> {

		if (!checkPermission) return Result.failure(FileReadPermissionNotFound())

		val projection = arrayOf(
			MediaStore.Audio.AudioColumns._ID,
			MediaStore.Audio.AudioColumns.DISPLAY_NAME,
			MediaStore.Audio.AudioColumns.DURATION,
		)

		val selection = buildString {
			append(MediaStore.Audio.AudioColumns.IS_ALARM)
			append(" = ? ")
			append(" OR ")
			append(MediaStore.Audio.AudioColumns.IS_RINGTONE)
		}
		val selectionArgs = arrayOf("1", "1")
		val queryArgs = bundleOf(
			// items only of this package

			ContentResolver.QUERY_ARG_SQL_SELECTION to selection,
			ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS to selectionArgs,

			ContentResolver.QUERY_ARG_SORT_COLUMNS to arrayOf(MediaStore.Audio.Media.DATE_MODIFIED),
			ContentResolver.QUERY_ARG_SORT_DIRECTION to ContentResolver.QUERY_SORT_DIRECTION_DESCENDING
		)
		return try {
			withContext(Dispatchers.IO) {
				context.contentResolver.query(volume, projection, queryArgs, null)
					?.use { cursor ->
						Result.success(readRingtonesFromCursor(cursor))
					} ?: Result.failure(CannotAccessContentResolverException())
			}
		} catch (e: Exception) {
			Result.failure(e)
		}
	}

	override suspend fun getRingtoneFromId(id: Long): Result<RingtoneMusicFile> {
		if (!checkPermission) return Result.failure(FileReadPermissionNotFound())

		val projection = arrayOf(
			MediaStore.Audio.AudioColumns._ID,
			MediaStore.Audio.AudioColumns.DISPLAY_NAME,
			MediaStore.Audio.AudioColumns.DURATION,
		)

		val selection = buildString {
			append(MediaStore.Audio.AudioColumns._ID)
			append(" = ? ")
		}
		val selectionArgs = arrayOf("$id")
		val queryArgs = bundleOf(
			// items only of this package

			ContentResolver.QUERY_ARG_SQL_SELECTION to selection,
			ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS to selectionArgs,

			ContentResolver.QUERY_ARG_SORT_COLUMNS to arrayOf(MediaStore.Audio.Media.DATE_MODIFIED),
			ContentResolver.QUERY_ARG_SORT_DIRECTION to ContentResolver.QUERY_SORT_DIRECTION_DESCENDING
		)
		return try {
			withContext(Dispatchers.IO) {
				context.contentResolver.query(volume, projection, queryArgs, null)
					?.use { cursor ->
						val results = readRingtonesFromCursor(cursor).firstOrNull()
							?: return@withContext Result.failure(NoMatchingIdInDatabaseException())
						Result.success(results)
					} ?: Result.failure(CannotAccessContentResolverException())
			}
		} catch (e: Exception) {
			Result.failure(e)
		}
	}


	private fun readRingtonesFromCursor(cursor: Cursor): List<RingtoneMusicFile> {
		val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns._ID)
		val displayNameColumn =
			cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DISPLAY_NAME)
		val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DURATION)

		return buildList {
			while (cursor.moveToNext()) {
				val id = cursor.getLong(idColumn)
				val name = cursor.getString(displayNameColumn)
				val duration = cursor.getLong(durationColumn)

				val ringtone = RingtoneMusicFile(
					name = name,
					uri = ContentUris.withAppendedId(volume, id).toString(),
					duration = duration.seconds
				)
				add(ringtone)
			}
		}
	}

	private class CannotAccessContentResolverException :
		Exception("Cannot get the underlying content-resolver")

}