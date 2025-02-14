package com.andef.myplans.data.mappers

import com.andef.myplans.data.datasource.PlanDbModel
import com.andef.myplans.domain.entities.Plan
import javax.inject.Inject

class PlanMapper @Inject constructor() {
    fun mapPlanToDbModel(plan: Plan): PlanDbModel {
        return PlanDbModel(plan.id, plan.title, plan.date, plan.importance)
    }

    private fun mapDbModelToPlan(planDbModel: PlanDbModel): Plan {
        return Plan(planDbModel.id, planDbModel.title, planDbModel.date, planDbModel.importance)
    }

    fun mapListDbModelToListPlan(planDbModelList: List<PlanDbModel>): List<Plan> {
        return planDbModelList.map { mapDbModelToPlan(it) }
    }
}