package com.andef.myplans.domain.usecases

import com.andef.myplans.domain.entities.Plan
import com.andef.myplans.domain.repository.PlanRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPlansUseCase @Inject constructor(
    private val repository: PlanRepository
) {
    fun execute(): Flow<List<Plan>> {
        return repository.getPlans()
    }
}