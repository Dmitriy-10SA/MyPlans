package com.andef.myplans.domain.usecases

import android.app.Application
import androidx.lifecycle.LiveData
import com.andef.myplans.data.repository.PlanRepositoryImpl
import com.andef.myplans.domain.entities.Plan

object GetPlansById {
    fun execute(application: Application, id: Int): LiveData<List<Plan>> {
       return PlanRepositoryImpl.getInstance(application).getPlansById(id)
    }
}