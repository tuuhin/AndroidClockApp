package com.eva.clockapp.features.alarms.presentation.composables

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AlarmOff
import androidx.compose.material.icons.outlined.AlarmOn
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.eva.clockapp.R
import com.eva.clockapp.features.alarms.presentation.alarms.state.SelectableAlarmModel
import kotlinx.collections.immutable.ImmutableList

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlarmsBottomBar(
	showBottomBar: Boolean,
	selectableAlarms: ImmutableList<SelectableAlarmModel>,
	onDelete: () -> Unit,
	modifier: Modifier = Modifier,
	onEnableAlarms: () -> Unit = {},
	onDisableAlarms: () -> Unit = {},
) {

	val isAnySelectedEnabled by remember(selectableAlarms) {
		derivedStateOf {
			val alarms = selectableAlarms.filter { it.isSelected }.map { it.model }
			alarms.any { it.isAlarmEnabled }
		}
	}

	val isAnySelectedDisabled by remember(selectableAlarms) {
		derivedStateOf {
			val alarms = selectableAlarms.filter { it.isSelected }.map { it.model }
			alarms.any { !it.isAlarmEnabled }
		}
	}

	AnimatedVisibility(
		visible = showBottomBar,
		enter = slideInVertically(),
		exit = slideOutVertically(),
		modifier = modifier
	) {
		BottomAppBar(
			floatingActionButton = {
				TooltipBox(
					positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
					tooltip = {
						PlainTooltip {
							Text(text = stringResource(R.string.delete_action))
						}
					},
					state = rememberTooltipState()
				) {
					FloatingActionButton(onClick = onDelete) {
						Icon(
							imageVector = Icons.Outlined.DeleteOutline,
							contentDescription = stringResource(R.string.delete_action)
						)
					}
				}
			},
			actions = {
				AnimatedVisibility(visible = isAnySelectedEnabled) {
					TooltipBox(
						positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
						tooltip = {
							PlainTooltip {
								Text(text = stringResource(R.string.action_turn_off_alarm))
							}
						},
						state = rememberTooltipState()
					) {
						IconButton(onClick = onDisableAlarms) {
							Icon(
								imageVector = Icons.Outlined.AlarmOff,
								contentDescription = stringResource(R.string.action_turn_on_alarm)
							)
						}
					}
				}
				AnimatedVisibility(visible = isAnySelectedDisabled) {
					TooltipBox(
						positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
						tooltip = {
							PlainTooltip {
								Text(text = stringResource(R.string.action_turn_on_alarm))
							}
						},
						state = rememberTooltipState()
					) {
						IconButton(onClick = onEnableAlarms) {
							Icon(
								imageVector = Icons.Outlined.AlarmOn,
								contentDescription = stringResource(R.string.action_turn_on_alarm)
							)
						}
					}
				}
			},
		)
	}
}