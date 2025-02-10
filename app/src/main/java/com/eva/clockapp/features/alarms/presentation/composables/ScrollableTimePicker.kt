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
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
@Composable
fun ScrollableTimePicker(
	onTimeSelected: (LocalTime) -> Unit,
	modifier: Modifier = Modifier,
	is24HrFormat: Boolean = false,
	startTime: LocalTime = LocalTime(0, 0),
	handsStyle: TextStyle = MaterialTheme.typography.displayMedium,
	containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
	contentColor: Color = MaterialTheme.colorScheme.onSurface,
	numberFontFamily: FontFamily? = FontFamily.SansSerif,
	shape: Shape = MaterialTheme.shapes.large,
) {
	val clockMinuteRange = remember { 0..<60 }

	val clockHourRange = remember(is24HrFormat) {
		if (is24HrFormat) 0..<24
		else 1..12
	}

	val updatedOnTimeSelected by rememberUpdatedState(onTimeSelected)

	val selectedIndexForHour = remember(is24HrFormat, startTime) {
		val currentHourAsFormat = with(startTime) {
			when {
				is24HrFormat -> hour
				hour == 0 || hour == 24 -> hour
				else -> hour % 12
			}
		}
		clockHourRange.indexOf(currentHourAsFormat).let { idx ->
			if (idx == -1) clockHourRange.first
			else idx
		}
	}

	val selectedIndexForMinute = remember(startTime) {
		clockMinuteRange.indexOf(startTime.minute).let { idx ->
			if (idx == -1) clockMinuteRange.first
			else idx
		}
	}

	var newTimeSelection by remember { mutableStateOf(startTime) }
	var isTimeInAm by remember { mutableStateOf(false) }

	LaunchedEffect(newTimeSelection) {
		snapshotFlow { newTimeSelection }
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
				selectedIndex = selectedIndexForHour,
				contentColor = contentColor,
				containerColor = containerColor,
				onFocusItem = { idx ->
					clockHourRange.toList().getOrNull(idx)?.let { hour ->
						val converted = if (isTimeInAm || is24HrFormat) hour else 12 + hour
						newTimeSelection = LocalTime(converted, newTimeSelection.minute)
					}
				},
			) { idx ->
				clockHourRange.toList().getOrNull(idx)?.let { hour ->
					Text(
						text = "$hour".padStart(2, '0'),
						textAlign = TextAlign.Center,
						fontFamily = numberFontFamily,
						style = handsStyle,
						modifier = Modifier.widthIn(min = 40.dp)
					)
				}
			}
			Spacer(modifier = Modifier.width(16.dp))
			Text(
				text = ":",
				style = MaterialTheme.typography.displayMedium,
				color = contentColor,
				modifier = Modifier.align(Alignment.CenterVertically)
			)
			Spacer(modifier = Modifier.width(16.dp))
			CircularRangedNumberPicker(
				range = clockMinuteRange,
				selectedIndex = selectedIndexForMinute,
				contentColor = contentColor,
				containerColor = containerColor,
				onFocusItem = { idx ->
					clockMinuteRange.toList().getOrNull(idx)?.let { minute ->
						newTimeSelection = LocalTime(newTimeSelection.hour, minute)
					}
				},
			) { idx ->
				clockMinuteRange.toList().getOrNull(idx)?.let { minute ->
					Text(
						text = "$minute".padStart(2, '0'),
						textAlign = TextAlign.Center,
						fontFamily = numberFontFamily,
						style = handsStyle,
						modifier = Modifier.widthIn(min = 40.dp)
					)
				}
			}
			AnimatedVisibility(
				visible = !is24HrFormat,
				enter = slideInVertically() + fadeIn(),
				exit = slideOutVertically() + fadeOut(),
				modifier = Modifier.align(Alignment.Bottom)
			) {
				CircularRangedNumberPicker(
					range = 0..1,
					selectedIndex = 0,
					contentColor = contentColor,
					containerColor = containerColor,
					endLess = false,
					onFocusItem = { option -> isTimeInAm = option == 0 },
					modifier = Modifier.padding(start = 16.dp)
				) { option ->

					val text = if (option == 0) stringResource(R.string.ante_meridian)
					else stringResource(R.string.post_meridian)

					Text(
						text = text,
						textAlign = TextAlign.Center,
						style = MaterialTheme.typography.headlineLarge,
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
	)
}