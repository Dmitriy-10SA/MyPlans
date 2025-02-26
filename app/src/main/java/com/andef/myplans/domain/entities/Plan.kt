package com.andef.myplans.domain.entities

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class Plan(
    val id: Int,
    var title: String,
    var date: String,
    var importance: Importance
) : Parcelable
