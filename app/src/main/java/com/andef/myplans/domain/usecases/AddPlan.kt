package com.andef.myplans.domain.usecases

import android.app.Application
import com.andef.myplans.data.repository.PlanRepositoryImpl
import com.andef.myplans.domain.entities.Plan
import io.reactivex.Completable

object AddPlan {
    fun execute(application: Application, plan: Plan): Completable {
        return PlanRepositoryImpl.getInstance(application).add(plan)
    }
}