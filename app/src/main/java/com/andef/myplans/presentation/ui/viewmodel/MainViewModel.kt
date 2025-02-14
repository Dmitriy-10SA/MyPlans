package com.andef.myplans.presentation.ui.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.andef.myplans.domain.entities.Plan
import com.andef.myplans.domain.usecases.GetPlansByDateUseCase
import com.andef.myplans.domain.usecases.GetPlansUseCase
import com.andef.myplans.domain.usecases.RemovePlanUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val getPlansUseCase: GetPlansUseCase,
    private val removePlanUseCase: RemovePlanUseCase,
    private val getPlansByDateUseCase: GetPlansByDateUseCase
) : ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    private val _plansInPlans = MutableLiveData<List<Plan>>()
    val plansInPlans: LiveData<List<Plan>> = _plansInPlans

    private val _plansInCalendar = MutableLiveData<List<Plan>>()
    val plansInCalendar: LiveData<List<Plan>> = _plansInCalendar

    fun loadPlansInPlans(lifecycleOwner: LifecycleOwner) {
        getPlansUseCase.execute().observe(lifecycleOwner) {
            _plansInPlans.value = it
        }
    }

    fun removePlan(id: Int) {
        val disposable = removePlanUseCase.execute(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
        compositeDisposable.add(disposable)
    }

    fun loadPlansByDate(lifecycleOwner: LifecycleOwner, date: String) {
        getPlansByDateUseCase.execute(date).observe(lifecycleOwner) {
            _plansInCalendar.value = it
        }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}