package com.eva.clockapp.features.alarms.domain.controllers

import com.eva.clockapp.core.utils.Resource
import com.eva.clockapp.features.alarms.domain.models.WallpaperPhoto
import kotlinx.coroutines.flow.Flow

typealias WallpaperOptions = List<WallpaperPhoto>

fun interface WallpaperProvider {

	fun loadWallpapers(): Flow<Resource<WallpaperOptions, Exception>>
}