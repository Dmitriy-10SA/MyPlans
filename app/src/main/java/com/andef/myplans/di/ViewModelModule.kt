package com.andef.myplans.di

import androidx.lifecycle.ViewModel
import com.andef.myplans.presentation.ui.viewmodel.CalendarFragmentViewModel
import com.andef.myplans.presentation.ui.viewmodel.PlanViewModel
import com.andef.myplans.presentation.ui.viewmodel.PlansFragmentViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ViewModelModule {
    @IntoMap
    @ViewModelKey(PlansFragmentViewModel::class)
    @Binds
    fun bindPlanFragmentViewModel(impl: PlansFragmentViewModel): ViewModel

    @IntoMap
    @ViewModelKey(PlanViewModel::class)
    @Binds
    fun bindPlanViewModel(impl: PlanViewModel): ViewModel

    @IntoMap
    @ViewModelKey(CalendarFragmentViewModel::class)
    @Binds
    fun bindCalendarFragmentViewModel(impl: CalendarFragmentViewModel): ViewModel
}