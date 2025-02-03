package com.andef.myplans.presentation.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.animation.Animation
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.andef.myplans.R
import com.andef.myplans.domain.entities.Importance
import com.andef.myplans.domain.entities.Plan
import com.andef.myplans.presentation.ui.viewmodel.AddPlanViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AddPlanActivity : AppCompatActivity() {
    private lateinit var main: ConstraintLayout

    private lateinit var editTextPlanTitle: EditText

    private lateinit var textViewHintDeleteChange: TextView

    private lateinit var radioButtonLow: RadioButton
    private lateinit var radioButtonMedium: RadioButton
    private lateinit var radioButtonHigh: RadioButton

    private lateinit var floatingActionButtonCalendar: FloatingActionButton
    private lateinit var floatingActionButtonHomeInAdd: FloatingActionButton

    private lateinit var buttonSave: Button

    private lateinit var viewModel: AddPlanViewModel

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContentView(R.layout.activity_add_plan)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel = ViewModelProvider(this)[AddPlanViewModel::class.java]

        initViews()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initViews() {
        main = findViewById(R.id.main)

        editTextPlanTitle = findViewById(R.id.editTextPlanTitleInAdd)

        textViewHintDeleteChange = findViewById(R.id.textViewHintDeleteChange)

        radioButtonLow = findViewById(R.id.radioButtonLowInAdd)
        radioButtonMedium = findViewById(R.id.radioButtonMediumInAdd)
        radioButtonHigh = findViewById(R.id.radioButtonHighInAdd)

        floatingActionButtonCalendar =
            findViewById<FloatingActionButton?>(R.id.floatingActionButtonCalendarInAdd).apply {
                setOnClickListener {
                    openCalendar()
                }
            }
        floatingActionButtonHomeInAdd =
            findViewById<FloatingActionButton?>(R.id.floatingActionButtonHomeInAdd).apply {
                setOnClickListener {
                    mainScreen()
                }
            }

        buttonSave = findViewById<Button?>(R.id.buttonSaveInAdd).apply {
            setOnClickListener {
                savePlan()
            }
        }

        if (intent.getBooleanExtra(EXTRA_IS_DARK_THEME, false)) {
            darkTheme()
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun darkTheme() {
        main.background = getDrawable(R.drawable.app_background_black)

        editTextPlanTitle.background = getDrawable(R.drawable.frame_black)
        editTextPlanTitle.setTextColor(getColor(R.color.white))
        editTextPlanTitle.setHintTextColor(getColor(R.color.white))
        editTextPlanTitle.alpha = 0.8f

        floatingActionButtonCalendar.alpha = 0.7f
        floatingActionButtonHomeInAdd.alpha = 0.7f

        textViewHintDeleteChange.setTextColor(getColor(R.color.white_text_black))

        radioButtonLow.background = getDrawable(R.drawable.green_black_background_for_importance)
        radioButtonMedium.background =
            getDrawable(R.drawable.orange_black_background_for_importance)
        radioButtonHigh.background = getDrawable(R.drawable.red_black_background_for_importance)
        radioButtonLow.alpha = 0.7f
        radioButtonMedium.alpha = 0.7f
        radioButtonHigh.alpha = 0.7f

        buttonSave.alpha = 0.7f
    }

    private fun mainScreen() {
        val animation =
            android.view.animation.AnimationUtils.loadAnimation(this, R.anim.touch_save_button)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationRepeat(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                finish()
            }
        })
        floatingActionButtonHomeInAdd.startAnimation(animation)
    }

    private fun getImportance(): Importance? {
        return if (radioButtonLow.isChecked) {
            Importance.LOW
        } else if (radioButtonMedium.isChecked) {
            Importance.MEDIUM
        } else if (radioButtonHigh.isChecked) {
            Importance.HIGH
        } else {
            null
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun savePlan() {
        val animation =
            android.view.animation.AnimationUtils.loadAnimation(this, R.anim.touch_save_button)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationRepeat(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                val title = editTextPlanTitle.text.toString().trim()
                val importance = getImportance()
                if (title.isEmpty()) {
                    getToast(getString(R.string.fill_in_the_text))
                    return
                }
                if (importance == null) {
                    getToast(getString(R.string.fill_in_the_importance))
                    return
                }
                val plan = Plan(0, title, getDate(), importance)
                viewModel.savePlan(plan)
                finish()
            }
        })
        buttonSave.startAnimation(animation)
    }

    private fun getToast(stringOfToast: String) {
        Toast.makeText(
            this,
            stringOfToast,
            Toast.LENGTH_SHORT
        ).show()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDate(): String {
        return viewModel.date
    }

    private fun openCalendar() {
        val animation =
            android.view.animation.AnimationUtils.loadAnimation(this, R.anim.touch_button)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationRepeat(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                openCalendarPicker()
            }
        })
        floatingActionButtonCalendar.startAnimation(animation)
    }

    private fun openCalendarPicker() {
        if (intent.getBooleanExtra(EXTRA_IS_DARK_THEME, false)) {
            viewModel.openDatePickerBlack(this)
        } else {
            viewModel.openDatePickerWhite(this)
        }

    }

    companion object {
        private const val EXTRA_IS_DARK_THEME = "IS_DARK_THEME"

        fun newIntent(context: Context, isDarkTheme: Boolean): Intent {
            val intent = Intent(context, AddPlanActivity::class.java)
            intent.putExtra(EXTRA_IS_DARK_THEME, isDarkTheme)
            return intent
        }
    }
}