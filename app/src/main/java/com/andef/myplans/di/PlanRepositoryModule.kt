package com.andef.myplans.di

import com.andef.myplans.data.repository.PlanRepositoryImpl
import com.andef.myplans.domain.repository.PlanRepository
import dagger.Binds
import dagger.Module

@Module
interface PlanRepositoryModule {
    @Binds
    fun bindPlanRepository(impl: PlanRepositoryImpl): PlanRepository
}