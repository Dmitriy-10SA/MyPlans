package com.andef.myplans.presentation.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.andef.myplans.R
import com.andef.myplans.databinding.ActivityMainBinding
import com.andef.myplans.presentation.ui.fragment.CalendarFragment
import com.andef.myplans.presentation.ui.fragment.PlansFragment

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private var lastSelectedId = R.id.plans

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        showPlansFragment()
        initViews()
    }

    private fun initViews() {
        with(binding) {
            floatingActionButtonAddPlan.setOnClickListener {
                showAddPlanScreen()
            }
            bottomNavigationView.setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.plans -> {
                        if (lastSelectedId != R.id.plans) {
                            showPlansFragment()
                            lastSelectedId = R.id.plans
                            true
                        } else {
                            false
                        }
                    }

                    R.id.calendar -> {
                        if (lastSelectedId != R.id.calendar) {
                            lastSelectedId = R.id.calendar
                            showCalendarFragment()
                            true
                        } else {
                            false
                        }
                    }

                    else -> {
                        false
                    }
                }
            }
        }
    }

    private fun showAddPlanScreen() {
        val intent = PlanActivity.newIntentAdd(this)
        startActivity(intent)
    }

    private fun showPlansFragment() {
        val fragment = PlansFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.fade_in_long,
                R.anim.fade_out_long
            )
            .replace(R.id.fragmentContainerViewMain, fragment)
            .commit()
    }

    private fun showCalendarFragment() {
        val fragment = CalendarFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.fade_in_long,
                R.anim.fade_out_long
            )
            .replace(R.id.fragmentContainerViewMain, fragment)
            .commit()
    }
}