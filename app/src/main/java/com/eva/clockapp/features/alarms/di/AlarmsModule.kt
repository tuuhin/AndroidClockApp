package com.eva.clockapp.features.alarms.di

import com.eva.clockapp.features.alarms.data.controllers.AppRingtoneProviderImpl
import com.eva.clockapp.features.alarms.data.controllers.ContentRingtoneProviderImpl
import com.eva.clockapp.features.alarms.data.controllers.VibrationControllerImpl
import com.eva.clockapp.features.alarms.domain.controllers.AppRingtoneProvider
import com.eva.clockapp.features.alarms.domain.controllers.ContentRingtoneProvider
import com.eva.clockapp.features.alarms.domain.controllers.VibrationController
import com.eva.clockapp.features.alarms.presentation.create_alarm.CreateAlarmViewModel
import com.eva.clockapp.features.alarms.presentation.alarms.AlarmsViewModel
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val alarmsModule = module {

	// singletons
	singleOf(::VibrationControllerImpl) bind VibrationController::class
	singleOf(::ContentRingtoneProviderImpl) bind ContentRingtoneProvider::class
	singleOf(::AppRingtoneProviderImpl) bind AppRingtoneProvider::class

	// view models
	viewModelOf(::CreateAlarmViewModel)
	viewModelOf(::AlarmsViewModel)
}