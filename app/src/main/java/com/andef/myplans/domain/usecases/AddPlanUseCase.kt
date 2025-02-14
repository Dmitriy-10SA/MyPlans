package com.andef.myplans.domain.usecases

import com.andef.myplans.domain.entities.Plan
import com.andef.myplans.domain.repository.PlanRepository
import io.reactivex.Completable
import javax.inject.Inject

class AddPlanUseCase @Inject constructor(private val repository: PlanRepository) {
    fun execute(plan: Plan): Completable {
        return repository.add(plan)
    }
}