package com.andef.myplans.presentation.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.andef.myplans.domain.usecases.GetPlansByDateUseCase
import com.andef.myplans.domain.usecases.GetPlansUseCase
import com.andef.myplans.domain.usecases.RemovePlanUseCase
import com.andef.myplans.presentation.ui.state.State
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class CalendarFragmentViewModel @Inject constructor(
    private val removePlanUseCase: RemovePlanUseCase,
    getPlansUseCase: GetPlansUseCase,
    private val getPlansByDateUseCase: GetPlansByDateUseCase,

    ) : ViewModel() {
    private val _state = MutableStateFlow(State.Initial as State)
    val state
        get() = _state.asLiveData()

    private val _plansByDate = MutableStateFlow(State.Initial as State)
    val plansByDate
        get() = _plansByDate.asLiveData()

    private val exceptionHandler = CoroutineExceptionHandler { _, _ ->
        _state.value = State.Error
    }

    val plans = getPlansUseCase.execute()
        .map {
            State.Plans(it) as State
        }
        .catch {
            emit(State.Error)
        }
        .asLiveData()

    fun getPlansByDate(date: String) {
        viewModelScope.launch {
            getPlansByDateUseCase.execute(date).map {
                State.Plans(it) as State
            }.catch {
                emit(State.Error)
            }.collect {
                _plansByDate.value = it
            }
        }
    }

    fun removePlan(id: Int) {
        viewModelScope.launch(exceptionHandler) {
            removePlanUseCase.execute(id)
        }
    }
}