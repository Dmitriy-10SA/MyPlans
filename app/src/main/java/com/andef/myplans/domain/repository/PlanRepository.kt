package com.andef.myplans.domain.repository

import androidx.lifecycle.LiveData
import com.andef.myplans.domain.entities.Importance
import com.andef.myplans.domain.entities.Plan
import io.reactivex.rxjava3.core.Completable

interface PlanRepository {
    fun add(plan: Plan): Completable
    fun remove(id: Int): Completable
    fun getPlans(): LiveData<List<Plan>>
    fun getPlansByDate(date: String): LiveData<List<Plan>>
    fun changePlanById(id: Int, title: String, date: String, importance: Importance): Completable
}