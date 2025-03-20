package com.eva.clockapp.features.alarms.presentation.gallery.state

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.eva.clockapp.R

enum class GalleryScreenTabs(val tabIndex: Int) {
	ALL_IMAGES(0),
	ALBUMS(1);

	val toText: String
		@Composable
		get() = when (this) {
			ALL_IMAGES -> stringResource(R.string.gallery_screen_tab_images)
			ALBUMS -> stringResource(R.string.gallery_screen_tab_albums)
		}
}

