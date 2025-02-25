package com.eva.clockapp.features.alarms.presentation.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.eva.clockapp.core.utils.HH_MM_A
import com.eva.clockapp.features.alarms.domain.models.AlarmsModel
import com.eva.clockapp.features.alarms.presentation.alarms.state.SelectableAlarmModel
import com.eva.clockapp.features.alarms.presentation.util.AlarmPreviewFakes
import com.eva.clockapp.ui.theme.ClockAppTheme
import com.eva.clockapp.ui.theme.DownloadableFonts
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime
import kotlinx.datetime.format
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AlarmsCompactCard(
	selectableModel: SelectableAlarmModel,
	onClick: () -> Unit,
	onLongClick: () -> Unit,
	onEnabledChange: (Boolean) -> Unit = {},
	modifier: Modifier = Modifier,
	isOthersSelected: Boolean = false,
	colors: CardColors = CardDefaults.cardColors(),
	shape: Shape = MaterialTheme.shapes.large,
	overlayColor: Color = MaterialTheme.colorScheme.primary,
) {

	val alarmModel = selectableModel.model

	Card(
		shape = shape,
		colors = colors,
		modifier = modifier
			.clip(shape)
			.combinedClickable(
				onClick = onClick,
				onLongClick = onLongClick,
				onLongClickLabel = "Select the item"
			),
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.padding(all = dimensionResource(R.dimen.card_internal_padding_large)),
			horizontalArrangement = Arrangement.spacedBy(4.dp),
			verticalAlignment = Alignment.CenterVertically,
		) {
			AnimatedVisibility(
				visible = isOthersSelected,
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
			AlarmsCardTimeAndDays(model = alarmModel, modifier = Modifier.weight(1f))
			AnimatedVisibility(
				visible = !isOthersSelected,
				enter = slideInHorizontally { width -> width },
				exit = slideOutHorizontally { width -> width }
			) {
				Switch(
					checked = alarmModel.isAlarmEnabled,
					onCheckedChange = onEnabledChange,
					colors = SwitchDefaults.colors(checkedTrackColor = overlayColor),
				)
			}
		}
	}
}

@Composable
private fun AlarmsCardTimeAndDays(
	model: AlarmsModel,
	modifier: Modifier = Modifier,
	textColor: Color = MaterialTheme.colorScheme.primary,
	overlayColor: Color = MaterialTheme.colorScheme.secondary,
) {
	val locale = remember { Locale.getDefault() }
	val textMeasurer = rememberTextMeasurer()

	val textFormat = remember(model.time) {
		model.time.format(LocalTime.Formats.HH_MM_A)
	}

	val rearrangedDayOfWeekEntries = remember {
		buildSet {
			add(DayOfWeek.SUNDAY)
			val others = DayOfWeek.entries.toMutableList()
				.apply { remove(DayOfWeek.SUNDAY) }
			addAll(others)
		}
	}

	Column(modifier = modifier) {
		Text(
			text = textFormat,
			style = MaterialTheme.typography.headlineMedium,
			color = textColor,
			fontFamily = DownloadableFonts.CHELSEA_MARKET
		)
		Spacer(modifier = Modifier.height(6.dp))
		Row(
			modifier = Modifier.wrapContentWidth(),
			horizontalArrangement = Arrangement.spacedBy(4.dp)
		) {
			rearrangedDayOfWeekEntries.forEach { day ->
				val weekDayTextStyle =
					MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)

				val color = if (day in model.weekDays) overlayColor
				else MaterialTheme.colorScheme.outline

				Canvas(
					modifier = Modifier.size(width = 12.dp, height = 20.dp)
				) {
					val weekDay = day.getDisplayName(TextStyle.NARROW_STANDALONE, locale)

					drawCircle(
						color = color,
						radius = 2.dp.toPx(),
						center = Offset(size.width * .5f, 0f),
					)

					val textResult = textMeasurer.measure(weekDay, style = weekDayTextStyle)
					val offset = with(textResult.size) {
						Offset(width * .5f, height * .5f)
					}

					drawText(
						textLayoutResult = textResult,
						topLeft = center - offset,
						color = color,

						)
				}
			}
		}
		Spacer(modifier = Modifier.height(4.dp))
		model.label?.let { labelText ->
			Text(
				text = labelText,
				style = MaterialTheme.typography.labelMedium,
				color = MaterialTheme.colorScheme.secondary,
				maxLines = 2,
				overflow = TextOverflow.Ellipsis,
			)
		}
	}
}

@PreviewLightDark
@Composable
private fun AlarmsCompactCardPreview() = ClockAppTheme {
	AlarmsCompactCard(
		selectableModel = AlarmPreviewFakes.FAKE_SELECTABLE_ALARM_MODEL,
		onClick = {},
		onLongClick = {},
	)
}