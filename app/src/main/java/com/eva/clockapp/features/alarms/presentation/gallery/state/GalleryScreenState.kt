package com.eva.clockapp.features.alarms.presentation.gallery.state

import com.eva.clockapp.features.alarms.domain.models.GalleryBucketModel
import com.eva.clockapp.features.alarms.domain.models.GalleryImageModel
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class GalleryScreenState(
	val allImages: ImmutableList<GalleryImageModel> = persistentListOf(),
	val buckets: ImmutableList<GalleryBucketModel> = persistentListOf(),
	val selectedBucket: GalleryBucketModel? = null,
	val results: ImmutableList<GalleryImageModel> = persistentListOf(),
) {
	val showSheet: Boolean
		get() = selectedBucket != null
}
