package com.eva.clockapp.features.alarms.presentation.composables

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toDrawable
import androidx.core.net.toUri
import coil3.ColorImage
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.AsyncImagePainter
import coil3.compose.AsyncImagePreviewHandler
import coil3.compose.LocalAsyncImagePreviewHandler
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import coil3.request.ImageRequest
import coil3.request.placeholder
import com.eva.clockapp.R
import com.eva.clockapp.features.alarms.domain.models.WallpaperPhoto
import com.eva.clockapp.features.alarms.presentation.util.AlarmPreviewFakes
import com.eva.clockapp.ui.theme.ClockAppTheme
import kotlinx.collections.immutable.ImmutableList

@OptIn(ExperimentalCoilApi::class, ExperimentalMaterial3Api::class)
@Composable
fun BackgroundImageSelector(
	imageOptions: ImmutableList<WallpaperPhoto>,
	onSelectImage: (String?) -> Unit,
	size: Size = Size(160f, 280f),
	modifier: Modifier = Modifier,
	shape: Shape = MaterialTheme.shapes.medium,
	colors: CardColors = CardDefaults.elevatedCardColors(),
) {
	val context = LocalContext.current
	val isInspectionMode = LocalInspectionMode.current

	val lazyColumKey: ((Int, WallpaperPhoto) -> Any)? = remember {
		if (isInspectionMode) return@remember null
		{ _, item -> item.id }
	}

	Surface(
		modifier = modifier,
		shape = shape,
		color = colors.containerColor,
	) {
		Column(
			modifier = Modifier.padding(all = dimensionResource(R.dimen.card_internal_padding_large)),
			verticalArrangement = Arrangement.spacedBy(4.dp)
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
					itemsIndexed(items = imageOptions, key = lazyColumKey) { _, itemUri ->
						BackgroundImageOption(
							photo = itemUri,
							size = size,
							onSelectImage = onSelectImage
						)
					}
				}
			}
			Row(
				modifier = Modifier.align(Alignment.CenterHorizontally),
				horizontalArrangement = Arrangement.spacedBy(1.dp),
				verticalAlignment = Alignment.CenterVertically
			) {
				Text(
					text = "Images are provided from ",
					style = MaterialTheme.typography.labelLarge,
					color = MaterialTheme.colorScheme.secondary,
				)
				TextButton(
					onClick = {
						try {
							val intent = Intent(Intent.ACTION_VIEW).apply {
								data = "https://www.pexels.com/".toUri()
								flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
										Intent.FLAG_ACTIVITY_SINGLE_TOP
							}
							context.startActivity(intent)
						} catch (_: Exception) {
						}
					},
					contentPadding = PaddingValues(0.dp)
				) {
					Text(
						text = "pexels.com",
						fontWeight = FontWeight.Medium,
						textDecoration = TextDecoration.Underline
					)
				}
			}
		}
	}
}

@Composable
private fun BackgroundImageOption(
	photo: WallpaperPhoto,
	onSelectImage: (String) -> Unit,
	size: Size = Size(160f, 280f),
	modifier: Modifier = Modifier,
) {
	val context = LocalContext.current
	val density = LocalDensity.current

	SubcomposeAsyncImage(
		model = ImageRequest.Builder(context)
			.data(photo.uri)
			.placeholder(drawable = photo.placeholderColor.toDrawable())
			.size(size.width.toInt(), size.height.toInt())
			.build(),
		contentDescription = "Image for :$photo",
		contentScale = ContentScale.Crop,
		filterQuality = FilterQuality.None,
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
			.clickable(onClick = { onSelectImage(photo.uri) }, role = Role.Image)
	) {
		val state by painter.state.collectAsState()
		when (state) {
			AsyncImagePainter.State.Empty -> Box(
				modifier = Modifier
					.background(color = MaterialTheme.colorScheme.surfaceContainerHigh)
					.clip(MaterialTheme.shapes.medium),
			)

			is AsyncImagePainter.State.Loading -> Box(
				modifier = Modifier
					.background(color = Color(photo.placeholderColor))
					.clip(MaterialTheme.shapes.medium),
			)

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
	BackgroundImageSelector(
		imageOptions = AlarmPreviewFakes.RANDOM_BACKGROUND_OPTIONS,
		onSelectImage = {})
}