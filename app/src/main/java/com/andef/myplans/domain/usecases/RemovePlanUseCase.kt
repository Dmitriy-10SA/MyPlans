package com.andef.myplans.domain.usecases

import com.andef.myplans.domain.repository.PlanRepository
import io.reactivex.Completable
import javax.inject.Inject

class RemovePlanUseCase @Inject constructor(private val repository: PlanRepository) {
    fun execute(id: Int): Completable {
        return repository.remove(id)
    }
}