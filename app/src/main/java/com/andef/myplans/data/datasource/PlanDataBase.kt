package com.andef.myplans.data.datasource

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.andef.myplans.data.dao.PlanDao
import com.andef.myplans.domain.entities.Plan

@Database(entities = [PlanDbModel::class], version = 1, exportSchema = false)
abstract class PlanDataBase : RoomDatabase() {
    abstract val planDao: PlanDao

    companion object {
        private var instance: PlanDataBase? = null

        fun getInstance(application: Application): PlanDataBase {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    application,
                    PlanDataBase::class.java,
                    DB_NAME
                ).build()
            }
            return instance!!
        }

        private const val DB_NAME = "Plan.db"
    }
}