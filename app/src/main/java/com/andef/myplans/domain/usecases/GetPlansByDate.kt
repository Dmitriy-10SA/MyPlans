package com.andef.myplans.domain.usecases

import android.app.Application
import androidx.lifecycle.LiveData
import com.andef.myplans.data.repository.PlanRepositoryImpl
import com.andef.myplans.domain.entities.Plan

object GetPlansByDate {
    fun execute(application: Application, date: String): LiveData<List<Plan>> {
       return PlanRepositoryImpl.getInstance(application).getPlansByDate(date)
    }
}