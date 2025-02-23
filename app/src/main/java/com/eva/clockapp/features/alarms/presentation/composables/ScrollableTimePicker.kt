package com.eva.clockapp.features.alarms.presentation.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.datasource.CollectionPreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.eva.clockapp.R
import com.eva.clockapp.ui.theme.ClockAppTheme
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.datetime.LocalTime
import java.text.NumberFormat
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
@Composable
fun ScrollableTimePicker(
	onTimeSelected: (LocalTime) -> Unit,
	modifier: Modifier = Modifier,
	is24HrFormat: Boolean = false,
	numberFormatLocale: Boolean = true,
	startTime: LocalTime = LocalTime(0, 0),
	handsStyle: TextStyle = MaterialTheme.typography.displayMedium,
	containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
	contentColor: Color = MaterialTheme.colorScheme.onSurface,
	numberFontFamily: FontFamily? = FontFamily.SansSerif,
	shape: Shape = MaterialTheme.shapes.large,
) {
	val clockMinuteRange = 0..<60
	val clockHourRange = 0..<24

	val updatedOnTimeSelected by rememberUpdatedState(onTimeSelected)
	val formatter = remember { NumberFormat.getInstance() }

	var newTimeSelection by remember { mutableStateOf(startTime) }
	var isTimeInAm by remember { mutableStateOf(startTime.hour < 12) }

	LaunchedEffect(newTimeSelection, isTimeInAm, is24HrFormat) {

		val newTime = when {
			is24HrFormat -> newTimeSelection
			!isTimeInAm && newTimeSelection.hour in 0..<12 -> LocalTime(
				newTimeSelection.hour + 12,
				newTimeSelection.minute
			)

			isTimeInAm && newTimeSelection.hour in 12..23 -> LocalTime(
				newTimeSelection.hour - 12,
				newTimeSelection.minute
			)

			else -> newTimeSelection
		}

		snapshotFlow { newTime }
			.debounce(200.milliseconds)
			.distinctUntilChanged()
			.collectLatest { time -> updatedOnTimeSelected(time) }

	}

	Card(
		shape = shape,
		elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
		colors = CardDefaults.cardColors(
			containerColor = containerColor,
			contentColor = contentColor
		),
		modifier = modifier.sizeIn(maxWidth = 320.dp),
	) {
		Row(
			modifier = Modifier
				.padding(all = dimensionResource(R.dimen.card_internal_padding_large))
				.fillMaxWidth(),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.Center,
		) {
			CircularRangedNumberPicker(
				range = clockHourRange,
				startIndex = startTime.hour,
				contentColor = contentColor,
				containerColor = containerColor,
				onFocusItem = { idx ->
					val hour = idx % 24
					newTimeSelection = LocalTime(hour, newTimeSelection.minute)
				},
			) { idx ->
				val hour = if (is24HrFormat) idx % 24 else if (idx % 12 == 0) 12 else idx % 12
				val hourText = if (numberFormatLocale) formatter.format(hour) else "$hour"
				Text(
					text = hourText.padStart(2, '0'),
					textAlign = TextAlign.Center,
					fontFamily = numberFontFamily,
					style = handsStyle,
					modifier = Modifier.widthIn(min = 40.dp)
				)
			}
			Spacer(modifier = Modifier.width(16.dp))
			Text(
				text = ":",
				style = MaterialTheme.typography.displayMedium,
				fontFamily = numberFontFamily,
				color = contentColor,
				modifier = Modifier.align(Alignment.CenterVertically)
			)
			Spacer(modifier = Modifier.width(16.dp))
			CircularRangedNumberPicker(
				range = clockMinuteRange,
				startIndex = startTime.minute,
				contentColor = contentColor,
				containerColor = containerColor,
				onFocusItem = { idx ->
					val minute = idx % 60
					newTimeSelection = LocalTime(newTimeSelection.hour, minute)
				},
			) { idx ->
				val minute = idx % 60
				val minuteText = if (numberFormatLocale) formatter.format(minute) else "$minute"
				Text(
					text = minuteText.padStart(2, '0'),
					textAlign = TextAlign.Center,
					fontFamily = numberFontFamily,
					style = handsStyle,
					modifier = Modifier.widthIn(min = 40.dp)
				)
			}
			AnimatedVisibility(
				visible = !is24HrFormat,
				enter = slideInVertically() + fadeIn(),
				exit = slideOutVertically() + fadeOut(),
				modifier = Modifier.align(Alignment.Bottom)
			) {
				CircularRangedNumberPicker(
					range = 0..1,
					startIndex = if (startTime.hour > 12) 1 else 0,
					contentColor = contentColor,
					containerColor = containerColor,
					endLess = false,
					onFocusItem = { option -> isTimeInAm = option != 0 },
					modifier = Modifier.padding(start = 16.dp)
				) { option ->

					val text = if (option == 0) stringResource(R.string.ante_meridian)
					else stringResource(R.string.post_meridian)

					Text(
						text = text,
						textAlign = TextAlign.Center,
						style = MaterialTheme.typography.headlineLarge,
						fontFamily = numberFontFamily,
						modifier = Modifier.widthIn(min = 40.dp)
					)
				}
			}
		}
	}
}

private class Is24HrsPreviewParams :
	CollectionPreviewParameterProvider<Boolean>(listOf(true, false))

@Preview
@Composable
private fun ScrollableTimePickerPreview(
	@PreviewParameter(Is24HrsPreviewParams::class)
	is24HrFormat: Boolean,
) = ClockAppTheme {
	ScrollableTimePicker(
		is24HrFormat = is24HrFormat,
		onTimeSelected = {},
		startTime = LocalTime(14, 10)
	)
}