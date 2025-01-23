package com.andef.myplans.presentation.ui.activity

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Im
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.andef.myplans.R
import com.andef.myplans.domain.entities.Importance
import com.andef.myplans.domain.entities.Plan
import com.andef.myplans.presentation.adapter.PlanAdapter
import com.andef.myplans.presentation.ui.viewmodel.MainViewModel
import com.applandeo.materialcalendarview.CalendarDay
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnCalendarDayClickListener
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Calendar
import java.util.Random

class MainActivity : AppCompatActivity() {
    private lateinit var plansInPlanAdapter: PlanAdapter
    private lateinit var plansInCalenderAdapter: PlanAdapter

    private lateinit var recyclerViewPlansInPlans: RecyclerView
    private lateinit var recyclerViewPlansInCalendar: RecyclerView

    private lateinit var textViewDate: TextView

    private lateinit var calendarView: CalendarView

    private lateinit var cardViewPlans: CardView
    private lateinit var cardViewCalendar: CardView

    private lateinit var floatingActionButtonAddPlan: FloatingActionButton

    private lateinit var viewModel: MainViewModel

    private lateinit var itemTouchHelperForPlanInPlans: ItemTouchHelper
    private lateinit var itemTouchHelperForPlanInCalendar: ItemTouchHelper

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        initViewModel()

        setItemTouchHelperPlanInPlans()
        setItemTouchHelperPlanInCalendar()
        initPlanTouch()

        initBeforeStart()
    }

    private fun initPlanTouch() {
        plansInPlanAdapter.setOnPlanClickListener(object : PlanAdapter.OnPlanClickListener {
            override fun onClick(plan: Plan) {
                changeScreen(plan)
            }
        })
        plansInCalenderAdapter.setOnPlanClickListener(object : PlanAdapter.OnPlanClickListener {
            override fun onClick(plan: Plan) {
                changeScreen(plan)
            }
        })
    }

    private fun getImportanceInt(importance: Importance): Int {
        return when (importance) {
            Importance.LOW -> 3
            Importance.MEDIUM -> 2
            else -> 1
        }
    }

    private fun changeScreen(plan: Plan) {
        val intent = ChangePlanActivity.newIntent(
            this,
            plan.id,
            plan.title,
            plan.date,
            getImportanceInt(plan.importance)
        )
        startActivity(intent)
    }

    private fun initBeforeStart() {
        calendarView.setOnCalendarDayClickListener(object : OnCalendarDayClickListener {
            override fun onClick(calendarDay: CalendarDay) {
                loadPlansByDate(calendarDay.calendar)
            }
        })

        viewModel.loadPlansInPlans(this)
    }

    private fun setItemTouchHelperPlanInCalendar() {
        itemTouchHelperForPlanInCalendar = ItemTouchHelper(
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

                @RequiresApi(Build.VERSION_CODES.O)
                override fun onSwiped(
                    viewHolder: RecyclerView.ViewHolder,
                    direction: Int
                ) {
                    val position = viewHolder.adapterPosition
                    val plan = plansInCalenderAdapter.plans[position]
                    viewModel.removePlan(plan.id)
                }
            })
        itemTouchHelperForPlanInCalendar.apply {
            attachToRecyclerView(recyclerViewPlansInCalendar)
        }
    }

    private fun setItemTouchHelperPlanInPlans() {
        itemTouchHelperForPlanInPlans = ItemTouchHelper(
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
                    val plan = plansInPlanAdapter.plans[position]
                    viewModel.removePlan(plan.id)
                }
            })
        itemTouchHelperForPlanInPlans.apply {
            attachToRecyclerView(recyclerViewPlansInPlans)
        }
    }

    private fun showEventDays(plans: List<Plan>) {
        val calendarDays = ArrayList<EventDay>()
        plans.forEach {
            val calendar = Calendar.getInstance()
            val date = it.date.split("/")
            calendar.set(date[2].toInt(), date[1].toInt() - 1, date[0].toInt())
            calendarDays.add(EventDay(calendar, R.drawable.calendarplans))
        }
        calendarView.setEvents(calendarDays)
    }

    @SuppressLint("SetTextI18n")
    private fun initViewModel() {
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        viewModel.plansInPlans.observe(this) {
            plansInPlanAdapter.plans = it
            showEventDays(it)
        }
        viewModel.plansInCalendar.observe(this) {
            if (it.isNotEmpty()) {
                val date = it[0].date.split("/")
                val day = date[0].toInt()
                val month = date[1].toInt()
                val year = date[2].toInt()
                textViewDate.text = "$day/$month/$year"
            } else {
                textViewDate.text = getString(R.string.wo_plans)
            }
            plansInCalenderAdapter.plans = it
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initViews() {
        plansInPlanAdapter = PlanAdapter()
        plansInCalenderAdapter = PlanAdapter()

        recyclerViewPlansInPlans = findViewById<RecyclerView?>(R.id.recyclerViewPlansInPlans).apply {
            adapter = plansInPlanAdapter
        }
        recyclerViewPlansInCalendar = findViewById<RecyclerView?>(R.id.recyclerViewPlansInCalendar).apply {
            adapter = plansInCalenderAdapter
        }

        calendarView = findViewById(R.id.calendarView)

        textViewDate = findViewById(R.id.textViewDate)

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

    private fun addNewPlan() {
        val intent = AddPlanActivity.newIntent(this)
        startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun actionForCalendar() {
        recyclerViewPlansInPlans.visibility = GONE
        calendarView.visibility = VISIBLE
        textViewDate.visibility = VISIBLE
        recyclerViewPlansInCalendar.visibility = VISIBLE
    }

    @SuppressLint("SetTextI18n")
    private fun loadPlansByDate(calendar: Calendar) {
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)
        viewModel.loadPlansByDate(this, "$day/$month/$year")
    }

    private fun actionForPlans() {
        recyclerViewPlansInCalendar.visibility = GONE
        calendarView.visibility = GONE
        textViewDate.visibility = GONE
        recyclerViewPlansInPlans.visibility = VISIBLE
        viewModel.loadPlansInPlans(this)
    }
}