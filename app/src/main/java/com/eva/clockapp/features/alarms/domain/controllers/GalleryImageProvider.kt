package com.eva.clockapp.features.alarms.domain.controllers

import com.eva.clockapp.features.alarms.domain.models.GalleryBucketModel
import com.eva.clockapp.features.alarms.domain.models.GalleryImageModel
import kotlinx.coroutines.flow.Flow

interface GalleryImageProvider {

	val loadItemsAsFlow: Flow<Result<List<GalleryImageModel>>>

	suspend fun readImageAlbums(): Result<List<GalleryBucketModel>>
}