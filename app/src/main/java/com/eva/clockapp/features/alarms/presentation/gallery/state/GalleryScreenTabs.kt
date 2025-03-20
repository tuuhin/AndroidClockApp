package com.eva.clockapp.features.alarms.presentation.gallery.state

import androidx.compose.runtime.Composable

enum class GalleryScreenTabs(val tabIndex: Int) {
	ALL_IMAGES(0),
	ALBUMS(1);

	val toText: String
		@Composable
		get() = when (this) {
			ALL_IMAGES -> "Images"
			ALBUMS -> "Albums"
		}
}

