package com.andef.myplans.domain.entities

import com.applandeo.materialcalendarview.CalendarDay

data class Plan(
    val id: Int,
    var title: String,
    var date: String,
    var importance: Importance
)
