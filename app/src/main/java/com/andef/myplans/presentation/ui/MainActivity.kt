package com.andef.myplans.presentation.ui

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.andef.myplans.R
import com.andef.myplans.domain.entities.Plan
import com.andef.myplans.presentation.adapter.PlanAdapter
import com.applandeo.materialcalendarview.CalendarDay
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.listeners.OnCalendarDayClickListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    private lateinit var plansAdapter: PlanAdapter

    private lateinit var recyclerViewPlansInPlans: RecyclerView
    private lateinit var recyclerViewPlansInCalendar: RecyclerView

    private lateinit var calendarView: CalendarView

    private lateinit var cardViewPlans: CardView
    private lateinit var cardViewCalendar: CardView

    private lateinit var floatingActionButtonAddPlan: FloatingActionButton

    private lateinit var viewModel: MainViewModel

    private lateinit var itemTouchHelperForPlan: ItemTouchHelper

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setItemTouchHelper()

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        initViews()
        observeOnViewModel()

        viewModel.loadPlansInPlans(this)
        itemTouchHelperForPlan.apply {
            attachToRecyclerView(recyclerViewPlansInPlans)
        }
    }

    private fun observeOnViewModel() {

        viewModel.plansInPlans.observe(this) {
            plansAdapter.plans = it
        }
        viewModel.plansInCalendar.observe(this) {
            plansAdapter.plans = it
        }
    }



    @RequiresApi(Build.VERSION_CODES.O)
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
                actionForCalendar()
            }
        }

        floatingActionButtonAddPlan = findViewById<FloatingActionButton?>(R.id.floatingActionButtonAddPlan).apply {
            setOnClickListener {
                addNewPlan()
            }
        }
    }

    private fun setItemTouchHelper() {
        itemTouchHelperForPlan = ItemTouchHelper(
            object : ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(
                    viewHolder: RecyclerView.ViewHolder,
                    direction: Int
                ) {
                    val position = viewHolder.adapterPosition
                    val plan = plansAdapter.plans[position]
                    viewModel.removePlan(plan.id)
                }
            })
    }

    private fun addNewPlan() {
        val intent = AddPlanActivity.newIntent(this)
        startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun actionForCalendar() {
        recyclerViewPlansInPlans.visibility = GONE
        calendarView.visibility = VISIBLE
        recyclerViewPlansInCalendar.visibility = VISIBLE
        itemTouchHelperForPlan.apply {
            attachToRecyclerView(recyclerViewPlansInCalendar)
        }
        calendarView.setOnCalendarDayClickListener(object : OnCalendarDayClickListener {
            override fun onClick(calendarDay: CalendarDay) {
                loadPlansByDate(calendarDay.calendar)
            }
        })
        val curDate = LocalDate.now()
        viewModel.loadPlansByDate(
            this,
            "${curDate.dayOfMonth}/${curDate.month.value}/${curDate.year}"
        )
    }

    private fun loadPlansByDate(calendar: Calendar) {
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)
        viewModel.loadPlansByDate(this, "$day/$month/$year")
    }

    private fun actionForPlans() {
        recyclerViewPlansInCalendar.visibility = GONE
        calendarView.visibility = GONE
        recyclerViewPlansInPlans.visibility = VISIBLE
        viewModel.loadPlansInPlans(this)
        itemTouchHelperForPlan.apply {
            attachToRecyclerView(recyclerViewPlansInPlans)
        }
        calendarView.setOnCalendarDayClickListener(object : OnCalendarDayClickListener {
            override fun onClick(calendarDay: CalendarDay) {}
        })
    }
}