package com.eva.clockapp.features.alarms.presentation.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.eva.clockapp.R
import com.eva.clockapp.core.utils.HH_MM
import com.eva.clockapp.core.utils.HH_MM_A
import com.eva.clockapp.features.alarms.presentation.alarms.state.SelectableAlarmModel
import com.eva.clockapp.features.alarms.presentation.util.AlarmPreviewFakes
import com.eva.clockapp.features.settings.data.utils.weekEntries
import com.eva.clockapp.features.settings.domain.models.StartOfWeekOptions
import com.eva.clockapp.ui.theme.ClockAppTheme
import com.eva.clockapp.ui.theme.DownloadableFonts
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime
import kotlinx.datetime.format
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AlarmsCompactCard(
	selectableModel: SelectableAlarmModel,
	onClick: () -> Unit,
	onLongClick: () -> Unit,
	modifier: Modifier = Modifier,
	onEnabledChange: (Boolean) -> Unit = {},
	isSelectableMode: Boolean = false,
	is24HrsFormat: Boolean = false,
	startOfWeek: StartOfWeekOptions = StartOfWeekOptions.SUNDAY,
	colors: CardColors = CardDefaults.elevatedCardColors(),
	shape: Shape = MaterialTheme.shapes.large,
	overlayColor: Color = MaterialTheme.colorScheme.primary,
) {
	Card(
		shape = shape,
		colors = colors,
		modifier = modifier
			.clip(shape)
			.combinedClickable(
				onClick = onClick,
				onLongClick = onLongClick,
				onLongClickLabel = "Select alarms to bulk enable or delete",
				onClickLabel = "Click to edit alarm"
			)
			.animateContentSize(),
	) {
		Column(
			modifier = Modifier
				.fillMaxWidth()
				.padding(all = dimensionResource(R.dimen.card_internal_padding_large)),
			verticalArrangement = Arrangement.spacedBy(2.dp)
		) {
			selectableModel.model.label?.let { labelText ->
				Text(
					text = labelText,
					overflow = TextOverflow.Ellipsis,
					style = MaterialTheme.typography.labelLarge,
					color = MaterialTheme.colorScheme.secondary,
					maxLines = 2,
				)
			}
			Row(
				horizontalArrangement = Arrangement.spacedBy(4.dp),
				verticalAlignment = Alignment.CenterVertically,
			) {
				AnimatedVisibility(
					visible = isSelectableMode,
					enter = slideInHorizontally(),
					exit = slideOutHorizontally()
				) {
					RadioButton(
						selected = selectableModel.isSelected,
						onClick = {},
						colors = RadioButtonDefaults
							.colors(selectedColor = MaterialTheme.colorScheme.secondary)
					)
				}
				AlarmsTimeAndWeekdays(
					time = selectableModel.model.time,
					weekDays = selectableModel.model.weekDays.toImmutableSet(),
					is24HrsFormat = is24HrsFormat,
					startOfWeek = startOfWeek,
					modifier = Modifier.weight(1f)
				)
				AnimatedVisibility(
					visible = !isSelectableMode,
					enter = slideInHorizontally { width -> width },
					exit = slideOutHorizontally { width -> width }
				) {
					Switch(
						checked = selectableModel.model.isAlarmEnabled,
						onCheckedChange = onEnabledChange,
						colors = SwitchDefaults.colors(checkedTrackColor = overlayColor),
					)
				}
			}
		}
	}
}


@Composable
private fun AlarmsTimeAndWeekdays(
	time: LocalTime,
	weekDays: ImmutableSet<DayOfWeek>,
	modifier: Modifier = Modifier,
	is24HrsFormat: Boolean = false,
	startOfWeek: StartOfWeekOptions = StartOfWeekOptions.SUNDAY,
	textColor: Color = MaterialTheme.colorScheme.primary,
	overlayColor: Color = MaterialTheme.colorScheme.secondary,
) {
	val locale = remember { Locale.getDefault() }
	val textMeasurer = rememberTextMeasurer()

	val textFormat = remember(time, is24HrsFormat) {
		val format = if (is24HrsFormat) LocalTime.Formats.HH_MM
		else LocalTime.Formats.HH_MM_A

		time.format(format)
	}


	Column(
		modifier = modifier,
		verticalArrangement = Arrangement.spacedBy(8.dp)
	) {
		Text(
			text = textFormat,
			style = MaterialTheme.typography.headlineLarge,
			color = textColor,
			fontWeight = FontWeight.SemiBold,
			fontFamily = DownloadableFonts.CHELSEA_MARKET
		)
		Row(
			modifier = Modifier.wrapContentWidth(),
			horizontalArrangement = Arrangement.spacedBy(4.dp)
		) {
			startOfWeek.weekEntries.forEach { day ->

				val textStyle = MaterialTheme.typography.labelLarge
					.copy(fontWeight = FontWeight.SemiBold)

				val color = if (day in weekDays) overlayColor
				else MaterialTheme.colorScheme.outline

				Canvas(
					modifier = Modifier.size(width = 12.dp, height = 20.dp)
				) {
					val weekDay = day.getDisplayName(
						java.time.format.TextStyle.NARROW_STANDALONE, locale
					)

					if (day in weekDays) {
						drawCircle(
							color = color,
							radius = 2.dp.toPx(),
							center = Offset(size.width * .5f, 0f),
						)
					}

					val textResult = textMeasurer.measure(weekDay, style = textStyle)
					val offset = with(textResult.size) {
						Offset(width * .5f, height * .5f)
					}

					drawText(
						textLayoutResult = textResult,
						topLeft = center - offset,
						color = color
					)
				}
			}
		}
	}
}


@PreviewLightDark
@Composable
private fun AlarmsCompactCardNormalPreview() = ClockAppTheme {
	AlarmsCompactCard(
		selectableModel = AlarmPreviewFakes.FAKE_SELECTABLE_ALARM_MODEL,
		onClick = {},
		onLongClick = {},
	)
}

@PreviewLightDark
@Composable
private fun AlarmsCompactCardSelectedPreview() = ClockAppTheme {
	AlarmsCompactCard(
		selectableModel = AlarmPreviewFakes.FAKE_SELECTABLE_ALARM_MODEL,
		isSelectableMode = true,
		onClick = {},
		onLongClick = {},
	)
}