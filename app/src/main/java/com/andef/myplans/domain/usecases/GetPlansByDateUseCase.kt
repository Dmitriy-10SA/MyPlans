package com.andef.myplans.domain.usecases

import com.andef.myplans.domain.entities.Plan
import com.andef.myplans.domain.repository.PlanRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetPlansByDateUseCase @Inject constructor(private val repository: PlanRepository) {
    fun execute(date: String): Flow<List<Plan>> {
        return repository.getPlansByDate(date)
    }
}