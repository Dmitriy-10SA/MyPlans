package com.andef.myplans.presentation.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.andef.myplans.domain.entities.Plan
import com.andef.myplans.domain.usecases.GetPlans
import com.andef.myplans.domain.usecases.GetPlansByDate

class MainViewModel(application: Application): AndroidViewModel(application) {
    val plansInPlans = MutableLiveData<List<Plan>>()
    val plansInCalendar = MutableLiveData<List<Plan>>()

    fun loadPlansInPlans(lifecycleOwner: LifecycleOwner) {
        GetPlans.execute(getApplication()).observe(lifecycleOwner) {
            plansInPlans.value = it
        }
    }

    fun loadPlansByDate(lifecycleOwner: LifecycleOwner, date: String) {
        GetPlansByDate.execute(getApplication(), date).observe(lifecycleOwner) {
            plansInCalendar.value = it
        }
    }
}