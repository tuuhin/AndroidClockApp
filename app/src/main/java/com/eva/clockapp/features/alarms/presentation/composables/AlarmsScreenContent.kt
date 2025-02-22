package com.eva.clockapp.features.alarms.presentation.composables

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.datasource.CollectionPreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.eva.clockapp.R
import com.eva.clockapp.features.alarms.domain.models.AlarmsModel
import com.eva.clockapp.features.alarms.presentation.util.AlarmPreviewFakes
import com.eva.clockapp.ui.theme.ClockAppTheme
import kotlinx.collections.immutable.ImmutableList

@Composable
fun AlarmsScreenContent(
	alarms: ImmutableList<AlarmsModel>,
	onAlarmClick: (AlarmsModel) -> Unit,
	onEnableAlarm: (isEnabled: Boolean, alarm: AlarmsModel) -> Unit,
	modifier: Modifier = Modifier,
	onCreateNew: () -> Unit = {},
	contentPadding: PaddingValues = PaddingValues(),
) {

	val isInspectionMode = LocalInspectionMode.current

	val itemKeys: ((Int, AlarmsModel) -> Any)? = remember {
		if (isInspectionMode) return@remember null
		{ _, alarm -> alarm.id }
	}

	val contentType: (Int, AlarmsModel) -> Any? = remember {
		if (isInspectionMode) return@remember { _, _ -> null }
		{ _, _ -> AlarmsModel::class }
	}

	val isAlarmsEmpty by remember(alarms) {
		derivedStateOf { alarms.isEmpty() }
	}

	Crossfade(
		targetState = isAlarmsEmpty,
		modifier = modifier
	) { isEmpty ->
		if (isEmpty) {
			NoAlarmsFoundPlaceHolder(
				onCreateNew = onCreateNew,
				modifier = Modifier.fillMaxSize()
			)
		} else {
			LazyVerticalGrid(
				columns = GridCells.Fixed(2),
				contentPadding = contentPadding,
				verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.alarm_grid_spacing)),
				horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.alarm_grid_spacing))
			) {
				itemsIndexed(
					items = alarms,
					key = itemKeys,
					contentType = contentType
				) { _, alarmModel ->
					AlarmsCompactCard(
						model = alarmModel,
						onClick = { onAlarmClick(alarmModel) },
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

private class AlarmsListPreviewParams :
	CollectionPreviewParameterProvider<ImmutableList<AlarmsModel>>(
		listOf(
			AlarmPreviewFakes.FAKE_ALARMS_MODEL_LIST,
			AlarmPreviewFakes.FAKE_ALARMS_MODEL_LIST_EMPTY
		)
	)

@PreviewLightDark
@Composable
private fun AlarmsScreenContentPreview(
	@PreviewParameter(AlarmsListPreviewParams::class)
	alarm: ImmutableList<AlarmsModel>,
) = ClockAppTheme {
	AlarmsScreenContent(
		alarms = alarm,
		onAlarmClick = {},
		onEnableAlarm = { _, _ -> },
		contentPadding = PaddingValues(20.dp),
		modifier = Modifier.fillMaxSize()
	)
}