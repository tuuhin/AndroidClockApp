package com.eva.clockapp.features.alarms.data.providers

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.os.bundleOf
import com.eva.clockapp.core.utils.checkImageReadPermission
import com.eva.clockapp.features.alarms.domain.controllers.GalleryImageProvider
import com.eva.clockapp.features.alarms.domain.exceptions.FileReadPermissionNotFound
import com.eva.clockapp.features.alarms.domain.models.GalleryBucketModel
import com.eva.clockapp.features.alarms.domain.models.GalleryImageModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.io.File

class GalleryImageProviderImpl(private val context: Context) : GalleryImageProvider {

	private val hasPermission: Boolean
		get() = context.checkImageReadPermission

	private val volume: Uri
		get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
			MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
		else MediaStore.Images.Media.EXTERNAL_CONTENT_URI


	override val loadItemsAsFlow: Flow<Result<List<GalleryImageModel>>>
		get() = callbackFlow {

			// load the recordings initially
			launch(Dispatchers.IO) { send(loadGalleryItems()) }

			val observer = object : ContentObserver(null) {
				override fun onChange(selfChange: Boolean) {
					// on any change load the recordings again
					launch(Dispatchers.IO) { send(loadGalleryItems()) }
				}
			}
			// add content observer for the volume
			context.contentResolver.registerContentObserver(volume, true, observer)
			awaitClose {
				// remove the content observer
				context.contentResolver.unregisterContentObserver(observer)
			}
		}

	override suspend fun readImageAlbums(): Result<List<GalleryBucketModel>> {
		if (!hasPermission) return Result.failure(FileReadPermissionNotFound())

		return try {
			withContext(Dispatchers.IO) {
				val contentResolverResult = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
					val projections = arrayOf(
						MediaStore.Images.ImageColumns.BUCKET_ID,
						MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
					)
					val selectionArgs = bundleOf(
						ContentResolver.QUERY_ARG_SQL_GROUP_BY to MediaStore.Images.ImageColumns.BUCKET_ID,
						ContentResolver.QUERY_ARG_SORT_COLUMNS to arrayOf(MediaStore.Images.ImageColumns.BUCKET_ID),
						ContentResolver.QUERY_ARG_SORT_DIRECTION to ContentResolver.QUERY_SORT_DIRECTION_ASCENDING
					)
					context.contentResolver.query(volume, projections, selectionArgs, null)
				} else {
					val projections = arrayOf(
						"DISTINCT " + MediaStore.Images.ImageColumns.BUCKET_ID,
						MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
					)
					context.contentResolver.query(volume, projections, null, null, null)
				}
				contentResolverResult?.use { cursor ->
					Result.success(readImageBucketsFromCursor(cursor))
				} ?: Result.failure(CannotAccessContentResolverException())
			}
		} catch (e: Exception) {
			Result.failure(e)
		}
	}

	private suspend fun loadGalleryItems(): Result<List<GalleryImageModel>> {

		// TODO: Add width and height if needed
		if (!hasPermission) return Result.failure(FileReadPermissionNotFound())

		val projection = arrayOf(
			MediaStore.Images.ImageColumns._ID,
			MediaStore.Images.ImageColumns.TITLE,
			MediaStore.Images.ImageColumns.BUCKET_ID,
			MediaStore.Images.ImageColumns.DATE_MODIFIED,
			MediaStore.Images.ImageColumns.DATA
		)

		val queryArgs = bundleOf(
			ContentResolver.QUERY_ARG_SORT_COLUMNS to arrayOf(MediaStore.Images.ImageColumns.DATE_MODIFIED),
			ContentResolver.QUERY_ARG_SORT_DIRECTION to ContentResolver.QUERY_SORT_DIRECTION_DESCENDING
		)
		return try {
			withContext(Dispatchers.IO) {
				context.contentResolver.query(volume, projection, queryArgs, null)
					?.use { cursor ->
						Result.success(readImagesFromCursor(cursor))
					} ?: Result.failure(CannotAccessContentResolverException())
			}
		} catch (e: Exception) {
			Result.failure(e)
		}
	}


	private suspend fun readAlbumThumbnail(albumId: Long): String? {
		if (!hasPermission) return null

		val projection = arrayOf(
			MediaStore.Images.ImageColumns._ID,
			MediaStore.Images.ImageColumns.DATA
		)

		val selection = buildString {
			append(MediaStore.Images.ImageColumns.BUCKET_ID)
			append(" = ? ")
		}
		val selectionArgs = arrayOf("$albumId")

		val queryArgs = bundleOf(
			ContentResolver.QUERY_ARG_SQL_SELECTION to selection,
			ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS to selectionArgs,
			ContentResolver.QUERY_ARG_SORT_COLUMNS to arrayOf(MediaStore.Images.ImageColumns.DATE_MODIFIED),
			ContentResolver.QUERY_ARG_SORT_DIRECTION to ContentResolver.QUERY_SORT_DIRECTION_DESCENDING
		)
		return try {
			withContext(Dispatchers.IO) {
				context.contentResolver.query(volume, projection, queryArgs, null)
					?.use { cursor -> readImageUriFromCursor(cursor) }
			}
		} catch (_: Exception) {
			null
		}
	}

	private suspend fun readImageBucketsFromCursor(cursor: Cursor): List<GalleryBucketModel> {
		val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.BUCKET_ID)
		val nameColumn =
			cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME)

		return buildList {
			while (cursor.moveToNext()) {
				val id = cursor.getLong(idColumn)
				val bucketName = cursor.getString(nameColumn)
				val imageUri = readAlbumThumbnail(id)

				val image = GalleryBucketModel(
					bucketId = id,
					bucketName = bucketName,
					thumbnail = imageUri
				)
				add(image)
			}
		}
	}

	private fun readImageUriFromCursor(cursor: Cursor): String? {
		val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns._ID)
		val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA)

		if (cursor.moveToFirst()) {
			val id = cursor.getLong(idColumn)
			val data = cursor.getString(dataColumn)
			if (File(data).exists()) {
				return ContentUris.withAppendedId(volume, id).toString()
			}
		}
		return null
	}


	private fun readImagesFromCursor(cursor: Cursor): List<GalleryImageModel> {
		val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns._ID)
		val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.TITLE)
		val bucketIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.BUCKET_ID)
		val dateModifiedColumn =
			cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATE_MODIFIED)
		val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.DATA)

		return buildList {
			while (cursor.moveToNext()) {
				val id = cursor.getLong(idColumn)
				val title = cursor.getString(nameColumn)
				val bucketId = cursor.getLong(bucketIdColumn)
				val uri = ContentUris.withAppendedId(volume, id).toString()
				val modifiedOn = cursor.getLong(dateModifiedColumn)
				val data = cursor.getString(dataColumn)

				if (File(data).exists()) {
					val date = if (modifiedOn == 0L) null
					else Instant.fromEpochSeconds(modifiedOn)
						.toLocalDateTime(TimeZone.currentSystemDefault())
						.date

					val image = GalleryImageModel(
						id = id,
						title = title,
						bucketId = bucketId,
						uri = uri,
						dateModified = date
					)
					add(image)
				}
			}
		}
	}
}