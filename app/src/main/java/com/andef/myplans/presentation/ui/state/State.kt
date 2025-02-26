package com.andef.myplans.presentation.ui.state

import com.andef.myplans.domain.entities.Plan

sealed class State {
    data object Error : State()
    class Plans(val plans: List<Plan>) : State()
    data object Initial : State()
}