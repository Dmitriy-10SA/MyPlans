package com.andef.myplans.data.datasource

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.andef.myplans.domain.entities.Importance

@Entity(tableName = "plans")
data class PlanDbModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    var title: String,
    var date: String,
    var importance: Importance
)