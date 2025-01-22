package com.andef.myplans.presentation.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.andef.myplans.domain.entities.Plan
import com.andef.myplans.domain.usecases.GetPlans
import com.andef.myplans.domain.usecases.GetPlansByDate
import com.andef.myplans.domain.usecases.RemovePlan
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MainViewModel(application: Application): AndroidViewModel(application) {
    private val compositeDisposable = CompositeDisposable()

    val plansInPlans = MutableLiveData<List<Plan>>()
    val plansInCalendar = MutableLiveData<List<Plan>>()

    fun loadPlansInPlans(lifecycleOwner: LifecycleOwner) {
        GetPlans.execute(getApplication()).observe(lifecycleOwner) {
            plansInPlans.value = it
        }
    }

    fun removePlan(id: Int) {
        val disposable = RemovePlan.execute(getApplication(), id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
        compositeDisposable.add(disposable)
    }

    fun loadPlansByDate(lifecycleOwner: LifecycleOwner, date: String) {
        GetPlansByDate.execute(getApplication(), date).observe(lifecycleOwner) {
            plansInCalendar.value = it
        }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}