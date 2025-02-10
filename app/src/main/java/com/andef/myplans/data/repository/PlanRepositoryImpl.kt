package com.andef.myplans.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.andef.myplans.data.datasource.PlanDataBase
import com.andef.myplans.data.mappers.PlanMapper
import com.andef.myplans.domain.entities.Importance
import com.andef.myplans.domain.entities.Plan
import com.andef.myplans.domain.repository.PlanRepository
import io.reactivex.Completable

class PlanRepositoryImpl(application: Application) : PlanRepository {
    companion object {
        private var instance: PlanRepositoryImpl? = null

        fun getInstance(application: Application): PlanRepositoryImpl {
            if (instance == null) {
                instance = PlanRepositoryImpl(application)
            }
            return instance!!
        }
    }

    private val planDataBase = PlanDataBase.getInstance(application)
    private val planMapper = PlanMapper()

    override fun add(plan: Plan): Completable {
        return planDataBase.planDao.add(planMapper.mapPlanToDbModel(plan))
    }

    override fun remove(id: Int): Completable {
        return planDataBase.planDao.remove(id)
    }

    override fun getPlans(): LiveData<List<Plan>> {
        return planMapper.mapLiveDateDbModelToLiveDatePlan(planDataBase.planDao.getPlans())
    }

    override fun getPlansByDate(date: String): LiveData<List<Plan>> {
        return planMapper.mapLiveDateDbModelToLiveDatePlan(planDataBase.planDao.getPlansByDate(date))
    }

    override fun changePlanById(
        id: Int,
        title: String,
        date: String,
        importance: Importance
    ): Completable {
        return planDataBase.planDao.changePlanById(id, title, date, importance)
    }
}