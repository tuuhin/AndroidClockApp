package com.eva.clockapp.features.alarms.presentation.gallery

import androidx.lifecycle.viewModelScope
import com.eva.clockapp.core.presentation.AppViewModel
import com.eva.clockapp.core.presentation.UiEvents
import com.eva.clockapp.features.alarms.domain.controllers.GalleryImageProvider
import com.eva.clockapp.features.alarms.domain.exceptions.FileReadPermissionNotFound
import com.eva.clockapp.features.alarms.domain.models.GalleryBucketModel
import com.eva.clockapp.features.alarms.domain.models.GalleryImageModel
import com.eva.clockapp.features.alarms.presentation.gallery.state.GalleryScreenEvent
import com.eva.clockapp.features.alarms.presentation.gallery.state.GalleryScreenState
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GalleryScreenViewModel(
	private val provider: GalleryImageProvider,
) : AppViewModel() {

	private val _galleryImages = MutableStateFlow<List<GalleryImageModel>>(emptyList())
	private val _imageBuckets = MutableStateFlow<List<GalleryBucketModel>>(emptyList())
	private val _selectedBucket = MutableStateFlow<GalleryBucketModel?>(null)
	private val _queryResult = MutableStateFlow<List<GalleryImageModel>>(emptyList())

	val screenState = combine(
		_selectedBucket, _queryResult, _galleryImages, _imageBuckets
	) { selectedBucket, results, allImages, buckets ->
		GalleryScreenState(
			allImages = allImages.toImmutableList(),
			buckets = buckets.toImmutableList(),
			selectedBucket = selectedBucket,
			results = results.toImmutableList()
		)
	}.onStart {
		loadImages()
		loadImageBuckets()
	}.stateIn(
		scope = viewModelScope,
		started = SharingStarted.Lazily,
		initialValue = GalleryScreenState()
	)

	private val _uiEvents = MutableSharedFlow<UiEvents>()
	override val uiEvents: SharedFlow<UiEvents>
		get() = _uiEvents

	private var _loadingJob: Job? = null

	private fun loadImages() {
		// cancel it
		_loadingJob?.cancel()
		// start a new one
		_loadingJob = provider.loadItemsAsFlow.onEach { res ->
			res.fold(
				onSuccess = { images ->
					_galleryImages.update { images }
				},
				onFailure = { err ->
					if (err is FileReadPermissionNotFound) return@fold
					val event = err.message?.let { UiEvents.ShowSnackBar(it) } ?: return@fold
					_uiEvents.emit(event)
				}
			)
		}.launchIn(viewModelScope)
	}

	private fun loadImageBuckets() = viewModelScope.launch {
		provider.readImageAlbums().fold(
			onSuccess = { buckets -> _imageBuckets.update { buckets } },
			onFailure = { err ->
				val event = err.message?.let { UiEvents.ShowSnackBar(it) } ?: return@fold
				_uiEvents.emit(event)
			},
		)
	}

	fun onEvent(event: GalleryScreenEvent) {
		when (event) {
			GalleryScreenEvent.LoadImages -> {
				loadImages()
				loadImageBuckets()
			}
			GalleryScreenEvent.OnDismissModalSheet -> {
				// hide the sheet and clear the results
				_selectedBucket.update { null }
				_queryResult.update { emptyList() }
			}

			is GalleryScreenEvent.OnSelectAlbum -> {
				_selectedBucket.update { event.album }
				val selectedImages = _galleryImages.value
					.filter { it.bucketId == event.album.bucketId }

				_queryResult.update { selectedImages }
			}
		}
	}
}