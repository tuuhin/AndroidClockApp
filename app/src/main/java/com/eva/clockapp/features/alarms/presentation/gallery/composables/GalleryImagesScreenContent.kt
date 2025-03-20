package com.eva.clockapp.features.alarms.presentation.gallery.composables

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.eva.clockapp.R
import com.eva.clockapp.features.alarms.domain.models.GalleryBucketModel
import com.eva.clockapp.features.alarms.domain.models.GalleryImageModel
import com.eva.clockapp.features.alarms.presentation.gallery.state.GalleryScreenTabs
import com.eva.clockapp.features.alarms.presentation.util.AlarmPreviewFakes
import com.eva.clockapp.ui.theme.ClockAppTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryImagesScreenContent(
	images: ImmutableList<GalleryImageModel>,
	buckets: ImmutableList<GalleryBucketModel>,
	onSelectImage: (GalleryImageModel) -> Unit,
	onSelectAlbum: (GalleryBucketModel) -> Unit,
	modifier: Modifier = Modifier,
	initialTab: GalleryScreenTabs = GalleryScreenTabs.ALL_IMAGES,
	contentPadding: PaddingValues = PaddingValues(0.dp),
) {
	val scope = rememberCoroutineScope()

	val pagerState = rememberPagerState(
		initialPage = initialTab.tabIndex,
		pageCount = { GalleryScreenTabs.entries.size }
	)

	val selectedTabIndex by remember(pagerState) {
		derivedStateOf(pagerState::currentPage)
	}

	Column(
		modifier = modifier.fillMaxSize(),
		verticalArrangement = Arrangement.spacedBy(4.dp)
	) {
		PrimaryTabRow(selectedTabIndex = selectedTabIndex) {
			GalleryScreenTabs.entries.forEach { tab ->
				Tab(
					selected = tab.tabIndex == selectedTabIndex,
					text = { Text(text = tab.toText) },
					onClick = {
						val index = tab.tabIndex
						if (index != selectedTabIndex)
							scope.launch {
								pagerState.animateScrollToPage(index)
							}
					},
				)
			}
		}
		HorizontalPager(
			state = pagerState,
			flingBehavior = PagerDefaults.flingBehavior(
				state = pagerState,
				snapPositionalThreshold = .4f,
				snapAnimationSpec = spring(
					dampingRatio = Spring.DampingRatioNoBouncy,
					stiffness = Spring.StiffnessLow
				)
			),
			pageSpacing = dimensionResource(id = R.dimen.sc_padding),
			contentPadding = contentPadding,
			modifier = Modifier.fillMaxSize(),
		) { tabIndex ->
			when (tabIndex) {
				GalleryScreenTabs.ALL_IMAGES.tabIndex -> GalleryImagesGrid(
					items = images,
					onSelectImage = onSelectImage,
					modifier = Modifier.fillMaxSize()
				)

				GalleryScreenTabs.ALBUMS.tabIndex -> GalleryAlbumGrid(
					albums = buckets,
					onSelectAlbum = onSelectAlbum,
					modifier = Modifier.fillMaxSize()
				)

				else -> {}
			}
		}
	}
}


@PreviewLightDark
@Composable
private fun GalleryImageLazyGridPreview() = ClockAppTheme {
	Surface {
		GalleryImagesScreenContent(
			images = AlarmPreviewFakes.GALLEY_IMAGE_MODELS,
			buckets = AlarmPreviewFakes.GALLEY_IMAGE_BUCKET_MODELS,
			onSelectImage = {},
			onSelectAlbum = {},
			modifier = Modifier.fillMaxSize()
		)
	}
}