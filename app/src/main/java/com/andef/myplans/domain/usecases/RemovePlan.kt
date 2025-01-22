package com.andef.myplans.domain.usecases

import android.app.Application
import com.andef.myplans.data.repository.PlanRepositoryImpl
import com.andef.myplans.domain.entities.Plan
import io.reactivex.Completable

object RemovePlan {
    fun execute(application: Application, id: Int): Completable {
       return PlanRepositoryImpl.getInstance(application).remove(id)
    }
}