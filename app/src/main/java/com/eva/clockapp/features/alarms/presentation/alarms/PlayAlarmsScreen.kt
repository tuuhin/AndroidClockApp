package com.eva.clockapp.features.alarms.presentation.alarms

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
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
import coil3.request.allowHardware
import coil3.request.placeholder
import coil3.size.Size
import com.eva.clockapp.features.alarms.presentation.util.AlarmPreviewFakes
import com.eva.clockapp.ui.theme.ClockAppTheme
import kotlinx.datetime.LocalDateTime

@Composable
fun PlayAlarmsScreen(
	dateTime: LocalDateTime,
	onStopAlarm: () -> Unit,
	onSnoozeAlarm: () -> Unit,
	modifier: Modifier = Modifier,
	labelText: String? = null,
	backgroundImage: String? = null,
	isActionEnabled: Boolean = true,
	containerColor: Color = MaterialTheme.colorScheme.background,
	shape: Shape = RectangleShape,
	borderStroke: BorderStroke? = null,
) {
	Surface(
		modifier = modifier,
		shape = shape,
		border = borderStroke,
		color = containerColor
	) {
		PlayAlarmScreenBackground(
			backgroundImage,
			containerColor = containerColor
		)
		PlayAlarmScreenContent(
			dateTime = dateTime,
			onSnoozeAlarm = onSnoozeAlarm,
			onStopAlarm = onStopAlarm,
			labelText = labelText,
			isActionEnabled = isActionEnabled,
			buttonContentColor = MaterialTheme.colorScheme.inverseOnSurface,
			buttonContainerColor = MaterialTheme.colorScheme.inverseSurface,
		)
	}
}

@Composable
fun PlayAlarmScreenBackground(
	backgroundImage: String?,
	modifier: Modifier = Modifier,
	containerColor: Color = MaterialTheme.colorScheme.background,
) {
	val context = LocalContext.current
	val config = LocalConfiguration.current

	val overlayModifier = Modifier
		.fillMaxSize()
		.drawWithContent {
			drawContent()
			drawRect(
				color = Color.Black,
				size = size,
				alpha = .4f,
				blendMode = BlendMode.Multiply
			)
		}

	backgroundImage?.let { uri ->
		AsyncImage(
			model = ImageRequest.Builder(context)
				.data(uri)
				.size {
					Size(config.screenWidthDp, config.screenHeightDp)
				}
				.allowHardware(false)
				.placeholder(containerColor.toArgb().toDrawable())
				.build(),
			contentDescription = "Alarm Screen background Image",
			contentScale = ContentScale.Crop,
			filterQuality = FilterQuality.Medium,
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