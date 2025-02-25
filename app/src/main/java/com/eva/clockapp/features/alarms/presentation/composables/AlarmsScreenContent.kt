package com.eva.clockapp.features.alarms.presentation.composables

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.eva.clockapp.R
import com.eva.clockapp.features.alarms.domain.models.AlarmsModel
import com.eva.clockapp.features.alarms.presentation.alarms.state.SelectableAlarmModel
import com.eva.clockapp.features.alarms.presentation.util.AlarmPreviewFakes
import com.eva.clockapp.ui.theme.ClockAppTheme
import kotlinx.collections.immutable.ImmutableList

@Composable
fun AlarmsScreenContent(
	alarms: ImmutableList<SelectableAlarmModel>,
	onAlarmClick: (AlarmsModel) -> Unit,
	onAlarmSelect: (AlarmsModel) -> Unit,
	onEnableAlarm: (isEnabled: Boolean, alarm: AlarmsModel) -> Unit,
	modifier: Modifier = Modifier,
	onCreateNew: () -> Unit = {},
	contentPadding: PaddingValues = PaddingValues(),
) {

	val isInspectionMode = LocalInspectionMode.current

	val itemKeys: ((Int, SelectableAlarmModel) -> Any)? = remember {
		if (isInspectionMode) return@remember null
		{ _, selectable -> selectable.model.id }
	}

	val contentType: (Int, SelectableAlarmModel) -> Any? = remember {
		if (isInspectionMode) return@remember { _, _ -> null }
		{ _, _ -> AlarmsModel::class }
	}

	val isAlarmsEmpty by remember(alarms) {
		derivedStateOf { alarms.isEmpty() }
	}

	val isAnySelected by remember(alarms) {
		derivedStateOf { alarms.any { it.isSelected } }
	}

	Crossfade(
		targetState = isAlarmsEmpty,
		label = "Alarms list empty or filled",
		modifier = modifier
	) { isEmpty ->
		if (isEmpty) {
			NoAlarmsFoundPlaceHolder(
				onCreateNew = onCreateNew,
				modifier = Modifier.fillMaxSize()
			)
		} else {
			LazyColumn(
				contentPadding = contentPadding,
				verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.alarm_grid_spacing)),
			) {
				itemsIndexed(
					items = alarms,
					key = itemKeys,
					contentType = contentType
				) { _, selectable ->

					val alarmModel = selectable.model

					AlarmsCompactCard(
						selectableModel = selectable,
						isOthersSelected = isAnySelected,
						onClick = {
							if (!isAnySelected) onAlarmClick(alarmModel)
							else onAlarmSelect(alarmModel)
						},
						onLongClick = {
							if (!isAnySelected) onAlarmSelect(alarmModel)
						},
						onEnabledChange = { isEnabled -> onEnableAlarm(isEnabled, alarmModel) },
						modifier = Modifier
							.animateItem()
							.fillMaxWidth()
					)
				}
			}
		}
	}
}


@PreviewLightDark
@Composable
private fun AlarmsScreenContentPreview() = ClockAppTheme {
	AlarmsScreenContent(
		alarms = AlarmPreviewFakes.FAKE_SELECTABLE_ALARM_MODEL_LIST,
		onAlarmClick = {},
		onAlarmSelect = {},
		onEnableAlarm = { _, _ -> },
		contentPadding = PaddingValues(20.dp),
		modifier = Modifier.fillMaxSize()
	)

}