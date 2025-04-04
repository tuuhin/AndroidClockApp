package com.eva.clockapp.features.alarms.presentation.play_alarm.composables

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.core.graphics.drawable.toDrawable
import coil3.compose.AsyncImagePainter
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import coil3.request.ImageRequest
import coil3.request.crossfade
import coil3.request.placeholder
import coil3.size.Size

@Composable
fun AlarmsBackground(
	backgroundImage: String?,
	modifier: Modifier = Modifier,
	containerColor: Color = MaterialTheme.colorScheme.background,
	isPreview: Boolean = false,
) {
	val context = LocalContext.current
	val config = LocalConfiguration.current
	val overlayColor = MaterialTheme.colorScheme.primary

	Crossfade(
		targetState = backgroundImage != null,
		modifier = modifier.fillMaxSize()
	) { isPresent ->
		if (isPresent && backgroundImage != null)
			SubcomposeAsyncImage(
				model = ImageRequest.Builder(context)
					.data(backgroundImage)
					.size { Size(config.screenWidthDp, config.screenHeightDp) }
					.placeholder(containerColor.toArgb().toDrawable())
					.crossfade(true)
					.build(),
				contentDescription = "Alarm Screen background Image",
				contentScale = ContentScale.Crop,
				filterQuality = if (isPreview) FilterQuality.None else FilterQuality.Medium,
				modifier = Modifier
					.fillMaxSize()
					.overlayModifier(
						isImagePresent = true,
						overlayColor = overlayColor
					)
			) {
				val state by painter.state.collectAsState()
				when (state) {
					AsyncImagePainter.State.Empty -> {
						Box(
							modifier = Modifier
								.background(color = MaterialTheme.colorScheme.surfaceContainerHigh)
						)
					}

					is AsyncImagePainter.State.Loading -> {
						Box(
							modifier = Modifier
								.fillMaxSize()
								.background(color = MaterialTheme.colorScheme.surfaceContainerHigh),
							contentAlignment = Alignment.Center
						) {
							CircularProgressIndicator(
								strokeCap = StrokeCap.Round,
								color = MaterialTheme.colorScheme.onPrimary
							)
						}
					}

					is AsyncImagePainter.State.Success -> SubcomposeAsyncImageContent()

					else -> {}
				}
			}
		else Box(
			modifier = Modifier
				.fillMaxSize()
				.overlayModifier(isImagePresent = false, overlayColor = overlayColor)
		)
	}
}


private fun Modifier.overlayModifier(
	isImagePresent: Boolean,
	overlayColor: Color,
) = then(
	Modifier.drawWithCache {
		val colors = buildList {
			add(Color.Transparent)
			if (isImagePresent)
				add(Color.Black) else add(overlayColor)
		}

		val brush = Brush.verticalGradient(
			colors = colors,
			startY = 0f,
			endY = size.height
		)
		onDrawWithContent {
			drawContent()
			drawRect(
				brush = brush,
				size = size,
				alpha = .5f,
			)
		}
	},
)
