package com.andef.myplans.domain.usecases

import androidx.lifecycle.LiveData
import com.andef.myplans.domain.entities.Plan
import com.andef.myplans.domain.repository.PlanRepository
import javax.inject.Inject

class GetPlansByDateUseCase @Inject constructor(private val repository: PlanRepository) {
    fun execute(date: String): LiveData<List<Plan>> {
        return repository.getPlansByDate(date)
    }
}