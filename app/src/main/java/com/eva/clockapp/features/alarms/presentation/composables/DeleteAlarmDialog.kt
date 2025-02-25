package com.eva.clockapp.features.alarms.presentation.composables

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.DialogProperties
import com.eva.clockapp.R

@Composable
fun DeleteAlarmsDialog(
	showDialog: Boolean,
	onDismiss: () -> Unit,
	onConfirm: () -> Unit,
	modifier: Modifier = Modifier,
	shape: Shape = AlertDialogDefaults.shape,
	properties: DialogProperties = DialogProperties(),
) {
	if (!showDialog) return

	AlertDialog(
		onDismissRequest = onDismiss,
		confirmButton = {
			TextButton(
				onClick = onConfirm,
				colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
			) {
				Text(text = stringResource(R.string.delete_action))
			}
		},
		dismissButton = {
			TextButton(onClick = onDismiss) {
				Text(text = stringResource(R.string.cancel_action))
			}
		},
		title = { Text(text = stringResource(R.string.delete_alarm_dialog_title)) },
		text = { Text(text = stringResource(R.string.delete_alarm_dialog_desc)) },
		shape = shape,
		properties = properties,
		modifier = modifier
	)
}