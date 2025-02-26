package com.andef.myplans.presentation.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.andef.myplans.R
import com.andef.myplans.databinding.ActivityPlanBinding
import com.andef.myplans.domain.entities.Importance
import com.andef.myplans.domain.entities.Plan
import com.andef.myplans.presentation.app.MyPlansApplication
import com.andef.myplans.presentation.factory.ViewModelFactory
import com.andef.myplans.presentation.ui.state.State
import com.andef.myplans.presentation.ui.viewmodel.PlanViewModel
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.builders.DatePickerBuilder
import com.applandeo.materialcalendarview.listeners.OnSelectDateListener
import java.time.LocalDate
import java.util.Calendar
import javax.inject.Inject

class PlanActivity : AppCompatActivity(), OnSelectDateListener {
    private val binding by lazy {
        ActivityPlanBinding.inflate(layoutInflater)
    }

    private val component by lazy {
        (application as MyPlansApplication).component
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[PlanViewModel::class.java]
    }

    private var planFromIntent: Plan? = null
    private var editTextTitleEmpty = true
    private val localDate = LocalDate.now()
    private var currentDate = "${localDate.dayOfMonth}/${localDate.month.value}/${localDate.year}"

    override fun onCreate(savedInstanceState: Bundle?) {
        component.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initViews()
        observeViewModel()
        if (intent?.getStringExtra(EXTRA_SCREEN_MODE) == EXTRA_SCREEN_MODE_CHANGE) {
            completionViews()
        }
    }

    private fun completionViews() {
        planFromIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.extras!!.getParcelable(EXTRA_PLAN, Plan::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.extras!!.getParcelable(EXTRA_PLAN)
        }
        currentDate = planFromIntent?.date ?: "${localDate.dayOfMonth}/${localDate.month.value}/${localDate.year}"
        with(binding) {
            editTextTitle.setText(planFromIntent?.title)
            when (planFromIntent?.importance) {
                Importance.LOW -> {
                    radioButtonLow.isChecked = true
                    showButtonAddIfNotEmptyAll()
                }
                Importance.MEDIUM -> {
                    radioButtonMedium.isChecked = true
                    showButtonAddIfNotEmptyAll()
                }
                Importance.HIGH -> {
                    radioButtonHigh.isChecked = true
                    showButtonAddIfNotEmptyAll()
                }
                null -> {}
            }
        }
    }

    private fun observeViewModel() {
        viewModel.state.observe(this) {
            when (it) {
                State.Error -> showErrorToast()
                State.Initial -> {}
                is State.Plans -> {}
            }
        }
    }

    private fun showErrorToast() {
        Toast.makeText(
            this,
            R.string.error,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun showErrorInputToast() {
        Toast.makeText(
            this,
            R.string.input,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun initViews() {
        with(binding) {
            editTextTitle.addTextChangedListener { text ->
                editTextTitleEmpty = text.toString().isEmpty()
                showButtonAddIfNotEmptyAll()
            }
            radioButtonLow.setOnClickListener {
                showButtonAddIfNotEmptyAll()
            }
            radioButtonMedium.setOnClickListener {
                showButtonAddIfNotEmptyAll()
            }
            radioButtonHigh.setOnClickListener {
                showButtonAddIfNotEmptyAll()
            }
            buttonSave.setOnClickListener {
                addOrChangePlan()
            }
            cardViewDate.setOnClickListener {
                openDatePicker()
            }
        }
    }

    override fun onSelect(calendar: List<Calendar>) {
        val selectedDate = calendar[0]
        currentDate = "${selectedDate[Calendar.DAY_OF_MONTH]}" +
                "/${selectedDate[Calendar.MONTH] + 1}" +
                "/${selectedDate[Calendar.YEAR]}"
    }

    private fun openDatePicker() {
        DatePickerBuilder(this, this)
            .pickerType(CalendarView.ONE_DAY_PICKER)
            .headerColor(R.color.my_blue)
            .headerLabelColor(R.color.white)
            .abbreviationsLabelsColor(R.color.black)
            .pagesColor(R.color.white)
            .selectionColor(R.color.my_blue)
            .selectionLabelColor(R.color.white)
            .daysLabelsColor(R.color.black)
            .dialogButtonsColor(R.color.black)
            .todayLabelColor(R.color.my_blue)
            .build()
            .show()
    }

    private fun addOrChangePlan() {
        if (intent?.getStringExtra(EXTRA_SCREEN_MODE) == EXTRA_SCREEN_MODE_ADD) {
            if (getImportance() != null && !editTextTitleEmpty) {
                val plan = Plan(
                    0,
                    binding.editTextTitle.text.toString().trim(),
                    currentDate,
                    getImportance()!!
                )
                viewModel.addPlan(plan)
                finish()
            } else {
                showErrorInputToast()
            }
        } else {
            if (getImportance() != null && !editTextTitleEmpty) {
                val title = binding.editTextTitle.text.toString().trim()
                val importance = getImportance()!!
                if (planFromIntent != null) {
                    viewModel.changePlan(planFromIntent!!.id, title, currentDate, importance)
                    finish()
                } else {
                    showErrorToast()
                }
            } else {
                showErrorInputToast()
            }
        }
    }

    private fun getImportance(): Importance? {
        return if (binding.radioButtonLow.isChecked) {
            Importance.LOW
        } else if (binding.radioButtonMedium.isChecked) {
            Importance.MEDIUM
        } else if (binding.radioButtonHigh.isChecked) {
            Importance.HIGH
        } else {
            null
        }
    }

    private fun showButtonAddIfNotEmptyAll() {
        with(binding) {
            if (!editTextTitleEmpty && getImportance() != null) {
                buttonSave.alpha = 1f
                buttonSave.isEnabled = true
            } else {
                buttonSave.alpha = 0.5f
                buttonSave.isEnabled = false
            }
        }
    }

    companion object {
        private const val EXTRA_PLAN = "plan"
        private const val EXTRA_SCREEN_MODE = "screenMode"
        private const val EXTRA_SCREEN_MODE_ADD = "screenModeAdd"
        private const val EXTRA_SCREEN_MODE_CHANGE = "screenModeChange"

        @JvmStatic
        fun newIntentAdd(context: Context): Intent {
            return Intent(context, PlanActivity::class.java).apply {
                putExtra(EXTRA_SCREEN_MODE, EXTRA_SCREEN_MODE_ADD)
            }
        }

        @JvmStatic
        fun newIntentChange(context: Context, plan: Plan): Intent {
            return Intent(context, PlanActivity::class.java).apply {
                putExtra(EXTRA_SCREEN_MODE, EXTRA_SCREEN_MODE_CHANGE)
                putExtra(EXTRA_PLAN, plan)
            }
        }
    }
}