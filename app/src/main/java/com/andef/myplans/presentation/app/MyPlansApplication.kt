package com.andef.myplans.presentation.app

import android.app.Application
import com.andef.myplans.di.DaggerMyPlansComponent

class MyPlansApplication : Application() {
    val component by lazy {
        DaggerMyPlansComponent.factory().create(this)
    }
}