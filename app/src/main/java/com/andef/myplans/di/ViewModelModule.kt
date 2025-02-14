package com.andef.myplans.di

import androidx.lifecycle.ViewModel
import com.andef.myplans.presentation.ui.viewmodel.AddPlanViewModel
import com.andef.myplans.presentation.ui.viewmodel.ChangePlanViewModel
import com.andef.myplans.presentation.ui.viewmodel.MainViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ViewModelModule {
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    @Binds
    fun bindMainViewModel(impl: MainViewModel): ViewModel

    @IntoMap
    @ViewModelKey(AddPlanViewModel::class)
    @Binds
    fun bindAddPlanViewModel(impl: AddPlanViewModel): ViewModel

    @IntoMap
    @ViewModelKey(ChangePlanViewModel::class)
    @Binds
    fun bindChangePlanViewModel(impl: ChangePlanViewModel): ViewModel
}