package com.eva.clockapp.features.alarms.presentation.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonColors
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.datasource.CollectionPreviewParameterProvider
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.eva.clockapp.R
import com.eva.clockapp.features.alarms.domain.models.SnoozeIntervalOption
import com.eva.clockapp.ui.theme.ClockAppTheme

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
	val staticOptions = remember {
		setOf(
			SnoozeIntervalOption.IntervalThreeMinutes,
			SnoozeIntervalOption.IntervalTenMinutes,
			SnoozeIntervalOption.IntervalFifteenMinutes,
			SnoozeIntervalOption.IntervalThirtyMinutes
		)
	}

	val isCustomModeSelected = remember(interval) {
		interval is SnoozeIntervalOption.IntervalCustomMinutes
	}

	val clockRange = remember { 0..<60 }

	Column(
		modifier = modifier,
		verticalArrangement = Arrangement.spacedBy(2.dp)
	) {
		ListItem(
			headlineContent = { Text(text = stringResource(R.string.snooze_options_title)) },
			colors = ListItemDefaults.colors(containerColor = Color.Transparent)
		)

		staticOptions.forEach { option ->
			Row(
				verticalAlignment = Alignment.CenterVertically,
				modifier = Modifier
					.fillMaxWidth()
					.clip(MaterialTheme.shapes.medium)
					.clickable { onIntervalChange(option) }
			) {
				RadioButton(
					selected = option == interval,
					onClick = { onIntervalChange(option) },
					enabled = enabled,
					colors = optionColors,
				)
				Text(
					text = "${option.duration}",
					style = MaterialTheme.typography.bodyMedium,
					color = if (enabled) LocalContentColor.current else optionColors.disabledUnselectedColor
				)
			}
		}
		Row(
			verticalAlignment = Alignment.CenterVertically,
			modifier = Modifier
				.fillMaxWidth()
				.clip(MaterialTheme.shapes.medium)
				.clickable(
					role = Role.RadioButton,
					onClick = { onIntervalChange(SnoozeIntervalOption.IntervalCustomMinutes(0)) },
				)
		) {
			RadioButton(
				selected = isCustomModeSelected,
				onClick = { onIntervalChange(SnoozeIntervalOption.IntervalCustomMinutes(0)) },
				colors = optionColors,
				enabled = enabled,
			)
			Text(
				text = stringResource(R.string.snooze_interval_custom),
				style = MaterialTheme.typography.bodyMedium,
				color = if (enabled) LocalContentColor.current else optionColors.disabledUnselectedColor
			)
		}
		AnimatedVisibility(
			visible = isCustomModeSelected && enabled,
			enter = expandVertically() + fadeIn(),
			exit = shrinkVertically() + fadeOut(),
			modifier = Modifier.align(Alignment.Start)
		) {
			Row(
				horizontalArrangement = Arrangement.Center,
				verticalAlignment = Alignment.CenterVertically,
				modifier = Modifier
					.fillMaxWidth()
					.padding(vertical = 4.dp),
			) {
				CircularRangedNumberPicker(
					range = clockRange,
					contentColor = contentColor,
					containerColor = containerColor,
					selectedIndex = (interval as? SnoozeIntervalOption.IntervalCustomMinutes)?.minutes
						?: 0,
					elementSize = DpSize(48.dp, 48.dp),
					onFocusItem = { idx ->
						clockRange.toList().getOrNull(idx)?.let { minute ->
							onIntervalChange(SnoozeIntervalOption.IntervalCustomMinutes(minute))
						}
					},
				) { idx ->
					clockRange.toList().getOrNull(idx)?.let { minute ->
						Text(
							text = "$minute".padStart(2, '0'),
							textAlign = TextAlign.Center,
							fontFamily = FontFamily.SansSerif,
							style = MaterialTheme.typography.titleLarge,
							modifier = Modifier.widthIn(min = 40.dp)
						)
					}
				}
				Spacer(modifier = Modifier.width(36.dp))
				Text(
					text = stringResource(R.string.clock_minutes),
					style = MaterialTheme.typography.displaySmall,
					color = MaterialTheme.colorScheme.secondary
				)
			}
		}
	}
}

private class SnoozeIntervalOptionPreviewParams :
	CollectionPreviewParameterProvider<SnoozeIntervalOption>(
		listOf(
			SnoozeIntervalOption.IntervalTenMinutes,
			SnoozeIntervalOption.IntervalCustomMinutes(10)
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