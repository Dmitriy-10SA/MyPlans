package com.andef.myplans.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.andef.myplans.data.datasource.PlanDbModel
import com.andef.myplans.domain.entities.Importance
import com.andef.myplans.domain.entities.Plan
import io.reactivex.Completable

@Dao
interface PlanDao {
    @Insert
    fun add(plan: PlanDbModel): Completable

    @Query("DELETE FROM `plans` WHERE id = :id")
    fun remove(id: Int): Completable

    @Query("SELECT * FROM plans")
    fun getPlans(): LiveData<List<PlanDbModel>>

    @Query("SELECT * FROM plans WHERE date = :date")
    fun getPlansByDate(date: String): LiveData<List<PlanDbModel>>

    @Query("UPDATE plans SET title = :title, date = :date, importance = :importance WHERE id = :id")
    fun changePlanById(id: Int, title: String, date: String, importance: Importance): Completable
}