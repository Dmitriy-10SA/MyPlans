package com.andef.myplans.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.andef.myplans.data.dao.PlanDao
import com.andef.myplans.data.mappers.PlanMapper
import com.andef.myplans.di.ApplicationScope
import com.andef.myplans.domain.entities.Importance
import com.andef.myplans.domain.entities.Plan
import com.andef.myplans.domain.repository.PlanRepository
import io.reactivex.Completable
import javax.inject.Inject

@ApplicationScope
class PlanRepositoryImpl @Inject constructor(
    private val planMapper: PlanMapper,
    private val planDao: PlanDao
) : PlanRepository {
    override fun add(plan: Plan): Completable {
        return planDao.add(planMapper.mapPlanToDbModel(plan))
    }

    override fun remove(id: Int): Completable {
        return planDao.remove(id)
    }

    override fun getPlans(): LiveData<List<Plan>> {
        return MediatorLiveData<List<Plan>>().apply {
            addSource(planDao.getPlans()) {
                value = planMapper.mapListDbModelToListPlan(it)
            }
        }
    }

    override fun getPlansByDate(date: String): LiveData<List<Plan>> {
        return MediatorLiveData<List<Plan>>().apply {
            addSource(planDao.getPlansByDate(date)) {
                value = planMapper.mapListDbModelToListPlan(it)
            }
        }
    }

    override fun changePlanById(
        id: Int,
        title: String,
        date: String,
        importance: Importance
    ): Completable {
        return planDao.changePlanById(id, title, date, importance)
    }
}