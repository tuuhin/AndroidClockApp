package com.eva.clockapp.features.alarms.presentation.composables

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.eva.clockapp.R
import com.eva.clockapp.features.alarms.presentation.alarms.state.SelectableAlarmModel
import kotlinx.collections.immutable.ImmutableList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmsTopAppBar(
	selectableAlarms: ImmutableList<SelectableAlarmModel>,
	modifier: Modifier = Modifier,
	navigation: @Composable () -> Unit = {},
	onCreateNewAlarm: () -> Unit = {},
	scrollBehavior: TopAppBarScrollBehavior? = null,
	colors: TopAppBarColors = TopAppBarDefaults.mediumTopAppBarColors(),
) {

	val isAnySelected by remember(selectableAlarms) {
		derivedStateOf { selectableAlarms.any { it.isSelected } }
	}

	val selectedItemCount by remember(selectableAlarms) {
		derivedStateOf { selectableAlarms.count { it.isSelected } }
	}

	MediumTopAppBar(
		title = {
			AnimatedContent(
				targetState = isAnySelected,
				transitionSpec = { animateTopBar() },
				label = "Selectable Top bar animation",
				contentAlignment = Alignment.TopCenter,
			) { isSelected ->
				if (isSelected) Text(
					text = stringResource(R.string.n_number_selected, selectedItemCount)
				)
				else Text(text = stringResource(R.string.alarms_screen_title))
			}
		},
		actions = {
			AnimatedVisibility(
				visible = !isAnySelected,
				enter = slideInVertically(),
				exit = slideOutVertically()
			) {
				TextButton(
					onClick = onCreateNewAlarm,
					colors = ButtonDefaults
						.textButtonColors(contentColor = MaterialTheme.colorScheme.primary)
				) {
					Text(text = stringResource(R.string.create_action))
				}
			}
		},
		navigationIcon = navigation,
		scrollBehavior = scrollBehavior,
		modifier = modifier,
		colors = colors,
	)
}

private fun animateTopBar(): ContentTransform {
	val enterIn = expandIn(
		animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
		expandFrom = Alignment.TopCenter
	) + slideInVertically(
		animationSpec = tween(durationMillis = 400),
		initialOffsetY = { height -> height },
	)

	val exitOut = shrinkOut(
		animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
		shrinkTowards = Alignment.TopCenter
	) + slideOutVertically(
		animationSpec = tween(durationMillis = 400),
		targetOffsetY = { height -> -height },
	)
	return enterIn togetherWith exitOut
}
