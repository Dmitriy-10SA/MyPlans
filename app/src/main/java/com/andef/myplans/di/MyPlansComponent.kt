package com.andef.myplans.di

import android.app.Application
import com.andef.myplans.presentation.ui.activity.PlanActivity
import com.andef.myplans.presentation.ui.fragment.CalendarFragment
import com.andef.myplans.presentation.ui.fragment.PlansFragment
import dagger.BindsInstance
import dagger.Component

@ApplicationScope
@Component(modules = [PlanRepositoryModule::class, PlanDaoModule::class, ViewModelModule::class])
interface MyPlansComponent {
    fun inject(plansFragment: PlansFragment)
    fun inject(planActivity: PlanActivity)
    fun inject(calendarFragment: CalendarFragment)

    @Component.Factory
    interface MyPlansFactory {
        fun create(@BindsInstance application: Application): MyPlansComponent
    }
}