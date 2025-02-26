package com.andef.myplans.data.repository

import com.andef.myplans.data.dao.PlanDao
import com.andef.myplans.data.mappers.PlanMapper
import com.andef.myplans.domain.entities.Importance
import com.andef.myplans.domain.entities.Plan
import com.andef.myplans.domain.repository.PlanRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PlanRepositoryImpl @Inject constructor(
    private val planMapper: PlanMapper,
    private val planDao: PlanDao
) : PlanRepository {
    override suspend fun add(plan: Plan) {
        return planDao.add(planMapper.mapPlanToDbModel(plan))
    }

    override suspend fun remove(id: Int) {
        return planDao.remove(id)
    }

    override fun getPlans(): Flow<List<Plan>> {
        return planDao.getPlans().map {
            planMapper.mapListDbModelToListPlan(it)
        }
    }

    override fun getPlansByDate(date: String): Flow<List<Plan>> {
        return planDao.getPlansByDate(date).map {
            planMapper.mapListDbModelToListPlan(it)
        }
    }

    override suspend fun changePlanById(
        id: Int,
        title: String,
        date: String,
        importance: Importance
    ) {
        return planDao.changePlanById(id, title, date, importance)
    }
}