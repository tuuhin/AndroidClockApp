package com.eva.clockapp.features.settings.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.eva.clockapp.R
import com.eva.clockapp.core.presentation.LocalSnackBarHostState
import com.eva.clockapp.features.settings.domain.models.AlarmSettingsModel
import com.eva.clockapp.features.settings.presentation.composable.StartOfWeekSettingsItem
import com.eva.clockapp.features.settings.presentation.composable.TimeFormatSettingsItem
import com.eva.clockapp.features.settings.presentation.composable.UpcomingAlarmSettingsItem
import com.eva.clockapp.features.settings.presentation.composable.VolumeControlSettingsItem
import com.eva.clockapp.ui.theme.ClockAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmSettingsScreen(
	settings: AlarmSettingsModel,
	onEvent: (ChangeAlarmSettingsEvent) -> Unit,
	modifier: Modifier = Modifier,
	navigation: @Composable () -> Unit = {},
) {

	val snackBarHostState = LocalSnackBarHostState.current
	val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

	Scaffold(
		topBar = {
			MediumTopAppBar(
				title = { Text(text = stringResource(R.string.settings_screen_title)) },
				navigationIcon = navigation,
				scrollBehavior = scrollBehavior
			)
		},
		snackbarHost = { SnackbarHost(snackBarHostState) },
		modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
	) { scPadding ->
		LazyColumn(
			contentPadding = scPadding,
			modifier = Modifier
				.fillMaxSize()
				.padding(
					horizontal = dimensionResource(R.dimen.sc_padding),
					vertical = dimensionResource(R.dimen.sc_padding_secondary)
				),
			verticalArrangement = Arrangement.spacedBy(8.dp)
		) {
			item {
				Text(
					text = stringResource(R.string.settings_subtitle_alarms),
					color = MaterialTheme.colorScheme.primary,
					style = MaterialTheme.typography.titleMedium,
					modifier = Modifier
						.fillMaxWidth()
						.padding(vertical = 4.dp),
				)
			}
			item {
				UpcomingAlarmSettingsItem(
					option = settings.notificationTime,
					onSelectOption = { time ->
						onEvent(ChangeAlarmSettingsEvent.OnUpcomingNotificationTimeChange(time))
					},
				)
			}
			item {
				StartOfWeekSettingsItem(
					startOfWeek = settings.startOfWeek,
					onStartOfWeekChange = { startOfWeek ->
						onEvent(ChangeAlarmSettingsEvent.OnStartOfWeekChange(startOfWeek))
					},
				)
			}
			item {
				TimeFormatSettingsItem(
					option = settings.timeFormat,
					onSelectOption = { timeOption ->
						onEvent(ChangeAlarmSettingsEvent.OnTimeFormatChange(timeOption))
					},
				)
			}
			item {
				VolumeControlSettingsItem(
					option = settings.volumeControl,
					onSelectOption = { volumeOption ->
						onEvent(ChangeAlarmSettingsEvent.OnVolumeControlChange(volumeOption))
					},
				)
			}
		}
	}
}

@PreviewLightDark
@Composable
private fun AlarmSettingsScreenPreview() = ClockAppTheme {
	AlarmSettingsScreen(
		settings = AlarmSettingsModel(),
		onEvent = {},
		navigation = {
			Icon(
				imageVector = Icons.AutoMirrored.Default.ArrowBack,
				contentDescription = stringResource(R.string.back_arrow)
			)
		},
	)
}