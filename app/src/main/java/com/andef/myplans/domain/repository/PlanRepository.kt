package com.andef.myplans.domain.repository

import com.andef.myplans.domain.entities.Importance
import com.andef.myplans.domain.entities.Plan
import kotlinx.coroutines.flow.Flow

interface PlanRepository {
    suspend fun add(plan: Plan)
    suspend fun remove(id: Int)
    fun getPlans(): Flow<List<Plan>>
    fun getPlansByDate(date: String): Flow<List<Plan>>
    suspend fun changePlanById(id: Int, title: String, date: String, importance: Importance)
}