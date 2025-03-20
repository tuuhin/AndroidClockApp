package com.eva.clockapp.features.alarms.presentation.gallery.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import coil3.ColorImage
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.AsyncImagePreviewHandler
import coil3.compose.LocalAsyncImagePreviewHandler
import com.eva.clockapp.features.alarms.domain.models.GalleryImageModel
import com.eva.clockapp.features.alarms.presentation.util.AlarmPreviewFakes
import com.eva.clockapp.ui.theme.ClockAppTheme
import kotlinx.collections.immutable.ImmutableList

@OptIn(ExperimentalCoilApi::class)
@Composable
fun GalleryImagesCompactGrid(
	items: ImmutableList<GalleryImageModel>,
	onSelectImage: (GalleryImageModel) -> Unit,
	modifier: Modifier = Modifier,
	itemShape: Shape = MaterialTheme.shapes.medium,
	contentPadding: PaddingValues = PaddingValues(0.dp),
) {
	val inspectionMode = LocalInspectionMode.current

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
			columns = GridCells.Fixed(4),
			modifier = modifier,
			contentPadding = contentPadding,
			horizontalArrangement = Arrangement.spacedBy(4.dp),
			verticalArrangement = Arrangement.spacedBy(4.dp),
		) {
			itemsIndexed(
				items = items,
				key = keys,
				contentType = contentType
			) { _, image ->
				GalleryGridImageItem(
					image = image,
					onClick = { onSelectImage(image) },
					shape = itemShape,
				)
			}
		}
	}
}

@PreviewLightDark
@Composable
private fun GalleryImagesCompactGridPreview() = ClockAppTheme {
	GalleryImagesCompactGrid(items = AlarmPreviewFakes.GALLEY_IMAGE_MODELS, onSelectImage = {})
}
