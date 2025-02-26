package com.andef.myplans.presentation.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.andef.myplans.domain.usecases.GetPlansUseCase
import com.andef.myplans.domain.usecases.RemovePlanUseCase
import com.andef.myplans.presentation.ui.state.State
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class PlansFragmentViewModel @Inject constructor(
    getPlansUseCase: GetPlansUseCase,
    private val removePlanUseCase: RemovePlanUseCase
) : ViewModel() {
    val plans = getPlansUseCase.execute()
        .map {
            State.Plans(it) as State
        }
        .catch {
            emit(State.Error)
        }
        .asLiveData()

    fun removePlan(id: Int) {
        viewModelScope.launch {
            removePlanUseCase.execute(id)
        }
    }
}