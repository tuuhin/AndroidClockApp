package com.eva.clockapp.features.alarms.presentation.gallery.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Shape
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
import com.eva.clockapp.features.alarms.domain.models.GalleryImageModel
import com.eva.clockapp.features.alarms.presentation.util.AlarmPreviewFakes
import com.eva.clockapp.ui.theme.ClockAppTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.format
import kotlinx.datetime.minus
import kotlinx.datetime.toKotlinLocalDate
import java.time.LocalDate

@OptIn(ExperimentalCoilApi::class)
@Composable
fun GalleryImagesGrid(
	items: ImmutableList<GalleryImageModel>,
	onSelectImage: (GalleryImageModel) -> Unit,
	modifier: Modifier = Modifier,
	contentPadding: PaddingValues = PaddingValues(0.dp),
) {

	val inspectionMode = LocalInspectionMode.current

	val today = remember { LocalDate.now().toKotlinLocalDate() }

	val keys: ((Int, GalleryImageModel) -> Any)? = remember {
		if (inspectionMode) null
		else { _, model -> model.id }
	}

	val contentType: ((Int, GalleryImageModel) -> Any?) = remember {
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

			items.groupBy { it.dateModified }.forEach { (date, images) ->
				date?.let {
					val readableDate = when {
						today == date -> "Today"
						today.minus(DatePeriod(days = 1)) == date -> "Yesterday"
						else -> date.format(kotlinx.datetime.LocalDate.Formats.ISO)
					}
					item(
						span = { GridItemSpan(maxLineSpan) },
						contentType = "Heading"
					) {
						ListItem(
							headlineContent = { Text(text = readableDate) },
							colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.background)
						)
					}
				}

				itemsIndexed(items = images, key = keys, contentType = contentType) { _, image ->
					GalleryGridImageItem(image = image, onClick = { onSelectImage(image) })
				}
			}
		}
	}
}

@Composable
fun GalleryGridImageItem(
	image: GalleryImageModel,
	onClick: () -> Unit,
	size: Size = Size(320f, 320f),
	shape: Shape = MaterialTheme.shapes.medium,
	modifier: Modifier = Modifier,
) {
	val density = LocalDensity.current
	val context = LocalContext.current

	SubcomposeAsyncImage(
		model = ImageRequest.Builder(context)
			.data(image.uri)
			.size(width = size.width.toInt(), height = size.height.toInt())
			.build(),
		contentDescription = "Galley Image for :${image.uri}",
		contentScale = ContentScale.Crop,
		filterQuality = FilterQuality.None,
		alignment = Alignment.Center,
		modifier = modifier
			.size(
				height = with(density) { size.height.toDp() },
				width = with(density) { size.width.toDp() }
			)
			.clip(shape)
			.clickable(onClick = onClick, role = Role.Image)
	) {
		val state by painter.state.collectAsState()
		when (state) {
			is AsyncImagePainter.State.Loading, AsyncImagePainter.State.Empty -> {
				Box(
					modifier = Modifier
						.background(color = MaterialTheme.colorScheme.surfaceContainerHigh)
						.clip(shape),
				)
			}

			is AsyncImagePainter.State.Success -> SubcomposeAsyncImageContent()
			else -> {}
		}
	}
}

@PreviewLightDark
@Composable
private fun GalleryImagesGridPreview() = ClockAppTheme {
	GalleryImagesGrid(items = AlarmPreviewFakes.GALLEY_IMAGE_MODELS, onSelectImage = {})
}