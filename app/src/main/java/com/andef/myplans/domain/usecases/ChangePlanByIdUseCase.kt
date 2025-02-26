package com.andef.myplans.domain.usecases

import com.andef.myplans.domain.entities.Importance
import com.andef.myplans.domain.repository.PlanRepository
import javax.inject.Inject

class ChangePlanByIdUseCase @Inject constructor(private val repository: PlanRepository) {
    suspend fun execute(
        id: Int,
        title: String,
        date: String,
        importance: Importance
    ) {
        return repository.changePlanById(
            id,
            title,
            date,
            importance
        )
    }
}