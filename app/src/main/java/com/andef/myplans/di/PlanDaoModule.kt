package com.andef.myplans.di

import android.app.Application
import com.andef.myplans.data.dao.PlanDao
import com.andef.myplans.data.datasource.PlanDataBase
import dagger.Module
import dagger.Provides

@Module
class PlanDaoModule {
    @Provides
    fun providePlanDao(application: Application): PlanDao {
        return PlanDataBase.getInstance(application).planDao
    }
}