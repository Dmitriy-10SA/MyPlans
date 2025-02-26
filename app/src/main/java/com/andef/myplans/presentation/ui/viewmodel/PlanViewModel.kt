package com.andef.myplans.presentation.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.andef.myplans.domain.entities.Importance
import com.andef.myplans.domain.entities.Plan
import com.andef.myplans.domain.usecases.AddPlanUseCase
import com.andef.myplans.domain.usecases.ChangePlanByIdUseCase
import com.andef.myplans.presentation.ui.state.State
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class PlanViewModel @Inject constructor(
    private val addPlanUseCase: AddPlanUseCase,
    private val changePlanByIdUseCase: ChangePlanByIdUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(State.Initial as State)
    val state
        get() = _state.asLiveData()

    private val exceptionHandler = CoroutineExceptionHandler { _, _ ->
        _state.value = State.Error
    }

    fun addPlan(plan: Plan) {
        viewModelScope.launch(exceptionHandler) {
            addPlanUseCase.execute(plan)
        }
    }

    fun changePlan(id: Int, title: String, date: String, importance: Importance) {
        viewModelScope.launch(exceptionHandler) {
            changePlanByIdUseCase.execute(id, title, date, importance)
        }
    }
}