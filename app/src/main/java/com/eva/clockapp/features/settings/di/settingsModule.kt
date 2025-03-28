package com.eva.clockapp.features.settings.di

import com.eva.clockapp.features.settings.data.datastore.AlarmSettingsRepoImpl
import com.eva.clockapp.features.settings.domain.repository.AlarmSettingsRepository
import com.eva.clockapp.features.settings.presentation.SettingsViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

val settingsModule = module {
	// factory
	factoryOf(::AlarmSettingsRepoImpl).bind<AlarmSettingsRepository>()
	// viewmodel
	viewModelOf(::SettingsViewModel)
}