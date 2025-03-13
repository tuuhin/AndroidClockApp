package com.eva.clockapp.features.alarms.domain.controllers

import com.eva.clockapp.core.utils.Resource
import kotlinx.coroutines.flow.Flow

typealias WallpaperOptions = List<String>

fun interface WallpaperProvider {

	fun loadWallpapers(): Flow<Resource<WallpaperOptions, Exception>>
}