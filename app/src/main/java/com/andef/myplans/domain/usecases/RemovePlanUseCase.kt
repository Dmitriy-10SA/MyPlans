package com.andef.myplans.domain.usecases

import com.andef.myplans.domain.repository.PlanRepository
import javax.inject.Inject

class RemovePlanUseCase @Inject constructor(private val repository: PlanRepository) {
    suspend fun execute(id: Int) {
        return repository.remove(id)
    }
}