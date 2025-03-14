package com.eva.clockapp.features.alarms.data.controllers

import android.content.Context
import com.eva.clockapp.core.utils.Resource
import com.eva.clockapp.features.alarms.domain.controllers.WallpaperOptions
import com.eva.clockapp.features.alarms.domain.controllers.WallpaperProvider
import com.eva.clockapp.features.alarms.domain.models.WallpaperPhoto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream

class WallpaperProviderImpl(private val context: Context) : WallpaperProvider {

	@Serializable
	private data class WallpaperResultDto(
		@SerialName("photos")
		val wallpapers: List<WallpaperPhotoDto> = emptyList(),
	)

	@Serializable
	private data class WallpaperPhotoDto(
		@SerialName("id") val id: Long = 0L,
		@SerialName("avg_color") val color: String = "",
		@SerialName("src") val uri: String = "",
	)

	@OptIn(ExperimentalSerializationApi::class)
	override fun loadWallpapers(): Flow<Resource<WallpaperOptions, Exception>> = flow {
		emit(Resource.Loading)
		try {
			val stream = context.assets.open("pexels_wallpapers.json")
			val results = stream.use { stream ->
				Json.decodeFromStream<WallpaperResultDto>(stream)
			}
			val models = results.wallpapers.map { dto ->
				WallpaperPhoto(
					id = dto.id,
					placeholderColor = hexToRgbaInt(dto.color),
					uri = dto.uri
				)
			}
			emit(Resource.Success(models))
		} catch (err: Exception) {
			err.printStackTrace()
			emit(Resource.Error(err))
		}
	}.flowOn(Dispatchers.IO)

	private fun hexToRgbaInt(hexColor: String): Int {
		var color = hexColor.replace("#", "")

		if (color.length == 6) {
			// Add full alpha if not present
			color = "FF$color"
		}

		return color.toLong(16).toInt()
	}
}