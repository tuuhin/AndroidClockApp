package com.eva.clockapp.features.alarms.presentation.gallery.state

import com.eva.clockapp.features.alarms.domain.models.GalleryBucketModel

sealed interface GalleryScreenEvent {

	data object LoadImages : GalleryScreenEvent
	data class OnSelectAlbum(val album: GalleryBucketModel) : GalleryScreenEvent
	data object OnDismissModalSheet : GalleryScreenEvent

}