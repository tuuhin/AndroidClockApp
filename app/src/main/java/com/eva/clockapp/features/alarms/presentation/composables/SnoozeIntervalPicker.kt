package com.eva.clockapp.features.alarms.presentation.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButtonColors
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.datasource.CollectionPreviewParameterProvider
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.eva.clockapp.R
import com.eva.clockapp.features.alarms.domain.models.SnoozeIntervalOption
import com.eva.clockapp.ui.theme.ClockAppTheme
import com.eva.clockapp.ui.theme.DownloadableFonts
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes

@Composable
fun SnoozeIntervalPicker(
	interval: SnoozeIntervalOption,
	onIntervalChange: (SnoozeIntervalOption) -> Unit,
	modifier: Modifier = Modifier,
	enabled: Boolean = true,
	containerColor: Color = MaterialTheme.colorScheme.background,
	contentColor: Color = MaterialTheme.colorScheme.onBackground,
	optionColors: RadioButtonColors = RadioButtonDefaults.colors(),
) {

	val isCustomModeSelected = remember(interval) {
		interval is SnoozeIntervalOption.IntervalCustomMinutes
	}

	Column(
		modifier = modifier,
		verticalArrangement = Arrangement.spacedBy(2.dp)
	) {
		ListItem(
			headlineContent = { Text(text = stringResource(R.string.snooze_options_title)) },
			colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.background)
		)
		SnoozeIntervalOption.predefinedOptions.forEach { option ->
			RadioButtonWithTextItem(
				text = "${option.duration}",
				isSelected = option == interval,
				onClick = { onIntervalChange(option) },
				enabled = enabled,
				colors = optionColors,
			)
		}
		RadioButtonWithTextItem(
			isSelected = isCustomModeSelected,
			enabled = enabled,
			text = stringResource(R.string.snooze_interval_custom),
			onClick = {
				val minutes = interval.duration.inWholeMinutes.toInt()
				onIntervalChange(SnoozeIntervalOption.IntervalCustomMinutes(minutes))
			},
			colors = optionColors,
		)
		AnimatedVisibility(
			visible = isCustomModeSelected && enabled,
			enter = expandVertically() + fadeIn(),
			exit = shrinkVertically() + fadeOut(),
			modifier = Modifier.align(Alignment.Start)
		) {
			CustomSnoozeTimePicker(
				starTime = interval.duration,
				onTimeChange = { duration ->
					val minutes = duration.inWholeMinutes.toInt()
					onIntervalChange(SnoozeIntervalOption.IntervalCustomMinutes(minutes))
				},
				containerColor = containerColor,
				contentColor = contentColor,
				modifier = Modifier.fillMaxWidth()
			)
		}
	}
}

@OptIn(FlowPreview::class)
@Composable
private fun CustomSnoozeTimePicker(
	starTime: Duration,
	onTimeChange: (Duration) -> Unit,
	modifier: Modifier = Modifier,
	containerColor: Color = MaterialTheme.colorScheme.background,
	contentColor: Color = MaterialTheme.colorScheme.onBackground,
) {

	val minuteRange = 0..98
	val startIndex = remember {
		val minute = starTime.inWholeMinutes.toInt()
		val index = minuteRange.toList().indexOf(minute) - 1
		// ensures its not negative
		maxOf(index, minuteRange.start)
	}

	var selectedDuration by remember { mutableStateOf(starTime) }
	val currentOnTimeChange by rememberUpdatedState(onTimeChange)

	LaunchedEffect(selectedDuration) {
		snapshotFlow { selectedDuration }
			.filter { it != starTime }
			.distinctUntilChanged()
			.debounce(50.milliseconds)
			.collectLatest { duration -> currentOnTimeChange(duration) }
	}

	Row(
		horizontalArrangement = Arrangement.Center,
		verticalAlignment = Alignment.CenterVertically,
		modifier = modifier.padding(vertical = 4.dp),
	) {
		CircularRangedNumberPicker(
			range = minuteRange,
			contentColor = contentColor,
			containerColor = containerColor,
			startIndex = startIndex,
			hapticEffectEnabled = false,
			elementSize = DpSize(48.dp, 48.dp),
			onFocusItem = { idx ->
				val minute = (idx + 1) % 100
				selectedDuration = minute.minutes
			},
		) { idx ->
			val minute = (idx + 1) % 100
			Text(
				text = "$minute".padStart(2, '0'),
				textAlign = TextAlign.Center,
				fontFamily = DownloadableFonts.CHELSEA_MARKET,
				style = MaterialTheme.typography.titleLarge,
				modifier = Modifier.widthIn(min = 40.dp)
			)
		}
		Spacer(modifier = Modifier.width(36.dp))
		Text(
			text = stringResource(R.string.clock_minutes),
			style = MaterialTheme.typography.displaySmall,
			color = MaterialTheme.colorScheme.secondary
		)
	}
}

private class SnoozeIntervalOptionPreviewParams :
	CollectionPreviewParameterProvider<SnoozeIntervalOption>(
		listOf(
			SnoozeIntervalOption.IntervalTenMinutes,
			SnoozeIntervalOption.IntervalCustomMinutes(30)
		)
	)

@PreviewLightDark
@Composable
private fun SnoozeIntervalPickerPreview(
	@PreviewParameter(SnoozeIntervalOptionPreviewParams::class)
	interval: SnoozeIntervalOption,
) = ClockAppTheme {
	Surface {
		SnoozeIntervalPicker(
			interval = interval,
			onIntervalChange = {},
		)
	}
}