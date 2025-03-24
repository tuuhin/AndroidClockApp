package com.eva.clockapp.features.alarms.presentation.composables

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.core.text.util.LocalePreferences
import androidx.core.text.util.LocalePreferences.FirstDayOfWeek
import androidx.core.text.util.LocalePreferences.HourCycle
import com.eva.clockapp.R
import com.eva.clockapp.features.alarms.domain.models.AlarmsModel
import com.eva.clockapp.features.alarms.presentation.alarms.state.ContentState
import com.eva.clockapp.features.alarms.presentation.alarms.state.SelectableAlarmModel
import com.eva.clockapp.features.alarms.presentation.util.AlarmPreviewFakes
import com.eva.clockapp.ui.theme.ClockAppTheme
import kotlinx.collections.immutable.ImmutableList
import kotlin.time.Duration

@Composable
fun AlarmsScreenContent(
	isLoaded: Boolean,
	alarms: ImmutableList<SelectableAlarmModel>,
	onSelectAlarm: (AlarmsModel) -> Unit,
	onAlarmSelect: (AlarmsModel) -> Unit,
	onEnableAlarm: (isEnabled: Boolean, alarm: AlarmsModel) -> Unit,
	modifier: Modifier = Modifier,
	nextAlarmSchedule: Duration? = null,
	contentPadding: PaddingValues = PaddingValues(),
) {
	val contentState by remember(alarms, isLoaded) {
		derivedStateOf {
			if (!isLoaded) ContentState.Loading
			else if (alarms.isEmpty()) ContentState.Empty
			else ContentState.Content(alarms)
		}
	}

	Crossfade(contentState) { state ->
		when (state) {
			ContentState.Loading -> BasicLoading()
			ContentState.Empty -> EmptyAlarmsList(modifier = modifier)
			is ContentState.Content -> AlarmsListContent(
				alarms = state.data,
				duration = nextAlarmSchedule,
				onAlarmClick = onSelectAlarm,
				onAlarmSelect = onAlarmSelect,
				onEnableAlarm = onEnableAlarm,
				contentPadding = contentPadding,
				modifier = modifier
			)
		}
	}
}

@Composable
private fun BasicLoading(modifier: Modifier = Modifier) {
	Box(
		modifier = modifier,
		contentAlignment = Alignment.Center
	) {
		CircularProgressIndicator()
	}
}

@Composable
private fun EmptyAlarmsList(modifier: Modifier = Modifier) {
	Column(
		modifier = modifier,
		horizontalAlignment = Alignment.CenterHorizontally,
		verticalArrangement = Arrangement.Center,
	) {
		Image(
			painter = painterResource(R.drawable.ic_alam_clock),
			contentDescription = "Alarm Clock",
			colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.secondary),
			modifier = Modifier.size(120.dp)
		)
		Spacer(modifier = Modifier.height(24.dp))
		Text(
			text = stringResource(R.string.no_alarm_title),
			style = MaterialTheme.typography.headlineSmall,
			color = MaterialTheme.colorScheme.secondary
		)
		Text(
			text = stringResource(R.string.no_alarm_desc),
			style = MaterialTheme.typography.bodyMedium,
			color = MaterialTheme.colorScheme.secondary
		)
	}
}

@Composable
private fun AlarmsListContent(
	alarms: ImmutableList<SelectableAlarmModel>,
	onAlarmClick: (AlarmsModel) -> Unit,
	onAlarmSelect: (AlarmsModel) -> Unit,
	onEnableAlarm: (isEnabled: Boolean, alarm: AlarmsModel) -> Unit,
	modifier: Modifier = Modifier,
	duration: Duration? = null,
	contentPadding: PaddingValues = PaddingValues(),
) {
	val isInspectionMode = LocalInspectionMode.current

	val isStartOfWeekSunday = remember {
		LocalePreferences.getFirstDayOfWeek() == FirstDayOfWeek.SUNDAY
	}

	val is24HrsFormat = remember {
		LocalePreferences.getHourCycle() == HourCycle.H23
	}

	val itemKeys: ((Int, SelectableAlarmModel) -> Any)? = remember {
		if (isInspectionMode) return@remember null
		{ _, selectable -> selectable.model.id }
	}

	val contentType: (Int, SelectableAlarmModel) -> Any? = remember {
		if (isInspectionMode) return@remember { _, _ -> null }
		{ _, _ -> AlarmsModel::class }
	}

	val isAnySelected by remember(alarms) {
		derivedStateOf { alarms.any { it.isSelected } }
	}

	LazyColumn(
		contentPadding = contentPadding,
		verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.alarm_grid_spacing)),
		modifier = modifier,
	) {
		if (!isAnySelected) {
			item {
				NextAlarmText(
					duration = duration,
					modifier = Modifier
						.animateItem()
						.fillMaxWidth()
				)
			}
		}
		itemsIndexed(
			items = alarms,
			key = itemKeys,
			contentType = contentType
		) { _, selectable ->

			val alarm = selectable.model

			AlarmsCompactCard(
				selectableModel = selectable,
				isSelectableMode = isAnySelected,
				isStartOfWeekSunday = isStartOfWeekSunday,
				is24HrsFormat = is24HrsFormat,
				onClick = {
					if (!isAnySelected) onAlarmClick(alarm)
					else onAlarmSelect(alarm)
				},
				onLongClick = {
					if (!isAnySelected) onAlarmSelect(alarm)
				},
				onEnabledChange = { isEnabled -> onEnableAlarm(isEnabled, alarm) },
				modifier = Modifier
					.animateItem()
					.fillMaxWidth()
			)
		}
	}
}

@PreviewLightDark
@Composable
private fun AlarmsScreenContentPreview() = ClockAppTheme {
	Surface {
		AlarmsScreenContent(
			isLoaded = true,
			alarms = AlarmPreviewFakes.FAKE_SELECTABLE_ALARM_MODEL_LIST,
			onSelectAlarm = {},
			onAlarmSelect = {},
			onEnableAlarm = { _, _ -> },
			contentPadding = PaddingValues(20.dp),
			modifier = Modifier.fillMaxSize()
		)
	}
}