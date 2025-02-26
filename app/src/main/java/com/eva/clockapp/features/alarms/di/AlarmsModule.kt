package com.eva.clockapp.features.alarms.di

import com.eva.clockapp.features.alarms.data.controllers.AlarmSoundPlayerImpl
import com.eva.clockapp.features.alarms.data.controllers.AppRingtoneProviderImpl
import com.eva.clockapp.features.alarms.data.controllers.ContentRingtoneProviderImpl
import com.eva.clockapp.features.alarms.data.controllers.VibrationControllerImpl
import com.eva.clockapp.features.alarms.data.repository.AlarmsRepositoryImpl
import com.eva.clockapp.features.alarms.domain.controllers.AlarmsSoundPlayer
import com.eva.clockapp.features.alarms.domain.controllers.AppRingtoneProvider
import com.eva.clockapp.features.alarms.domain.controllers.ContentRingtoneProvider
import com.eva.clockapp.features.alarms.domain.controllers.VibrationController
import com.eva.clockapp.features.alarms.domain.repository.AlarmsRepository
import com.eva.clockapp.features.alarms.presentation.alarms.AlarmsViewModel
import com.eva.clockapp.features.alarms.presentation.create_alarm.CreateAlarmViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val alarmsModule = module {

	// singletons
	singleOf(::VibrationControllerImpl).bind<VibrationController>()
	singleOf(::ContentRingtoneProviderImpl).bind<ContentRingtoneProvider>()
	singleOf(::AppRingtoneProviderImpl).bind<AppRingtoneProvider>()
	singleOf(::AlarmSoundPlayerImpl).bind<AlarmsSoundPlayer>()

	//factory
	factoryOf(::AlarmsRepositoryImpl).bind<AlarmsRepository>()

	// view models
	viewModelOf(::CreateAlarmViewModel)
	viewModelOf(::AlarmsViewModel)
}