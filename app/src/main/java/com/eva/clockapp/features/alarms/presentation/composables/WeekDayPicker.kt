package com.eva.clockapp.features.alarms.presentation.composables

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.EaseIn
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.datasource.CollectionPreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.eva.clockapp.R
import com.eva.clockapp.features.settings.data.utils.weekEntries
import com.eva.clockapp.features.settings.domain.models.StartOfWeekOptions
import com.eva.clockapp.ui.theme.ClockAppTheme
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toPersistentSet
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import java.util.Locale
import kotlin.time.Duration.Companion.days
import java.time.format.TextStyle as FormatStyles


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WeekDayPicker(
	selectedDays: ImmutableSet<DayOfWeek>,
	onSelectDay: (DayOfWeek) -> Unit,
	modifier: Modifier = Modifier,
	startOfWeek: StartOfWeekOptions = StartOfWeekOptions.SYSTEM_DEFAULT,
	selectedWeekDayColor: Color = MaterialTheme.colorScheme.secondaryContainer,
	unSelectedWeekDayColor: Color = Color.Transparent,
	textStyle: TextStyle = MaterialTheme.typography.titleMedium,
) {
	val context = LocalContext.current
	val locale = remember { Locale.getDefault() }

	val tomorrow = remember {
		val timeZone = TimeZone.currentSystemDefault()
		Clock.System.now().plus(1.days)
			.toLocalDateTime(timeZone)
	}


	val selectedDayText = remember(selectedDays) {

		val isOnlyTomorrowSelected =
			selectedDays.size == 1 && selectedDays.first() == tomorrow.dayOfWeek

		when {
			selectedDays.isEmpty() -> context.getString(R.string.select_alarm_days)

			isOnlyTomorrowSelected -> buildString {
				val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
				val tomorrow = today.date.plus(DatePeriod(days = 1))

				val dayOfWeek = tomorrow.dayOfWeek
					.getDisplayName(FormatStyles.SHORT_STANDALONE, locale)
				val monthName = tomorrow.month
					.getDisplayName(FormatStyles.SHORT_STANDALONE, locale)

				append(context.getString(R.string.tomorrow))
				append("-$dayOfWeek")
				append(", ${tomorrow.dayOfMonth}")
				append(" $monthName")
			}

			selectedDays.containsAll(DayOfWeek.entries) -> context.getString(R.string.all_week_days)

			else -> buildString {
				append(context.getString(R.string.every_week_day))
				append(" ")
				val days = selectedDays.sorted()
				days.forEachIndexed { idx, week ->
					val name = week.getDisplayName(FormatStyles.SHORT_STANDALONE, locale)
					append(name)
					if (idx != days.size - 1) append(", ")
				}
			}
		}
	}

	Column(
		modifier = modifier.padding(vertical = 8.dp),
		verticalArrangement = Arrangement.spacedBy(8.dp)
	) {
		Text(
			text = selectedDayText,
			style = textStyle,
			color = MaterialTheme.colorScheme.secondary
		)
		FlowRow(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.SpaceEvenly,
		) {
			startOfWeek.weekEntries.forEach { weekDay ->

				val shortName = weekDay.getDisplayName(FormatStyles.NARROW_STANDALONE, locale)
				val isSelected = selectedDays.contains(weekDay)

				val color by animateColorAsState(
					targetValue = if (isSelected) selectedWeekDayColor else unSelectedWeekDayColor,
					animationSpec = tween(durationMillis = 200, easing = EaseIn)
				)

				Box(
					modifier = Modifier
						.size(40.dp)
						.clip(CircleShape)
						.then(
							if (!isSelected) Modifier.border(
								width = 1.dp,
								color = MaterialTheme.colorScheme.outline,
								shape = CircleShape
							) else Modifier
						)
						.background(color = color, shape = CircleShape)
						.clickable(role = Role.Checkbox) { onSelectDay(weekDay) },
					contentAlignment = Alignment.Center
				) {
					Text(
						text = shortName,
						style = MaterialTheme.typography.titleMedium,
						color = contentColorFor(color)
					)
				}
			}
		}
	}
}

private class DayOfWeekPreviewParams :
	CollectionPreviewParameterProvider<ImmutableSet<DayOfWeek>>(
		listOf(
			persistentSetOf(),
			DayOfWeek.entries.toPersistentSet(),
			persistentSetOf(DayOfWeek.MONDAY, DayOfWeek.SATURDAY)
		),
	)

@PreviewLightDark
@Composable
private fun WeekDayPickerPreview(
	@PreviewParameter(DayOfWeekPreviewParams::class)
	selectedDays: ImmutableSet<DayOfWeek>,
) = ClockAppTheme {
	Surface {
		WeekDayPicker(
			selectedDays = selectedDays,
			onSelectDay = {},
			modifier = Modifier
				.fillMaxWidth()
				.padding(10.dp)
		)
	}
}