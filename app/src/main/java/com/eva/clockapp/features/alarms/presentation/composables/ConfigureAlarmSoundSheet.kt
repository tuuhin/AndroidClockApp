package com.eva.clockapp.features.alarms.presentation.composables

import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderColors
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.eva.clockapp.R
import com.eva.clockapp.ui.theme.ClockAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigureAlarmSoundSheet(
	isVolumeStepIncrease: Boolean,
	volume: Float,
	onVolumeChange: (Float) -> Unit,
	onVolumeStepIncreaseChange: (Boolean) -> Unit,
	modifier: Modifier = Modifier,
	sliderColors: SliderColors = SliderDefaults.colors(),
	switchColors: SwitchColors = SwitchDefaults.colors(),
	textStyle: TextStyle = MaterialTheme.typography.titleMedium,
) {

	Column(
		modifier = modifier.padding(horizontal = dimensionResource(R.dimen.sheet_padding)),
	) {
		Text(
			text = stringResource(R.string.alarm_sound_configure),
			style = MaterialTheme.typography.headlineSmall
		)
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(vertical = 4.dp),
			horizontalArrangement = Arrangement.SpaceBetween,
			verticalAlignment = Alignment.CenterVertically
		) {
			Text(
				text = stringResource(R.string.increase_volume_by_steps),
				style = textStyle,
				modifier = Modifier.weight(.9f)
			)
			Switch(
				checked = isVolumeStepIncrease,
				onCheckedChange = onVolumeStepIncreaseChange,
				colors = switchColors,
			)
		}
		Text(text = stringResource(R.string.alarm_sound_volume), style = textStyle)
		Row(
			horizontalArrangement = Arrangement.spacedBy(2.dp),
			verticalAlignment = Alignment.CenterVertically,
		) {
			Icon(
				imageVector = Icons.AutoMirrored.Outlined.VolumeUp,
				contentDescription = null,
				tint = sliderColors.thumbColor
			)
			Slider(
				value = volume,
				onValueChange = onVolumeChange,
				valueRange = 1f..100f,
				colors = sliderColors,
				modifier = Modifier.weight(1f),
				thumb = { state ->
					val interactionSource = remember { MutableInteractionSource() }
					val textMeasure = rememberTextMeasurer()

					val numberStyle = MaterialTheme.typography.labelMedium

					Spacer(
						modifier = Modifier
							.size(DpSize(28.dp, 28.dp))
							.hoverable(interactionSource = interactionSource)
							.drawBehind {
								drawCircle(
									color = sliderColors.thumbColor,
									style = Stroke(width = 2.dp.toPx())
								)
								val text = textMeasure.measure(
									text = "${state.value.toUInt()}",
									maxLines = 1,
									style = numberStyle
								)
								val halfSize = Offset(text.size.width / 2f, text.size.height / 2f)

								drawText(
									textLayoutResult = text,
									topLeft = center - halfSize,
									color = sliderColors.thumbColor
								)
							}
					)
				},
			)
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@PreviewLightDark
@Composable
private fun ConfigureAlarmSoundSheetPreview() = ClockAppTheme {
	Surface(shape = BottomSheetDefaults.ExpandedShape) {
		ConfigureAlarmSoundSheet(
			volume = 40f,
			isVolumeStepIncrease = true,
			onVolumeChange = {},
			onVolumeStepIncreaseChange = {},
		)
	}
}