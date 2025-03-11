package com.eva.clockapp.core.utils

sealed class Resource<out S, out E> {

	data class Success<out S, out E>(
		val data: S,
		val message: String? = null,
	) : Resource<S, E>()

	data class Error<out S, out E : Exception>(
		val error: E,
		val message: String? = null,
		val data: S? = null,
	) : Resource<S, E>()

	data object Loading : Resource<Nothing, Nothing>()

	inline fun fold(
		onSuccess: (S) -> Unit = {},
		onError: (E, String?) -> Unit = { _, _ -> },
		onLoading: () -> Unit = {},
	) {
		when (this) {
			is Error -> onError(this.error, this.message)
			Loading -> onLoading()
			is Success -> onSuccess(this.data)
		}
	}

}