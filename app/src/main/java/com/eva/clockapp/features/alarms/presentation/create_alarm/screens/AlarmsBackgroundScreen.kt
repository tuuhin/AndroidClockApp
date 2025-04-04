package com.eva.clockapp.features.alarms.presentation.create_alarm.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.eva.clockapp.R
import com.eva.clockapp.core.presentation.LocalSnackBarHostState
import com.eva.clockapp.features.alarms.domain.models.WallpaperPhoto
import com.eva.clockapp.features.alarms.presentation.composables.BackgroundScreenTopBar
import com.eva.clockapp.features.alarms.presentation.composables.SelectBackgroundScreenContent
import com.eva.clockapp.features.alarms.presentation.create_alarm.state.AlarmsBackgroundState
import com.eva.clockapp.features.alarms.presentation.create_alarm.state.CreateAlarmEvents
import com.eva.clockapp.features.alarms.presentation.create_alarm.state.CreateAlarmState
import com.eva.clockapp.features.alarms.presentation.util.AlarmPreviewFakes
import com.eva.clockapp.ui.theme.ClockAppTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.datetime.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AlarmsBackgroundScreen(
	isItemsLoaded: Boolean,
	wallpaperOptions: ImmutableList<WallpaperPhoto>,
	onSelectUri: (String?) -> Unit,
	modifier: Modifier = Modifier,
	selectedBackground: String? = null,
	alarmTime: LocalTime = LocalTime(0, 0),
	alarmLabelText: String? = null,
	navigation: @Composable () -> Unit = {},
	onNavigateToPreviewScreen: () -> Unit = {},
	onOpenGallery: () -> Unit = {},
) {

	val snackBarHostState = LocalSnackBarHostState.current
	val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()


	Scaffold(
		topBar = {
			BackgroundScreenTopBar(
				onOpenGallery = onOpenGallery,
				navigation = navigation,
				scrollBehavior = scrollBehavior
			)
		},
		snackbarHost = { SnackbarHost(snackBarHostState) },
		modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
	) { scPadding ->
		SelectBackgroundScreenContent(
			isLoaded = isItemsLoaded,
			selectedUri = selectedBackground,
			options = wallpaperOptions,
			startTime = alarmTime,
			onSelectUri = onSelectUri,
			labelText = alarmLabelText,
			onPreviewAlarm = onNavigateToPreviewScreen,
			modifier = Modifier
				.padding(scPadding)
				.padding(dimensionResource(R.dimen.sc_padding))
				.fillMaxSize()
		)
	}
}

@Composable
fun AlarmsBackgroundScreen(
	state: CreateAlarmState,
	wallpapersState: AlarmsBackgroundState,
	onEvent: (CreateAlarmEvents) -> Unit,
	modifier: Modifier = Modifier,
	navigation: @Composable () -> Unit = {},
	onSelectFromDevice: () -> Unit = {},
	onNavigateToPreviewScreen: () -> Unit = {},
) = AlarmsBackgroundScreen(
	isItemsLoaded = wallpapersState.isLoaded,
	wallpaperOptions = wallpapersState.options,
	selectedBackground = state.backgroundImageUri,
	alarmLabelText = state.labelState,
	alarmTime = state.selectedTime,
	onSelectUri = { onEvent(CreateAlarmEvents.OnSelectUriForBackground(it)) },
	modifier = modifier,
	navigation = navigation,
	onOpenGallery = onSelectFromDevice,
	onNavigateToPreviewScreen = onNavigateToPreviewScreen
)


@PreviewLightDark
@Composable
private fun AlarmsBackgroundScreenPreview() = ClockAppTheme {
	AlarmsBackgroundScreen(
		isItemsLoaded = true,
		wallpaperOptions = AlarmPreviewFakes.RANDOM_BACKGROUND_OPTIONS,
		onSelectUri = {},
		navigation = {
			Icon(
				imageVector = Icons.AutoMirrored.Default.ArrowBack,
				contentDescription = stringResource(R.string.back_arrow)
			)
		},
	)
}