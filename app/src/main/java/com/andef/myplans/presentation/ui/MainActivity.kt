package com.andef.myplans.presentation.ui

import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.andef.myplans.R
import com.andef.myplans.presentation.adapter.PlanAdapter
import com.applandeo.materialcalendarview.CalendarView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private lateinit var plansAdapter: PlanAdapter

    private lateinit var recyclerViewPlansInPlans: RecyclerView
    private lateinit var recyclerViewPlansInCalendar: RecyclerView

    private lateinit var calendarView: CalendarView

    private lateinit var cardViewPlans: CardView
    private lateinit var cardViewCalendar: CardView

    private lateinit var floatingActionButtonAddPlan: FloatingActionButton

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        initViews()
        observeOnViewModel()
    }

    private fun observeOnViewModel() {
        viewModel.plansInPlans.observe(this) {
            plansAdapter.plans = it
        }
        viewModel.plansInCalendar.observe(this) {
            plansAdapter.plans = it
        }
    }

    private fun initViews() {
        plansAdapter = PlanAdapter()

        recyclerViewPlansInPlans = findViewById<RecyclerView?>(R.id.recyclerViewPlansInPlans).apply {
            adapter = plansAdapter
        }
        recyclerViewPlansInCalendar = findViewById<RecyclerView?>(R.id.recyclerViewPlansInCalendar).apply {
            adapter = plansAdapter
        }

        calendarView = findViewById(R.id.calendarView)

        cardViewPlans = findViewById<CardView?>(R.id.cardViewPlans).apply {
            setOnClickListener {
                actionForPlans()
            }
        }
        cardViewCalendar = findViewById<CardView?>(R.id.cardViewCalendar).apply {
            setOnClickListener {
                recyclerViewPlansInPlans.visibility = GONE
                calendarView.visibility = VISIBLE
                recyclerViewPlansInCalendar.visibility = VISIBLE
            }
        }

        floatingActionButtonAddPlan = findViewById(R.id.floatingActionButtonAddPlan)
    }

    private fun actionForPlans() {
        recyclerViewPlansInCalendar.visibility = GONE
        calendarView.visibility = GONE
        recyclerViewPlansInPlans.visibility = VISIBLE
        viewModel.loadPlansInPlans(this)
    }
}