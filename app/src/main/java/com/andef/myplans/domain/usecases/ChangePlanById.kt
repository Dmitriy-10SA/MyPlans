package com.andef.myplans.domain.usecases

import android.app.Application
import com.andef.myplans.data.repository.PlanRepositoryImpl
import com.andef.myplans.domain.entities.Importance
import io.reactivex.rxjava3.core.Completable

object ChangePlanById {
    fun execute(
        application: Application,
        id: Int,
        title: String,
        date: String,
        importance: Importance
    ): Completable {
        return PlanRepositoryImpl.getInstance(application).changePlanById(
            id,
            title,
            date,
            importance
        )
    }
}