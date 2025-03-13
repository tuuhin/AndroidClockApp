package com.eva.clockapp.features.alarms.presentation.play_alarm

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.palette.graphics.Palette
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.toBitmap
import com.eva.clockapp.R
import com.eva.clockapp.core.utils.HH_MM
import com.eva.clockapp.core.utils.WEEK_MONTH_DAY
import com.eva.clockapp.ui.theme.ClockAppTheme
import com.eva.clockapp.ui.theme.DownloadableFonts
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.format

@Composable
fun PlayAlarmsScreen(
	dateTime: LocalDateTime,
	onStopAlarm: () -> Unit,
	onSnoozeAlarm: () -> Unit,
	modifier: Modifier = Modifier,
	labelText: String? = null,
	backgroundImage: String? = null,
	isActionEnabled: Boolean = true,
	defaultTitleColor: Color = MaterialTheme.colorScheme.onBackground,
	defaultButtonColor: Color = MaterialTheme.colorScheme.onBackground,
	shape: Shape = RectangleShape,
	borderStroke: BorderStroke? = null,
) {
	val context = LocalContext.current
	val isInspectionMode = LocalInspectionMode.current
	val scope = rememberCoroutineScope()

	var paletteVibrantColor by remember { mutableStateOf<Color?>(null) }
	var paletteMutedColor by remember { mutableStateOf<Color?>(null) }
	var paletteDominantColor by remember { mutableStateOf<Color?>(null) }


	val listener = remember {
		object : Palette.PaletteAsyncListener {
			override fun onGenerated(palette: Palette?) {
				if (palette == null) return

				// TODO: Check for the colors later

				val dominant = palette.dominantSwatch

				val vibrant = palette.darkVibrantSwatch
					?: palette.vibrantSwatch ?: palette.lightVibrantSwatch
				val muted = palette.lightMutedSwatch
					?: palette.mutedSwatch ?: palette.darkMutedSwatch

				paletteVibrantColor = vibrant?.bodyTextColor?.let(::Color)
				paletteMutedColor = muted?.titleTextColor?.let(::Color)
				paletteDominantColor = dominant?.titleTextColor?.let(::Color)
			}
		}
	}

	Surface(
		modifier = modifier,
		shape = shape,
		border = borderStroke
	) {
		backgroundImage?.let { uri ->
			AsyncImage(
				model = ImageRequest.Builder(context)
					.data(uri)
					.allowHardware(false)
					.build(),
				onSuccess = { success ->
					if (!isInspectionMode) {
						scope.launch(Dispatchers.Default) {
							try {
								val bitmap = success.result.image.toBitmap()
								// generate palette
								Palette.Builder(bitmap).generate(listener)

							} catch (e: Exception) {
								e.printStackTrace()
							}
						}
					}
				},
				contentDescription = "Alarm Screen background Image",
				contentScale = ContentScale.Crop,
				modifier = Modifier
					.fillMaxSize()
					.drawWithContent {
						drawContent()
						drawRect(color = Color.Gray, size = size, alpha = 0.2f)
					}
			)
		}
		Box(
			modifier = Modifier
				.padding(all = dimensionResource(R.dimen.alarms_screen_padding))
				.fillMaxSize(),
			contentAlignment = Alignment.Center
		) {
			Column(
				verticalArrangement = Arrangement.spacedBy(12.dp),
				horizontalAlignment = Alignment.CenterHorizontally,
			) {
				Text(
					text = dateTime.time.format(LocalTime.Formats.HH_MM),
					style = MaterialTheme.typography.displayLarge,
					fontFamily = DownloadableFonts.BUNGEE,
					color = paletteDominantColor ?: defaultTitleColor,
				)
				Text(
					text = dateTime.date.format(LocalDate.Formats.WEEK_MONTH_DAY),
					style = MaterialTheme.typography.headlineSmall,
					color = paletteVibrantColor ?: defaultTitleColor,
				)
				Text(
					text = labelText ?: "Alarm",
					style = MaterialTheme.typography.headlineSmall,
					color = paletteVibrantColor ?: defaultTitleColor,
					maxLines = 2,
					overflow = TextOverflow.Ellipsis,
					textAlign = TextAlign.Center,
					modifier = Modifier.widthIn(max = 140.dp),
				)
			}
			Column(
				verticalArrangement = Arrangement.spacedBy(24.dp),
				horizontalAlignment = Alignment.CenterHorizontally,
				modifier = Modifier
					.align(Alignment.BottomCenter)
					.offset(y = (-20).dp)
			) {
				FloatingActionButton(
					onClick = onStopAlarm,
					shape = CircleShape,
					containerColor = paletteMutedColor ?: defaultButtonColor,
					contentColor = paletteVibrantColor ?: MaterialTheme.colorScheme.background
				) {
					Icon(imageVector = Icons.Default.Close, contentDescription = null)
				}
				OutlinedButton(
					onClick = onSnoozeAlarm,
					border = BorderStroke(
						color = MaterialTheme.colorScheme.onBackground,
						width = 2.dp
					),
					enabled = isActionEnabled,
					shape = MaterialTheme.shapes.extraLarge,
					colors = ButtonDefaults.outlinedButtonColors(
						contentColor = paletteVibrantColor ?: defaultTitleColor
					),
					modifier = Modifier.widthIn(max = 120.dp),
				) {
					Text(text = stringResource(R.string.snooze_options_title))
				}
			}
		}
	}
}


@PreviewLightDark
@Composable
private fun PlayAlarmsScreenPreview() = ClockAppTheme {
	PlayAlarmsScreen(
		dateTime = LocalDateTime(2025, 3, 4, 12, 0),
		onStopAlarm = {},
		onSnoozeAlarm = {},
	)
}