package com.eva.clockapp.features.alarms.presentation.composables

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import com.eva.clockapp.R
import com.eva.clockapp.core.utils.checkPostNotificationPermission
import com.eva.clockapp.ui.theme.ClockAppTheme

@Composable
fun HasNotificationPermission(
	modifier: Modifier = Modifier,
) {
	val context = LocalContext.current

	var hasPermission by remember {
		mutableStateOf(context.checkPostNotificationPermission)
	}

	if (hasPermission) return

	val launcher = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.RequestPermission(),
		onResult = { isGranted -> hasPermission = isGranted },
	)

	NotificationPermissionDialog(
		onCheckPermission = {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
				launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
			}
		},
		modifier = modifier,
	)
}

@Composable
fun NotificationPermissionDialog(
	onCheckPermission: () -> Unit,
	modifier: Modifier = Modifier,
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
		title = { Text(text = stringResource(R.string.notification_permission_not_found_dialog_title)) },
		text = {
			Text(
				text = stringResource(R.string.notification_permission_not_found_dialog_text),
				textAlign = TextAlign.Center
			)
		},
		icon = {
			Icon(
				imageVector = Icons.Outlined.Notifications,
				contentDescription = null
			)
		},
		modifier = modifier,
	)
}

@PreviewLightDark
@Composable
private fun NotificationPermissionDialogPreview() = ClockAppTheme {
	NotificationPermissionDialog(onCheckPermission = {})
}