package com.andef.myplans.domain.usecases

import com.andef.myplans.domain.entities.Importance
import com.andef.myplans.domain.repository.PlanRepository
import io.reactivex.Completable
import javax.inject.Inject

class ChangePlanByIdUseCase @Inject constructor(private val repository: PlanRepository) {
    fun execute(
        id: Int,
        title: String,
        date: String,
        importance: Importance
    ): Completable {
        return repository.changePlanById(
            id,
            title,
            date,
            importance
        )
    }
}