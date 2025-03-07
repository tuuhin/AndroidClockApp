package com.eva.clockapp.features.alarms.presentation.composables

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NotificationImportant
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.eva.clockapp.R
import com.eva.clockapp.ui.theme.ClockAppTheme

@Composable
fun HasScheduleAlarmPermissionsDialog(
	modifier: Modifier = Modifier,
	onChange: (Boolean) -> Unit = {},
) {
	if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return

	val context = LocalContext.current
	val lifecycleOwner = LocalLifecycleOwner.current

	val hasPermission = remember {
		val alarmManager = context.getSystemService<AlarmManager>()
		alarmManager?.canScheduleExactAlarms() == true
	}

	var showDialog by remember { mutableStateOf(false) }

	if (hasPermission || !showDialog) return

	DisposableEffect(lifecycleOwner) {

		val receiver = object : BroadcastReceiver() {
			override fun onReceive(context: Context, intent: Intent) {
				if (intent.action != AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED) return
				val manager = context.getSystemService<AlarmManager>()
				val canSchedule = manager?.canScheduleExactAlarms() == true
				onChange(canSchedule)
				showDialog = canSchedule
			}
		}

		ContextCompat.registerReceiver(
			context,
			receiver,
			IntentFilter(AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED),
			ContextCompat.RECEIVER_NOT_EXPORTED
		)
		onDispose {
			context.unregisterReceiver(receiver)
		}
	}

	ScheduleAlarmsPermissionDialog(
		onCheckPermission = {
			try {
				context.startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
			} catch (e: Exception) {
				e.printStackTrace()
				val message = context.getString(R.string.cannot_start_activity_error)
				Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
			}
		},
		modifier = modifier,
	)
}

@Composable
private fun ScheduleAlarmsPermissionDialog(
	modifier: Modifier = Modifier,
	onCheckPermission: () -> Unit,
) {
	AlertDialog(
		onDismissRequest = {},
		confirmButton = {
			Button(
				onClick = onCheckPermission,
				colors = ButtonDefaults.buttonColors(
					containerColor = MaterialTheme.colorScheme.secondaryContainer,
					contentColor = MaterialTheme.colorScheme.onSecondaryContainer
				)
			) {
				Text(text = stringResource(R.string.action_allow_permission))
			}
		},
		title = { Text(text = stringResource(R.string.schedule_alarms_permission_not_found_dialog_title)) },
		text = {
			Text(
				text = stringResource(R.string.schedule_alarms_permission_not_found_dialog_text),
				textAlign = TextAlign.Center
			)
		},
		icon = {
			Icon(
				imageVector = Icons.Outlined.NotificationImportant,
				contentDescription = null
			)
		},
		modifier = modifier,
	)
}

@PreviewLightDark
@Composable
private fun ScheduleAlarmsPermissionDialogPreview() = ClockAppTheme {
	ScheduleAlarmsPermissionDialog(onCheckPermission = {})
}