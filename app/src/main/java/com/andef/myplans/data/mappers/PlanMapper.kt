package com.andef.myplans.data.mappers

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.andef.myplans.data.datasource.PlanDbModel
import com.andef.myplans.domain.entities.Plan

class PlanMapper {
    fun mapPlanToDbModel(plan: Plan): PlanDbModel {
        return PlanDbModel(plan.id, plan.title, plan.date, plan.importance)
    }

    private fun mapDbModelToPlan(planDbModel: PlanDbModel): Plan {
        return Plan(planDbModel.id, planDbModel.title, planDbModel.date, planDbModel.importance)
    }

    private fun mapListDbModelToListPlan(planDbModelList: List<PlanDbModel>): List<Plan> {
        return planDbModelList.map { mapDbModelToPlan(it) }
    }

    fun mapLiveDateDbModelToLiveDatePlan(planDbModelList: LiveData<List<PlanDbModel>>): LiveData<List<Plan>> {
        return planDbModelList.map { mapListDbModelToListPlan(it) }
    }
}