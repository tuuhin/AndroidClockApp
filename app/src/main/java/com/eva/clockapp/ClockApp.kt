package com.eva.clockapp

import android.app.Application
import com.eva.clockapp.core.di.commonModule
import com.eva.clockapp.features.alarms.di.alarmsModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin

class ClockApp : Application() {

	override fun onCreate() {
		super.onCreate()

		val modules = listOf(alarmsModule, commonModule)

		startKoin {
			androidLogger()
			androidContext(this@ClockApp)
			modules(modules)
		}
	}
}