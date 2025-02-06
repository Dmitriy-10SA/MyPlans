package com.andef.myplans.presentation.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.andef.myplans.domain.entities.Plan
import com.andef.myplans.domain.usecases.GetPlans
import com.andef.myplans.domain.usecases.GetPlansByDate
import com.andef.myplans.domain.usecases.RemovePlan
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val compositeDisposable = CompositeDisposable()

    private val _plansInPlans = MutableLiveData<List<Plan>>()
    val plansInPlans: LiveData<List<Plan>> = _plansInPlans

    private val _plansInCalendar = MutableLiveData<List<Plan>>()
    val plansInCalendar: LiveData<List<Plan>> = _plansInCalendar

    fun loadPlansInPlans(lifecycleOwner: LifecycleOwner) {
        GetPlans.execute(getApplication()).observe(lifecycleOwner) {
            _plansInPlans.value = it
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
            _plansInCalendar.value = it
        }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}