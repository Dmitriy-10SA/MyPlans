package com.andef.myplans.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.andef.myplans.domain.entities.Importance
import com.andef.myplans.domain.entities.Plan
import io.reactivex.rxjava3.core.Completable

@Dao
interface PlanDao {
    @Insert
    fun add(plan: Plan): Completable

    @Query("DELETE FROM `plans` WHERE id = :id")
    fun remove(id: Int): Completable

    @Query("SELECT * FROM plans")
    fun getPlans(): LiveData<List<Plan>>

    @Query("SELECT * FROM plans WHERE date = :date")
    fun getPlansByDate(date: String): LiveData<List<Plan>>

    @Query("UPDATE plans SET title = :title, date = :date, importance = :importance WHERE id = :id")
    fun changePlanById(id: Int, title: String, date: String, importance: Importance): Completable
}