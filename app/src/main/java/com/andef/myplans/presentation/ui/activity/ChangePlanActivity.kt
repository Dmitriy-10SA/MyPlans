package com.andef.myplans.presentation.ui.activity

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
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.andef.myplans.R
import com.andef.myplans.domain.entities.Importance
import com.andef.myplans.domain.entities.Plan
import com.andef.myplans.presentation.ui.viewmodel.ChangePlanViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ChangePlanActivity : AppCompatActivity() {
    private lateinit var editTextPlanTitle: EditText

    private lateinit var textViewLastDate: TextView

    private lateinit var radioButtonLow: RadioButton
    private lateinit var radioButtonMedium: RadioButton
    private lateinit var radioButtonHigh: RadioButton

    private lateinit var floatingActionButtonCalendar: FloatingActionButton
    private lateinit var floatingActionButtonHome: FloatingActionButton

    private lateinit var buttonSave: Button

    private lateinit var viewModel: ChangePlanViewModel

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContentView(R.layout.activity_change_plan)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel = ViewModelProvider(this)[ChangePlanViewModel::class.java]
        initViews()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initViews() {
        editTextPlanTitle = findViewById<EditText?>(R.id.editTextPlanTitleInChange).apply {
            setText(intent.getStringExtra(EXTRA_TITLE))
        }

        textViewLastDate = findViewById<TextView?>(R.id.textViewLastDate).apply {
            val stringBuilder = StringBuilder()
            stringBuilder.append(text.toString())
            stringBuilder.append(intent.getStringExtra(EXTRA_DATE))
            text = stringBuilder.toString()
            viewModel.lastDate(intent.getStringExtra(EXTRA_DATE).toString())
        }

        radioButtonLow = findViewById(R.id.radioButtonLowInChange)
        radioButtonMedium = findViewById(R.id.radioButtonMediumInChange)
        radioButtonHigh = findViewById(R.id.radioButtonHighInChange)

        when (intent.getIntExtra(EXTRA_IMPORTANCE, 1)) {
            3 -> radioButtonLow.isChecked = true
            2 -> radioButtonMedium.isChecked = true
            1 -> radioButtonHigh.isChecked = true
        }

        floatingActionButtonCalendar = findViewById<FloatingActionButton?>(R.id.floatingActionButtonCalendarInChange).apply {
            setOnClickListener {
                openCalendar()
            }
        }
        floatingActionButtonHome = findViewById<FloatingActionButton?>(R.id.floatingActionButtonHomeInChange).apply {
            setOnClickListener {
                mainScreen()
            }
        }

        buttonSave = findViewById<Button?>(R.id.buttonSaveInChange).apply {
            setOnClickListener {
                changePlan()
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
        floatingActionButtonHome.startAnimation(animation)
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
    private fun changePlan() {
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
                val plan = Plan(intent.getIntExtra(EXTRA_ID, -1), title, getDate(), importance)
                viewModel.changePlan(plan)
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDate(): String {
        return viewModel.date
    }

    companion object {
        private const val EXTRA_ID = "id"
        private const val EXTRA_TITLE = "title"
        private const val EXTRA_DATE = "date"
        private const val EXTRA_IMPORTANCE = "importance"

        fun newIntent(
            context: Context,
            id: Int,
            title: String,
            date: String,
            importance: Int
        ): Intent {
            val intent = Intent(context, ChangePlanActivity::class.java)
            intent.putExtra(EXTRA_ID, id)
            intent.putExtra(EXTRA_TITLE, title)
            intent.putExtra(EXTRA_DATE, date)
            intent.putExtra(EXTRA_IMPORTANCE, importance)
            return intent
        }
    }
}