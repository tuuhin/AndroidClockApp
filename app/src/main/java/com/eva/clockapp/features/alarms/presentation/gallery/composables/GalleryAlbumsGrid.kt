package com.eva.clockapp.features.alarms.presentation.gallery.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import coil3.ColorImage
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.AsyncImagePainter
import coil3.compose.AsyncImagePreviewHandler
import coil3.compose.LocalAsyncImagePreviewHandler
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import coil3.request.ImageRequest
import com.eva.clockapp.features.alarms.domain.models.GalleryBucketModel
import com.eva.clockapp.features.alarms.domain.models.GalleryImageModel
import com.eva.clockapp.features.alarms.presentation.util.AlarmPreviewFakes
import com.eva.clockapp.ui.theme.ClockAppTheme
import kotlinx.collections.immutable.ImmutableList

@OptIn(ExperimentalCoilApi::class)
@Composable
fun GalleryAlbumGrid(
	albums: ImmutableList<GalleryBucketModel>,
	onSelectAlbum: (GalleryBucketModel) -> Unit,
	modifier: Modifier = Modifier,
	contentPadding: PaddingValues = PaddingValues(0.dp),
) {

	val inspectionMode = LocalInspectionMode.current

	val keys: ((Int, GalleryBucketModel) -> Any)? = remember {
		if (inspectionMode) null
		else { _, model -> model.bucketId }
	}

	val contentType: ((Int, GalleryBucketModel) -> Any?) = remember {
		if (inspectionMode) { _, _ -> null }
		else { _, model -> GalleryImageModel::class.java.simpleName }
	}

	val previewColor = MaterialTheme.colorScheme.surfaceContainerHigh

	val previewHandler = remember {
		AsyncImagePreviewHandler {
			ColorImage(color = previewColor.toArgb())
		}
	}

	CompositionLocalProvider(LocalAsyncImagePreviewHandler provides previewHandler) {
		LazyVerticalGrid(
			columns = GridCells.Fixed(3),
			modifier = modifier,
			contentPadding = contentPadding,
			horizontalArrangement = Arrangement.spacedBy(4.dp),
			verticalArrangement = Arrangement.spacedBy(4.dp),
		) {

			itemsIndexed(items = albums, key = keys, contentType = contentType) { _, album ->
				Box(
					modifier = Modifier
						.clip(MaterialTheme.shapes.medium)
						.clickable(onClick = { onSelectAlbum(album) }, role = Role.Image)
						.animateItem(),
				) {
					Column(
						modifier = Modifier.padding(4.dp),
						verticalArrangement = Arrangement.spacedBy(6.dp),
						horizontalAlignment = Alignment.CenterHorizontally
					) {
						GalleryAlbumGridItem(bucket = album)
						Text(
							text = album.bucketName,
							style = MaterialTheme.typography.labelLarge,
							color = MaterialTheme.colorScheme.onSurfaceVariant
						)
					}
				}
			}
		}
	}
}

@Composable
private fun GalleryAlbumGridItem(
	bucket: GalleryBucketModel,
	imageSize: Size = Size(320f, 320f),
	modifier: Modifier = Modifier,
) {

	val context = LocalContext.current
	val density = LocalDensity.current

	val cardModifier = Modifier
		.clip(MaterialTheme.shapes.medium)
		.size(
			height = with(density) { imageSize.height.toDp() },
			width = with(density) { imageSize.width.toDp() }
		)

	bucket.thumbnail?.let { uri ->
		SubcomposeAsyncImage(
			model = ImageRequest.Builder(context)
				.data(uri)
				.size(200, 200)
				.build(),
			contentDescription = "Thumbnail for album :${bucket.bucketId}",
			contentScale = ContentScale.Crop,
			filterQuality = FilterQuality.None,
			modifier = modifier.then(cardModifier)
		) {
			val state by painter.state.collectAsState()
			when (state) {
				is AsyncImagePainter.State.Loading, AsyncImagePainter.State.Empty -> {
					Box(
						modifier = Modifier
							.background(color = MaterialTheme.colorScheme.surfaceContainerHigh)
							.clip(MaterialTheme.shapes.medium),
					)
				}

				is AsyncImagePainter.State.Success -> SubcomposeAsyncImageContent()
				else -> {}
			}
		}
	} ?: Box(
		modifier = modifier
			.background(color = MaterialTheme.colorScheme.surfaceContainerHigh)
			.then(cardModifier)
	)
}

@PreviewLightDark
@Composable
private fun GalleryAlbumGridPreview() = ClockAppTheme {
	Surface {
		GalleryAlbumGrid(
			albums = AlarmPreviewFakes.GALLEY_IMAGE_BUCKET_MODELS,
			onSelectAlbum = {},
		)
	}
}
