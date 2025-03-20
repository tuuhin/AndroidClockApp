package com.eva.clockapp.features.alarms.presentation.gallery

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.eva.clockapp.R
import com.eva.clockapp.core.presentation.LocalSnackBarHostState
import com.eva.clockapp.core.utils.checkImageReadPermission
import com.eva.clockapp.features.alarms.domain.models.GalleryBucketModel
import com.eva.clockapp.features.alarms.domain.models.GalleryImageModel
import com.eva.clockapp.features.alarms.presentation.gallery.composables.GalleryImagesCompactGrid
import com.eva.clockapp.features.alarms.presentation.gallery.composables.GalleryImagesScreenContent
import com.eva.clockapp.features.alarms.presentation.gallery.composables.ImagesPermissionPlaceholder
import com.eva.clockapp.features.alarms.presentation.gallery.state.GalleryScreenEvent
import com.eva.clockapp.features.alarms.presentation.gallery.state.GalleryScreenState
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GalleryImageScreen(
	images: ImmutableList<GalleryImageModel>,
	buckets: ImmutableList<GalleryBucketModel>,
	onSelectImage: (GalleryImageModel) -> Unit,
	onPermissionChange: (Boolean) -> Unit,
	modifier: Modifier = Modifier,
	selectedBucket: GalleryBucketModel? = null,
	showSheet: Boolean = false,
	queriedImages: ImmutableList<GalleryImageModel> = persistentListOf(),
	onDismissModalSheet: () -> Unit = {},
	onSelectAlbum: (GalleryBucketModel) -> Unit = {},
	navigation: @Composable () -> Unit = {},
) {
	val context = LocalContext.current
	val snackBarHostState = LocalSnackBarHostState.current
	val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

	var hasPermission by remember { mutableStateOf(context.checkImageReadPermission) }

	val launcher = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.RequestPermission(),
		onResult = { granted ->
			onPermissionChange(granted)
			hasPermission = granted
		},
	)

	val scope = rememberCoroutineScope()
	val sheetState = rememberModalBottomSheetState()

	if (showSheet && sheetState.isVisible) {
		ModalBottomSheet(
			onDismissRequest = onDismissModalSheet,
			sheetState = sheetState
		) {
			Column(
				modifier = Modifier.padding(dimensionResource(R.dimen.bottom_sheet_content_padding)),
				verticalArrangement = Arrangement.spacedBy(12.dp)
			) {
				selectedBucket?.let { bucket ->
					Text(
						text = bucket.bucketName,
						style = MaterialTheme.typography.titleLarge
					)
					GalleryImagesCompactGrid(
						items = queriedImages,
						onSelectImage = onSelectImage,
						modifier = Modifier.fillMaxSize(),
					)
				}
			}
		}
	}

	Scaffold(
		topBar = {
			MediumTopAppBar(
				title = { Text(text = stringResource(R.string.image_gallery_screen_title)) },
				navigationIcon = navigation,
				scrollBehavior = scrollBehavior,
			)
		},
		snackbarHost = { SnackbarHost(snackBarHostState) },
		modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
	) { scPadding ->
		Crossfade(
			targetState = hasPermission,
			modifier = Modifier
				.padding(scPadding)
				.fillMaxSize()
		) { granted ->
			if (granted) GalleryImagesScreenContent(
				images = images,
				buckets = buckets,
				onSelectImage = onSelectImage,
				onSelectAlbum = { bucket ->
					onSelectAlbum(bucket)
					// show the sheet
					scope.launch {
						sheetState.show()
					}
				},
				contentPadding = PaddingValues(horizontal = dimensionResource(R.dimen.sc_padding))
			)
			else ImagesPermissionPlaceholder(
				onAllowPermission = {
					val perms = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
						Manifest.permission.READ_MEDIA_IMAGES
					else Manifest.permission.READ_EXTERNAL_STORAGE
					launcher.launch(perms)
				},
				modifier = Modifier.fillMaxSize(),
			)
		}
	}
}


@Composable
fun GalleryImageScreen(
	state: GalleryScreenState,
	onEvent: (GalleryScreenEvent) -> Unit,
	onSelectImage: (GalleryImageModel) -> Unit,
	modifier: Modifier = Modifier,
	navigation: @Composable () -> Unit = {},
) {
	GalleryImageScreen(
		images = state.allImages,
		buckets = state.buckets,
		queriedImages = state.results,
		selectedBucket = state.selectedBucket,
		showSheet = state.showSheet,
		onSelectImage = onSelectImage,
		onPermissionChange = { onEvent(GalleryScreenEvent.LoadImages) },
		onDismissModalSheet = { onEvent(GalleryScreenEvent.OnDismissModalSheet) },
		onSelectAlbum = { onEvent(GalleryScreenEvent.OnSelectAlbum(it)) },
		modifier = modifier,
		navigation = navigation,
	)
}