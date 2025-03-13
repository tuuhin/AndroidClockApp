package com.eva.clockapp.features.alarms.presentation.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotInterested
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
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
import com.eva.clockapp.R
import com.eva.clockapp.ui.theme.ClockAppTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList

@OptIn(ExperimentalCoilApi::class, ExperimentalMaterial3Api::class)
@Composable
fun BackgroundImageSelector(
	items: ImmutableList<String>,
	onSelectImage: (String?) -> Unit,
	size: Size = Size(160f, 280f),
	modifier: Modifier = Modifier,
	shape: Shape = MaterialTheme.shapes.medium,
	colors: CardColors = CardDefaults.elevatedCardColors(),
) {
	val isInspectionMode = LocalInspectionMode.current

	val lazyColumKey: ((Int, String) -> Any)? = remember {
		if (isInspectionMode) return@remember null
		{ _, item -> item }
	}

	Card(
		modifier = modifier,
		shape = shape,
		colors = colors,
	) {
		Column(
			modifier = Modifier.padding(all = dimensionResource(R.dimen.card_internal_padding_large)),
			verticalArrangement = Arrangement.spacedBy(12.dp)
		) {

			val previewHandler = AsyncImagePreviewHandler {
				ColorImage(color = colors.contentColor.toArgb())
			}

			CompositionLocalProvider(LocalAsyncImagePreviewHandler provides previewHandler) {
				LazyRow(
					contentPadding = PaddingValues(4.dp),
					horizontalArrangement = Arrangement.spacedBy(12.dp),
					modifier = Modifier.fillMaxWidth(),
				) {
					blankItem(
						size = size,
						onClick = { onSelectImage(null) },
					)
					itemsIndexed(items = items, key = lazyColumKey) { _, itemUri ->
						BackgroundImageOption(
							uri = itemUri,
							size = size,
							onSelectImage = onSelectImage
						)
					}
				}
			}
			Text(
				text = stringResource(R.string.background_images_provider),
				style = MaterialTheme.typography.labelLarge,
				color = MaterialTheme.colorScheme.secondary,
				modifier = Modifier.align(Alignment.CenterHorizontally)
			)
		}
	}
}

@Composable
private fun BackgroundImageOption(
	uri: String,
	onSelectImage: (String) -> Unit,
	size: Size = Size(160f, 280f),
	modifier: Modifier = Modifier,
) {
	val context = LocalContext.current
	val density = LocalDensity.current

	SubcomposeAsyncImage(
		model = ImageRequest.Builder(context)
			.data(uri)
			.size(size.width.toInt(), size.height.toInt())
			.build(),
		contentDescription = "Image for :$uri",
		contentScale = ContentScale.Crop,
		filterQuality = FilterQuality.Low,
		modifier = modifier
			.border(
				1.25.dp,
				color = MaterialTheme.colorScheme.secondary,
				shape = MaterialTheme.shapes.large
			)
			.size(
				height = with(density) { size.height.toDp() },
				width = with(density) { size.width.toDp() }
			)
			.clip(MaterialTheme.shapes.large)
			.clickable(onClick = { onSelectImage(uri) }, role = Role.Image)
	) {
		val state by painter.state.collectAsState()
		when (state) {
			is AsyncImagePainter.State.Loading, AsyncImagePainter.State.Empty -> Box(
				modifier = Modifier
					.background(color = MaterialTheme.colorScheme.surfaceContainerHigh)
					.clip(MaterialTheme.shapes.medium),
				contentAlignment = Alignment.Center
			) {
				val isLoading = state is AsyncImagePainter.State.Loading
				if (isLoading) {
					CircularProgressIndicator()
				}
			}

			is AsyncImagePainter.State.Success -> SubcomposeAsyncImageContent()

			else -> {}
		}
	}
}


private fun LazyListScope.blankItem(size: Size = Size(160f, 280f), onClick: () -> Unit) =
	item {
		val density = LocalDensity.current
		Column(
			modifier = Modifier
				.border(
					1.25.dp,
					color = MaterialTheme.colorScheme.secondary,
					shape = MaterialTheme.shapes.large
				)
				.size(
					height = with(density) { size.height.toDp() },
					width = with(density) { size.width.toDp() }
				)
				.clip(MaterialTheme.shapes.large)
				.clickable(onClick = onClick, role = Role.Image),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Center
		) {
			Icon(
				imageVector = Icons.Default.NotInterested,
				contentDescription = "No background",
				modifier = Modifier.size(20.dp),
				tint = MaterialTheme.colorScheme.outline
			)
			Spacer(modifier = Modifier.height(2.dp))
			Text(text = "Normal", style = MaterialTheme.typography.labelSmall)
		}
	}

@PreviewLightDark
@Composable
private fun BackgroundImageSelectorPreview() = ClockAppTheme {
	BackgroundImageSelector(items = List(10) { ":$it" }.toPersistentList(), onSelectImage = {})
}