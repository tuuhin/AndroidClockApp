package com.eva.clockapp.features.alarms.data.controllers

import android.content.Context
import com.eva.clockapp.core.utils.Resource
import com.eva.clockapp.features.alarms.domain.controllers.WallpaperOptions
import com.eva.clockapp.features.alarms.domain.controllers.WallpaperProvider
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
	private data class WallpaperResult(
		@SerialName("wallpapers")
		val wallpapers: List<String> = emptyList(),
	)

	@OptIn(ExperimentalSerializationApi::class)
	override fun loadWallpapers(): Flow<Resource<WallpaperOptions, Exception>> = flow {
		emit(Resource.Loading)
		try {
			val stream = context.assets.open("pexels_wallpapers.json")
			val results = stream.use {
				Json.decodeFromStream<WallpaperResult>(it)
			}
			emit(Resource.Success(results.wallpapers))
		} catch (err: Exception) {
			err.printStackTrace()
			emit(Resource.Error(err))
		}
	}.flowOn(Dispatchers.IO)
}