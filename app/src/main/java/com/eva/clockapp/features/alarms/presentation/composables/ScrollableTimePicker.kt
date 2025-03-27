package com.eva.clockapp.features.alarms.presentation.composables

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
import kotlinx.coroutines.flow.filter
import kotlinx.datetime.LocalTime
import java.text.NumberFormat
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
@Composable
fun ScrollableTimePicker(
	onTimeSelected: (LocalTime) -> Unit,
	modifier: Modifier = Modifier,
	is24HrFormat: Boolean = false,
	formatToLocale: Boolean = true,
	startTime: LocalTime = LocalTime(0, 0),
	handsStyle: TextStyle = MaterialTheme.typography.displayMedium,
	containerColor: Color = MaterialTheme.colorScheme.surfaceContainer,
	contentColor: Color = MaterialTheme.colorScheme.onSurface,
	fontFamily: FontFamily? = FontFamily.SansSerif,
	shape: Shape = MaterialTheme.shapes.large,
) {
	val clockMinuteRange = 0..<60
	val clockHourRange = 0..<24

	val currentOnTimeSelected by rememberUpdatedState(onTimeSelected)
	val formatter = remember { NumberFormat.getInstance() }

	var selectedTime by remember { mutableStateOf(startTime) }
	var isTimeInAm by remember { mutableStateOf(startTime.hour < 12) }


	LaunchedEffect(selectedTime, isTimeInAm, is24HrFormat) {

		val newTime = validateLocalTime(selectedTime, is24HrFormat, isTimeInAm)

		snapshotFlow { newTime }
			.filter { time -> time != startTime }
			.debounce(100.milliseconds)
			.distinctUntilChanged()
			.collectLatest { time -> currentOnTimeSelected(time) }
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
					selectedTime = LocalTime(hour, selectedTime.minute)
				},
			) { hour24 ->
				val hour = roundToHour(hour24, is24HrFormat)
				val hourText = if (formatToLocale) formatter.format(hour) else "$hour"
				Text(
					text = hourText.padStart(2, '0'),
					textAlign = TextAlign.Center,
					fontFamily = fontFamily,
					style = handsStyle,
					modifier = Modifier.widthIn(min = 40.dp)
				)
			}
			Spacer(modifier = Modifier.width(16.dp))
			Text(
				text = ":",
				style = MaterialTheme.typography.displayMedium,
				fontFamily = fontFamily,
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
					selectedTime = LocalTime(selectedTime.hour, minute)
				},
			) { idx ->
				val minute = idx % 60
				val minuteText = if (formatToLocale) formatter.format(minute) else "$minute"
				Text(
					text = minuteText.padStart(2, '0'),
					textAlign = TextAlign.Center,
					fontFamily = fontFamily,
					style = handsStyle,
					modifier = Modifier.widthIn(min = 40.dp)
				)
			}
			if (!is24HrFormat) {
				CircularRangedNumberPicker(
					range = 0..1,
					startIndex = if (startTime.hour > 12) 1 else 0,
					contentColor = contentColor,
					containerColor = containerColor,
					isInfinite = false,
					onFocusItem = { option -> isTimeInAm = option != 0 },
					modifier = Modifier.padding(start = 16.dp)
				) { option ->

					val text = if (option == 0) stringResource(R.string.ante_meridian)
					else stringResource(R.string.post_meridian)

					Text(
						text = text,
						textAlign = TextAlign.Center,
						style = MaterialTheme.typography.headlineLarge,
						fontFamily = fontFamily,
						modifier = Modifier.widthIn(min = 40.dp)
					)
				}
			}
		}
	}
}

private fun roundToHour(hour24: Int, is24HrFormat: Boolean): Int {
	return when {
		is24HrFormat -> hour24
		hour24 == 0 -> 12
		hour24 > 12 -> hour24 - 12
		else -> hour24
	}
}

private fun validateLocalTime(time: LocalTime, is24HrFormat: Boolean, isTimeInAm: Boolean)
		: LocalTime {
	return when {
		is24HrFormat -> time
		!isTimeInAm && time.hour in 0..<12 -> LocalTime(time.hour + 12, time.minute)
		isTimeInAm && time.hour in 12..23 -> LocalTime(time.hour - 12, time.minute)
		else -> time
	}
}

private class Is24HrsPreviewParams
	: CollectionPreviewParameterProvider<Boolean>(listOf(true, false))

@Preview
@Composable
private fun ScrollableTimePickerPreview(
	@PreviewParameter(Is24HrsPreviewParams::class)
	is24HrFormat: Boolean,
) = ClockAppTheme {
	ScrollableTimePicker(
		is24HrFormat = is24HrFormat,
		onTimeSelected = { },
		startTime = LocalTime(0, 0)
	)
}