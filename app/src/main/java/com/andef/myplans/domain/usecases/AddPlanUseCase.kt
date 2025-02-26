package com.andef.myplans.domain.usecases

import com.andef.myplans.domain.entities.Plan
import com.andef.myplans.domain.repository.PlanRepository
import javax.inject.Inject

class AddPlanUseCase @Inject constructor(private val repository: PlanRepository) {
    suspend fun execute(plan: Plan) {
        return repository.add(plan)
    }
}