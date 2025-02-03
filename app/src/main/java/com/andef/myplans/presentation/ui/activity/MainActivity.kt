package com.andef.myplans.presentation.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.animation.Animation
import android.widget.Button
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
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

class MainActivity : AppCompatActivity() {
    private lateinit var main: ConstraintLayout

    private lateinit var plansInPlanAdapter: PlanAdapter
    private lateinit var plansInCalenderAdapter: PlanAdapter

    private lateinit var recyclerViewPlansInPlans: RecyclerView
    private lateinit var recyclerViewPlansInCalendar: RecyclerView

    private lateinit var imageViewPlansInPlans: ImageView
    private lateinit var imageViewCalendarInCalendar: ImageView
    private lateinit var imageViewSettings: ImageView

    private lateinit var textViewDate: TextView
    private lateinit var textViewPlansInPlans: TextView
    private lateinit var textViewCalendarInCalendar: TextView
    private lateinit var textViewSettings: TextView

    private lateinit var calendarView: CalendarView

    private lateinit var cardViewPlans: CardView
    private lateinit var cardViewCalendar: CardView
    private lateinit var cardViewSettings: CardView

    private lateinit var floatingActionButtonAddPlan: FloatingActionButton

    private lateinit var viewModel: MainViewModel

    private lateinit var itemTouchHelperForPlanInPlans: ItemTouchHelper
    private lateinit var itemTouchHelperForPlanInCalendar: ItemTouchHelper

    private lateinit var settingsTheme: SharedPreferences

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
            getImportanceInt(plan.importance),
            settingsTheme.getBoolean(PREF_IS_DARK_THEME, false)
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
        main = findViewById(R.id.main)

        plansInPlanAdapter = PlanAdapter()
        plansInCalenderAdapter = PlanAdapter()

        recyclerViewPlansInPlans =
            findViewById<RecyclerView?>(R.id.recyclerViewPlansInPlans).apply {
                adapter = plansInPlanAdapter
            }
        recyclerViewPlansInCalendar =
            findViewById<RecyclerView?>(R.id.recyclerViewPlansInCalendar).apply {
                adapter = plansInCalenderAdapter
            }

        imageViewPlansInPlans = findViewById(R.id.imageViewPlansInPlans)
        imageViewCalendarInCalendar = findViewById(R.id.imageViewCalendarInCalendar)
        imageViewSettings = findViewById(R.id.imageViewSettings)

        calendarView = findViewById(R.id.calendarView)

        textViewDate = findViewById(R.id.textViewDate)
        textViewPlansInPlans = findViewById(R.id.textViewPlansInPlans)
        textViewCalendarInCalendar = findViewById(R.id.textViewCalendarInCalendar)
        textViewSettings = findViewById(R.id.textViewSettings)

        cardViewPlans = findViewById<CardView?>(R.id.cardViewPlans).apply {
            setOnClickListener {
                touchCardViewPlansAnim()
                actionForPlans()
            }
        }
        cardViewCalendar = findViewById<CardView?>(R.id.cardViewCalendar).apply {
            setOnClickListener {
                touchCardViewCalendarAnim()
                actionForCalendar()
            }
        }
        cardViewSettings = findViewById<CardView?>(R.id.cardViewSettings).apply {
            setOnClickListener {
                touchCardViewChangeThemeAnim()
                settingsAction()
            }
        }

        floatingActionButtonAddPlan =
            findViewById<FloatingActionButton?>(R.id.floatingActionButtonAddPlan).apply {
                setOnClickListener {
                    addNewPlan()
                }
            }

        settingsTheme = application.getSharedPreferences(PREF_FILE_THEME, Context.MODE_PRIVATE)
        if (settingsTheme.getBoolean(PREF_IS_DARK_THEME, false)) {
            darkTheme()
        } else {
            lightTheme()
        }
    }

    @SuppressLint("UseSwitchCompatOrMaterialCode", "UseCompatLoadingForDrawables")
    private fun settingsAction() {
        val view = layoutInflater.inflate(R.layout.alert_dialog_custom, null)
        val switchChangeTheme = view.findViewById<Switch>(R.id.switchChangeTheme)
        val buttonSave = view.findViewById<Button>(R.id.buttonSave)

        if (settingsTheme.getBoolean(PREF_IS_DARK_THEME, false)) {
            switchChangeTheme.isChecked = true
            view.background = getDrawable(R.drawable.alert_dialog_background_black)
            switchChangeTheme.trackTintList = ColorStateList.valueOf(Color.BLACK)
            switchChangeTheme.thumbTintList = ColorStateList.valueOf(Color.BLACK)
            buttonSave.background.setTint(Color.BLACK)
        }

        val dialog = AlertDialog.Builder(this)
            .setView(view)
            .setCancelable(false)
            .create()

        dialog.show()

        switchChangeTheme.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                view.background = getDrawable(R.drawable.alert_dialog_background_black)
                switchChangeTheme.trackTintList = ColorStateList.valueOf(Color.BLACK)
                switchChangeTheme.thumbTintList = ColorStateList.valueOf(Color.BLACK)
                buttonSave.background.setTint(Color.BLACK)
                buttonSave.setTextColor(getColor(R.color.white_text_black))
                darkTheme()
                plansInPlanAdapter.isDarkTheme = true
                plansInCalenderAdapter.isDarkTheme = true
            } else {
                view.background = getDrawable(R.drawable.alert_dialog_background)
                switchChangeTheme.trackTintList =
                    ColorStateList.valueOf(getColor(R.color.button_add_white))
                switchChangeTheme.thumbTintList =
                    ColorStateList.valueOf(getColor(R.color.button_add_white))
                buttonSave.background.setTint(getColor(R.color.button_add_white))
                buttonSave.setTextColor(Color.WHITE)
                lightTheme()
                plansInPlanAdapter.isDarkTheme = false
                plansInCalenderAdapter.isDarkTheme = false
            }
        }

        buttonSave.setOnClickListener {
            val prefEditor = settingsTheme.edit()
            if (switchChangeTheme.isChecked) {
                darkTheme()
                prefEditor.putBoolean(PREF_IS_DARK_THEME, true)
            } else {
                lightTheme()
                prefEditor.putBoolean(PREF_IS_DARK_THEME, false)
            }
            prefEditor.apply()
            dialog.dismiss()
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun darkTheme() {
        main.background = getDrawable(R.drawable.app_background_black)

        calendarView.setHeaderColor(R.color.black)
        calendarView.setHeaderLabelColor(R.color.white)
        calendarView.alpha = 0.7f

        textViewDate.setTextColor(getColor(R.color.white_text_black))
        textViewDate.background = getDrawable(R.drawable.frame_black)

        cardViewPlans.setCardBackgroundColor(getColor(R.color.card_back_color_black))
        cardViewCalendar.setCardBackgroundColor(getColor(R.color.card_back_color_black))
        cardViewSettings.setCardBackgroundColor(getColor(R.color.card_back_color_black))

        textViewPlansInPlans.setTextColor(getColor(R.color.white_text_black))
        imageViewPlansInPlans.setImageDrawable(getDrawable(R.drawable.plan_dark))

        textViewCalendarInCalendar.setTextColor(getColor(R.color.white_text_black))
        imageViewCalendarInCalendar.setImageDrawable(getDrawable(R.drawable.calendar_black))

        textViewSettings.setTextColor(getColor(R.color.white_text_black))
        imageViewSettings.setImageDrawable(getDrawable(R.drawable.dots_black))

        plansInPlanAdapter.isDarkTheme = true
        plansInCalenderAdapter.isDarkTheme = true
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun lightTheme() {
        main.background = getDrawable(R.drawable.app_background_white)

        calendarView.setHeaderColor(R.color.header_color)
        calendarView.setHeaderLabelColor(R.color.white)
        calendarView.alpha = 1f

        textViewDate.setTextColor(Color.BLACK)
        textViewDate.background = getDrawable(R.drawable.frame)

        cardViewPlans.setCardBackgroundColor(getColor(R.color.card_back_color))
        cardViewCalendar.setCardBackgroundColor(getColor(R.color.card_back_color))
        cardViewSettings.setCardBackgroundColor(getColor(R.color.card_back_color))

        textViewPlansInPlans.setTextColor(Color.BLACK)
        imageViewPlansInPlans.setImageDrawable(getDrawable(R.drawable.plan))

        textViewCalendarInCalendar.setTextColor(Color.BLACK)
        imageViewCalendarInCalendar.setImageDrawable(getDrawable(R.drawable.calendar))

        textViewSettings.setTextColor(Color.BLACK)
        imageViewSettings.setImageDrawable(getDrawable(R.drawable.dots))

        plansInPlanAdapter.isDarkTheme = false
        plansInCalenderAdapter.isDarkTheme = false
    }

    private fun touchCardViewChangeThemeAnim() {
        val animation =
            android.view.animation.AnimationUtils.loadAnimation(this, R.anim.touch_cardview)
        cardViewSettings.startAnimation(animation)
    }

    private fun touchCardViewCalendarAnim() {
        val animation =
            android.view.animation.AnimationUtils.loadAnimation(this, R.anim.touch_cardview)
        cardViewCalendar.startAnimation(animation)
    }

    private fun touchCardViewPlansAnim() {
        val animation =
            android.view.animation.AnimationUtils.loadAnimation(this, R.anim.touch_cardview)
        cardViewPlans.startAnimation(animation)
    }

    private fun addNewPlan() {
        val intent =
            AddPlanActivity.newIntent(this, settingsTheme.getBoolean(PREF_IS_DARK_THEME, false))
        val animation =
            android.view.animation.AnimationUtils.loadAnimation(this, R.anim.touch_button)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationRepeat(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                startActivity(intent)
            }
        })
        floatingActionButtonAddPlan.startAnimation(animation)
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

    companion object {
        private const val PREF_FILE_THEME = "THEME"
        private const val PREF_IS_DARK_THEME = "IS_DARK_THEME"
    }
}