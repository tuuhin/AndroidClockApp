package com.eva.clockapp.core.utils

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker

val Context.checkMusicReadPermission: Boolean
	get() {
		val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
			ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO)
		else
			ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
		return permission == PermissionChecker.PERMISSION_GRANTED
	}