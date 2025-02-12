package com.eva.clockapp.features.alarms.presentation.composables

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import com.eva.clockapp.R

@Composable
fun CheckReadMusicPermission(
	onPermissionChanged: (Boolean) -> Unit,
	modifier: Modifier = Modifier,
	colors: ListItemColors = ListItemDefaults.colors(),
) {
	val launcher = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.RequestPermission(),
		onResult = { granted -> onPermissionChanged(granted) }
	)

	ListItem(
		headlineContent = { Text(text = stringResource(R.string.read_music_permission_title)) },
		supportingContent = { Text(text = stringResource(R.string.read_music_permission_desc)) },
		trailingContent = {
			TextButton(
				onClick = {
					val perms = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
						Manifest.permission.READ_MEDIA_AUDIO
					else Manifest.permission.READ_EXTERNAL_STORAGE
					launcher.launch(perms)
				},
				shape = MaterialTheme.shapes.medium,
			) {
				Text(text = stringResource(id = R.string.allow_permission_short_hand))
			}
		},
		colors = colors,
		modifier = modifier.clip(MaterialTheme.shapes.medium)
	)
}
