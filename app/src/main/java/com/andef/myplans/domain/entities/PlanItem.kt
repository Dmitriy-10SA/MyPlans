package com.andef.myplans.domain.entities

sealed class PlanItem {
    class PlanInItem(val plan: Plan) : PlanItem()
    class DateInItem(val date: String) : PlanItem()
}