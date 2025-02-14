package com.andef.myplans.presentation.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.animation.Animation
import android.widget.Button
import android.widget.Switch
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.andef.myplans.R
import com.andef.myplans.databinding.ActivityMainBinding
import com.andef.myplans.domain.entities.Importance
import com.andef.myplans.domain.entities.Plan
import com.andef.myplans.presentation.adapter.PlanAdapter
import com.andef.myplans.presentation.app.MyPlansApplication
import com.andef.myplans.presentation.factory.ViewModelFactory
import com.andef.myplans.presentation.ui.viewmodel.MainViewModel
import com.applandeo.materialcalendarview.CalendarDay
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnCalendarDayClickListener
import java.util.Calendar
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val component by lazy {
        (application as MyPlansApplication).component
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]
    }

    @Inject
    lateinit var plansInPlanAdapter: PlanAdapter

    @Inject
    lateinit var plansInCalenderAdapter: PlanAdapter

    private lateinit var itemTouchHelperForPlanInPlans: ItemTouchHelper
    private lateinit var itemTouchHelperForPlanInCalendar: ItemTouchHelper

    private lateinit var settingsTheme: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        component.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

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
        binding.calendarView.setOnCalendarDayClickListener(object : OnCalendarDayClickListener {
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
            attachToRecyclerView(binding.recyclerViewPlansInCalendar)
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
            attachToRecyclerView(binding.recyclerViewPlansInPlans)
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
        binding.calendarView.setEvents(calendarDays)
    }

    @SuppressLint("SetTextI18n")
    private fun initViewModel() {
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
                binding.textViewDate.text = "$day/$month/$year"
            } else {
                binding.textViewDate.text = getString(R.string.wo_plans)
            }
            plansInCalenderAdapter.plans = it
        }
    }

    private fun initViews() {
//        plansInPlanAdapter = PlanAdapter()
//        plansInCalenderAdapter = PlanAdapter()
        binding.recyclerViewPlansInPlans.adapter = plansInPlanAdapter
        binding.recyclerViewPlansInCalendar.adapter = plansInCalenderAdapter

        binding.cardViewPlans.setOnClickListener {
            touchCardViewPlansAnim()
            actionForPlans()
        }
        binding.cardViewCalendar.setOnClickListener {
            touchCardViewCalendarAnim()
            actionForCalendar()
        }
        binding.cardViewSettings.setOnClickListener {
            touchCardViewChangeThemeAnim()
            settingsAction()
        }
        binding.floatingActionButtonAddPlan.setOnClickListener {
            addNewPlan()
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
            val animation =
                android.view.animation.AnimationUtils.loadAnimation(this, R.anim.touch_save_button)
            animation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}
                override fun onAnimationRepeat(animation: Animation?) {}

                override fun onAnimationEnd(animation: Animation?) {
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
            })
            it.startAnimation(animation)
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun darkTheme() {
        binding.main.background = getDrawable(R.drawable.app_background_black)

        binding.calendarView.setHeaderColor(R.color.black)
        binding.calendarView.setHeaderLabelColor(R.color.white)
        binding.calendarView.alpha = 0.7f

        binding.textViewDate.setTextColor(getColor(R.color.white_text_black))
        binding.textViewDate.background = getDrawable(R.drawable.frame_black)

        binding.cardViewPlans.setCardBackgroundColor(getColor(R.color.card_back_color_black))
        binding.cardViewCalendar.setCardBackgroundColor(getColor(R.color.card_back_color_black))
        binding.cardViewSettings.setCardBackgroundColor(getColor(R.color.card_back_color_black))

        binding.textViewPlansInPlans.setTextColor(getColor(R.color.white_text_black))
        binding.imageViewPlansInPlans.setImageDrawable(getDrawable(R.drawable.plan_dark))

        binding.textViewCalendarInCalendar.setTextColor(getColor(R.color.white_text_black))
        binding.imageViewCalendarInCalendar.setImageDrawable(getDrawable(R.drawable.calendar_black))

        binding.textViewSettings.setTextColor(getColor(R.color.white_text_black))
        binding.imageViewSettings.setImageDrawable(getDrawable(R.drawable.dots_black))

        plansInPlanAdapter.isDarkTheme = true
        plansInCalenderAdapter.isDarkTheme = true
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun lightTheme() {
        binding.main.background = getDrawable(R.drawable.app_background_white)

        binding.calendarView.setHeaderColor(R.color.header_color)
        binding.calendarView.setHeaderLabelColor(R.color.white)
        binding.calendarView.alpha = 1f

        binding.textViewDate.setTextColor(Color.BLACK)
        binding.textViewDate.background = getDrawable(R.drawable.frame)

        binding.cardViewPlans.setCardBackgroundColor(getColor(R.color.card_back_color))
        binding.cardViewCalendar.setCardBackgroundColor(getColor(R.color.card_back_color))
        binding.cardViewSettings.setCardBackgroundColor(getColor(R.color.card_back_color))

        binding.textViewPlansInPlans.setTextColor(Color.BLACK)
        binding.imageViewPlansInPlans.setImageDrawable(getDrawable(R.drawable.plan))

        binding.textViewCalendarInCalendar.setTextColor(Color.BLACK)
        binding.imageViewCalendarInCalendar.setImageDrawable(getDrawable(R.drawable.calendar))

        binding.textViewSettings.setTextColor(Color.BLACK)
        binding.imageViewSettings.setImageDrawable(getDrawable(R.drawable.dots))

        plansInPlanAdapter.isDarkTheme = false
        plansInCalenderAdapter.isDarkTheme = false
    }

    private fun touchCardViewChangeThemeAnim() {
        val animation =
            android.view.animation.AnimationUtils.loadAnimation(this, R.anim.touch_cardview)
        binding.cardViewSettings.startAnimation(animation)
    }

    private fun touchCardViewCalendarAnim() {
        val animation =
            android.view.animation.AnimationUtils.loadAnimation(this, R.anim.touch_cardview)
        binding.cardViewCalendar.startAnimation(animation)
    }

    private fun touchCardViewPlansAnim() {
        val animation =
            android.view.animation.AnimationUtils.loadAnimation(this, R.anim.touch_cardview)
        binding.cardViewPlans.startAnimation(animation)
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
        binding.floatingActionButtonAddPlan.startAnimation(animation)
    }

    private fun actionForCalendar() {
        binding.recyclerViewPlansInPlans.visibility = GONE
        binding.calendarView.visibility = VISIBLE
        binding.textViewDate.visibility = GONE
        binding.recyclerViewPlansInCalendar.visibility = VISIBLE
    }

    @SuppressLint("SetTextI18n")
    private fun loadPlansByDate(calendar: Calendar) {
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)
        viewModel.loadPlansByDate(this, "$day/$month/$year")
    }

    private fun actionForPlans() {
        binding.recyclerViewPlansInCalendar.visibility = GONE
        binding.calendarView.visibility = GONE
        binding.textViewDate.visibility = GONE
        binding.recyclerViewPlansInPlans.visibility = VISIBLE
        viewModel.loadPlansInPlans(this)
    }

    companion object {
        private const val PREF_FILE_THEME = "THEME"
        private const val PREF_IS_DARK_THEME = "IS_DARK_THEME"
    }
}