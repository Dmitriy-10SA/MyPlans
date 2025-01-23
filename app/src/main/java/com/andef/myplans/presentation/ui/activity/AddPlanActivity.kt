package com.andef.myplans.presentation.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.animation.Animation
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.andef.myplans.R
import com.andef.myplans.domain.entities.Importance
import com.andef.myplans.domain.entities.Plan
import com.andef.myplans.presentation.ui.viewmodel.AddPlanViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AddPlanActivity : AppCompatActivity() {
    private lateinit var editTextPlanTitle: EditText

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
        editTextPlanTitle = findViewById(R.id.editTextPlanTitleInAdd)

        radioButtonLow = findViewById(R.id.radioButtonLowInAdd)
        radioButtonMedium = findViewById(R.id.radioButtonMediumInAdd)
        radioButtonHigh = findViewById(R.id.radioButtonHighInAdd)

        floatingActionButtonCalendar = findViewById<FloatingActionButton?>(R.id.floatingActionButtonCalendarInAdd).apply {
            setOnClickListener {
                openCalendar()
            }
        }
        floatingActionButtonHomeInAdd = findViewById<FloatingActionButton?>(R.id.floatingActionButtonHomeInAdd).apply {
            setOnClickListener {
               mainScreen()
           }
        }

        buttonSave = findViewById<Button?>(R.id.buttonSaveInAdd).apply {
            setOnClickListener {
                savePlan()
            }
        }
    }

    private fun mainScreen() {
        val animation = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.touch_save_button)
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
        val animation = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.touch_save_button)
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
        val animation = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.touch_button)
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
        viewModel.openDatePicker(this)
    }

    companion object {
        fun newIntent(context: Context, ): Intent {
            return Intent(context, AddPlanActivity::class.java)
        }
    }
}