package com.andef.myplans.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.andef.myplans.data.datasource.PlanDbModel
import com.andef.myplans.di.ApplicationScope
import com.andef.myplans.domain.entities.Importance
import kotlinx.coroutines.flow.Flow

@ApplicationScope
@Dao
interface PlanDao {
    @Insert
    suspend fun add(plan: PlanDbModel)

    @Query("DELETE FROM `plans` WHERE id = :id")
    suspend fun remove(id: Int)

    @Query("SELECT * FROM plans")
    fun getPlans(): Flow<List<PlanDbModel>>

    @Query("SELECT * FROM plans WHERE date = :date")
    fun getPlansByDate(date: String): Flow<List<PlanDbModel>>

    @Query("UPDATE plans SET title = :title, date = :date, importance = :importance WHERE id = :id")
    suspend fun changePlanById(id: Int, title: String, date: String, importance: Importance)
}