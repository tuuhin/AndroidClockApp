package com.eva.clockapp.features.timer.di

import com.eva.clockapp.features.timer.presentation.TimerViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val timerModule = module {

	//viewmodel
	viewModelOf(::TimerViewModel)
}