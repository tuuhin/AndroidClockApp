package com.eva.clockapp.features.alarms.presentation.alarms

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.core.graphics.drawable.toDrawable
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.placeholder
import coil3.size.Size
import com.eva.clockapp.features.alarms.presentation.util.AlarmPreviewFakes
import com.eva.clockapp.ui.theme.ClockAppTheme
import kotlinx.datetime.LocalDateTime

@Composable
fun PlayAlarmsScreen(
	dateTime: LocalDateTime,
	modifier: Modifier = Modifier,
	onStopAlarm: () -> Unit = {},
	onSnoozeAlarm: () -> Unit = {},
	isActionEnabled: Boolean = true,
	isPreview: Boolean = false,
	backgroundImage: String? = null,
	labelText: String? = null,
	borderStroke: BorderStroke? = null,
	shape: Shape = RectangleShape,
	backgroundColor: Color = MaterialTheme.colorScheme.background,
) {
	Surface(
		modifier = modifier,
		shape = shape,
		border = borderStroke,
		color = backgroundColor,
	) {
		PlayAlarmScreenBackground(
			backgroundImage = backgroundImage,
			containerColor = backgroundColor,
			modifier = Modifier.fillMaxSize()
		)
		PlayAlarmScreenContent(
			dateTime = dateTime,
			onSnoozeAlarm = onSnoozeAlarm,
			onStopAlarm = onStopAlarm,
			labelText = labelText,
			isPreview = isPreview,
			isActionEnabled = isActionEnabled,
			textColorPrimary = if (backgroundImage != null) Color.White else MaterialTheme.colorScheme.onSurface,
			textColorSecondary = if (backgroundImage != null) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
		)
	}
}

@Composable
private fun PlayAlarmScreenBackground(
	backgroundImage: String?,
	modifier: Modifier = Modifier,
	containerColor: Color = MaterialTheme.colorScheme.background,
) {
	val context = LocalContext.current
	val config = LocalConfiguration.current
	val overlayColor = MaterialTheme.colorScheme.primary

	val overlayModifier = Modifier
		.fillMaxSize()
		.drawWithCache {

			val colors = buildList {
				add(Color.Transparent)
				if (backgroundImage != null) {
					add(Color.Black)
				} else {
					add(overlayColor)
				}
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
		}

	backgroundImage?.let { uri ->
		AsyncImage(
			model = ImageRequest.Builder(context)
				.data(uri)
				.size { Size(config.screenWidthDp, config.screenHeightDp) }
				.placeholder(containerColor.toArgb().toDrawable())
				.build(),
			contentDescription = "Alarm Screen background Image",
			contentScale = ContentScale.Crop,
			filterQuality = FilterQuality.Medium,
			onError = {
				// TODO: Add a fallback or do something
			},
			modifier = modifier.then(overlayModifier)
		)
	} ?: Box(modifier = modifier.then(overlayModifier))
}

@PreviewLightDark
@Composable
private fun PlayAlarmsScreenPreview() = ClockAppTheme {
	PlayAlarmsScreen(
		dateTime = LocalDateTime(2025, 3, 4, 12, 0),
		labelText = AlarmPreviewFakes.LOREM_TEXT,
		onStopAlarm = {},
		onSnoozeAlarm = {},
	)
}