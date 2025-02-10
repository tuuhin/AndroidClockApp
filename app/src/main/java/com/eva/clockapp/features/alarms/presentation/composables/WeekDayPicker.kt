package com.eva.clockapp.features.alarms.presentation.composables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.datasource.CollectionPreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.eva.clockapp.R
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
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun WeekDayPicker(
	selectedDays: ImmutableSet<DayOfWeek>,
	onSelectDay: (DayOfWeek) -> Unit,
	modifier: Modifier = Modifier,
) {
	val context = LocalContext.current
	val locale = remember { Locale.getDefault() }

	val selectedDayText = remember(selectedDays) {
		when {
			selectedDays.isEmpty() -> buildString {
				val today = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
				val tomorrow = today.date.plus(DatePeriod(days = 1))

				val dayOfWeek = tomorrow.dayOfWeek
					.getDisplayName(TextStyle.SHORT_STANDALONE, locale)
				val monthName = tomorrow.month
					.getDisplayName(TextStyle.SHORT_STANDALONE, locale)

				append(context.getString(R.string.tomorrow))
				append("-$dayOfWeek")
				append(", ${tomorrow.dayOfMonth}")
				append(" $monthName")
			}

			selectedDays.containsAll(DayOfWeek.entries) -> context.getString(R.string.all_week_days)

			else -> buildString {
				append(context.getString(R.string.every_week_day))
				val days = selectedDays.sorted()
				days.forEachIndexed { idx, week ->
					val name = week.getDisplayName(TextStyle.SHORT_STANDALONE, locale)
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
			style = MaterialTheme.typography.titleMedium,
			color = MaterialTheme.colorScheme.secondary
		)
		LazyRow(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.spacedBy(6.dp)
		) {
			itemsIndexed(
				items = DayOfWeek.entries,
				key = { _, weekday -> weekday.value },
				contentType = { _, _ -> DayOfWeek::class.simpleName },
			) { _, weekDay ->

				val shortName = weekDay.getDisplayName(TextStyle.SHORT_STANDALONE, locale)

				FilterChip(
					selected = selectedDays.contains(weekDay),
					onClick = { onSelectDay(weekDay) },
					label = { Text(shortName) },
					border = FilterChipDefaults.filterChipBorder(
						enabled = true,
						selected = selectedDays.contains(weekDay),
						selectedBorderWidth = 1.5.dp
					),
					shape = MaterialTheme.shapes.large,
					colors = FilterChipDefaults.filterChipColors(
						selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
						selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
					)
				)
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
			modifier = Modifier.padding(10.dp)
		)
	}
}