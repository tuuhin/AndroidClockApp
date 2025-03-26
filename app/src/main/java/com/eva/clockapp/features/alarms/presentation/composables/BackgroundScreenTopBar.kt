package com.eva.clockapp.features.alarms.presentation.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.eva.clockapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BackgroundScreenTopBar(
	onOpenGallery: () -> Unit,
	modifier: Modifier = Modifier,
	onGenerate: () -> Unit = {},
	navigation: @Composable () -> Unit,
	scrollBehavior: TopAppBarScrollBehavior? = null,
) {

	var showDropDown by remember { mutableStateOf(false) }

	MediumTopAppBar(
		title = { Text(text = stringResource(R.string.select_alarms_background_title)) },
		navigationIcon = navigation,
		actions = {
			TextButton(onClick = onOpenGallery) {
				Text(text = stringResource(R.string.select_image_from_gallery))
			}
			Box {
				IconButton(onClick = { showDropDown = true }) {
					Icon(Icons.Default.MoreVert, contentDescription = null)
				}
				DropdownMenu(
					expanded = showDropDown,
					shape = MaterialTheme.shapes.large,
					onDismissRequest = { showDropDown = false },
				) {
					DropdownMenuItem(
						text = { Text(text = stringResource(R.string.select_image_generate)) },
						onClick = onGenerate,
						enabled = false
					)
				}
			}
		},
		modifier = modifier,
		scrollBehavior = scrollBehavior
	)
}