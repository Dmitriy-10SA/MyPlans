package com.andef.myplans.di

import android.app.Application
import com.andef.myplans.presentation.ui.activity.AddPlanActivity
import com.andef.myplans.presentation.ui.activity.ChangePlanActivity
import com.andef.myplans.presentation.ui.activity.MainActivity
import dagger.BindsInstance
import dagger.Component

@ApplicationScope
@Component(modules = [PlanRepositoryModule::class, PlanDaoModule::class, ViewModelModule::class])
interface MyPlansComponent {
    fun inject(activity: MainActivity)
    fun inject(activity: AddPlanActivity)
    fun inject(activity: ChangePlanActivity)

    @Component.Factory
    interface MyPlansFactory {
        fun create(@BindsInstance application: Application): MyPlansComponent
    }
}