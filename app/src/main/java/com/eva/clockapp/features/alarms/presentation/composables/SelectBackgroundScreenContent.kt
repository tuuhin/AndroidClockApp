package com.eva.clockapp.features.alarms.presentation.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.eva.clockapp.features.alarms.domain.models.WallpaperPhoto
import com.eva.clockapp.features.alarms.presentation.alarms.PlayAlarmsScreen
import kotlinx.collections.immutable.ImmutableList
import kotlinx.datetime.LocalTime
import kotlinx.datetime.atTime
import kotlinx.datetime.toKotlinLocalDateTime
import java.time.LocalDateTime

@Composable
fun SelectBackgroundScreenContent(
	isLoaded: Boolean,
	selectedUri: String?,
	options: ImmutableList<WallpaperPhoto>,
	onSelectUri: (String?) -> Unit,
	modifier: Modifier = Modifier,
	alarmTime: LocalTime = LocalTime(0, 0),
) {
	val config = LocalConfiguration.current

	val dateTime = remember {
		LocalDateTime.now().toKotlinLocalDateTime().date
			.atTime(alarmTime)
	}

	val aspectRatio = remember(config) {
		config.screenWidthDp.toFloat() / config.screenHeightDp
	}

	Column(
		modifier = modifier,
		verticalArrangement = Arrangement.spacedBy(12.dp),
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		PlayAlarmsScreen(
			dateTime = dateTime,
			onSnoozeAlarm = {},
			onStopAlarm = {},
			isActionEnabled = false,
			backgroundImage = selectedUri,
			shape = MaterialTheme.shapes.extraLarge,
			borderStroke = BorderStroke(1.dp, color = MaterialTheme.colorScheme.primary),
			modifier = Modifier
				.fillMaxSize()
				.aspectRatio(aspectRatio)
				.scale(.5f)
				.weight(1f)
		)
		AnimatedVisibility(
			visible = isLoaded,
			enter = slideInVertically { height -> height / 2 },
			exit = slideOutVertically { height -> height / 2 }
		) {
			BackgroundImageSelector(
				imageOptions = options,
				onSelectImage = onSelectUri,
				modifier = Modifier.fillMaxWidth()
			)
		}
	}
}