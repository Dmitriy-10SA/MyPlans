package com.andef.myplans.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.andef.myplans.data.datasource.PlanDataBase
import com.andef.myplans.domain.entities.Importance
import com.andef.myplans.domain.entities.Plan
import com.andef.myplans.domain.repository.PlanRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers

class PlanRepositoryImpl(application: Application): PlanRepository {
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

    override fun add(plan: Plan): Completable {
        return planDataBase.planDao.add(plan)
    }

    override fun remove(id: Int): Completable {
        return planDataBase.planDao.remove(id)
    }

    override fun getPlans(): LiveData<List<Plan>> {
        return planDataBase.planDao.getPlans()
    }

    override fun getPlansById(id: Int): LiveData<List<Plan>> {
        return planDataBase.planDao.getPlansById(id)
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