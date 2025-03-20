package com.eva.clockapp.features.alarms.domain.models

import kotlinx.datetime.LocalDate

data class GalleryImageModel(
	val id: Long,
	val title: String,
	val bucketId: Long,
	val uri: String,
	val dateModified: LocalDate? = null,
)
