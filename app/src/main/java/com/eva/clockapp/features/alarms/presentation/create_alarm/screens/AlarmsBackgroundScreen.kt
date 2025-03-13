package com.eva.clockapp.features.alarms.presentation.create_alarm.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import com.eva.clockapp.features.alarms.presentation.composables.BackgroundScreenTopBar
import com.eva.clockapp.features.alarms.presentation.composables.SelectBackgroundScreenContent
import com.eva.clockapp.features.alarms.presentation.create_alarm.state.CreateAlarmEvents
import com.eva.clockapp.features.alarms.presentation.util.AlarmPreviewFakes
import com.eva.clockapp.ui.theme.ClockAppTheme
import kotlinx.collections.immutable.ImmutableList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmsBackgroundScreen(
	isItemsLoaded: Boolean,
	wallpaperOptions: ImmutableList<String>,
	onEvent: (CreateAlarmEvents) -> Unit,
	modifier: Modifier = Modifier,
	selectedBackground: String? = null,
	navigation: @Composable () -> Unit = {},
) {

	val snackBarHostState = LocalSnackBarHostState.current
	val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

	val launcher = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.PickVisualMedia(),
		onResult = { selectedUri ->
			if (selectedUri != null) {
				onEvent(CreateAlarmEvents.OnSelectAlarmBackground(selectedUri.toString()))
			}
		}
	)

	Scaffold(
		topBar = {
			BackgroundScreenTopBar(
				onSelectFromDevice = {
					val requestBuilder = PickVisualMediaRequest.Builder()
						.setDefaultTab(ActivityResultContracts.PickVisualMedia.DefaultTab.PhotosTab)
						.setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
						.build()

					launcher.launch(requestBuilder)
				},
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
			backgroundOptions = wallpaperOptions,
			onSelectUri = { onEvent(CreateAlarmEvents.OnSelectAlarmBackground(it)) },
			modifier = Modifier
				.padding(scPadding)
				.padding(dimensionResource(R.dimen.sc_padding))
				.fillMaxSize()
		)
	}
}

@PreviewLightDark
@Composable
private fun AlarmsBackgroundScreenPreview() = ClockAppTheme {
	AlarmsBackgroundScreen(
		isItemsLoaded = true,
		wallpaperOptions = AlarmPreviewFakes.RANDOM_BACKGROUND_OPTIONS,
		onEvent = {},
		navigation = {
			Icon(
				imageVector = Icons.AutoMirrored.Default.ArrowBack,
				contentDescription = stringResource(R.string.back_arrow)
			)
		},
	)
}