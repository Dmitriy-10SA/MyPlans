package com.andef.myplans.presentation.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.andef.myplans.R
import com.andef.myplans.databinding.ActivityChangePlanBinding
import com.andef.myplans.di.DaggerMyPlansComponent
import com.andef.myplans.domain.entities.Importance
import com.andef.myplans.domain.entities.Plan
import com.andef.myplans.presentation.app.MyPlansApplication
import com.andef.myplans.presentation.factory.ViewModelFactory
import com.andef.myplans.presentation.ui.viewmodel.ChangePlanViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import javax.inject.Inject

class ChangePlanActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityChangePlanBinding.inflate(layoutInflater)
    }

    private val component by lazy {
        (application as MyPlansApplication).component
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[ChangePlanViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        component.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initViews()
    }

    private fun initViews() {
        binding.editTextPlanTitleInChange.setText(intent.getStringExtra(EXTRA_TITLE))

        binding.textViewLastDate.apply {
            val stringBuilder = StringBuilder()
            stringBuilder.append(text.toString())
            stringBuilder.append(intent.getStringExtra(EXTRA_DATE))
            text = stringBuilder.toString()
            viewModel.lastDate(intent.getStringExtra(EXTRA_DATE).toString())
        }

        when (intent.getIntExtra(EXTRA_IMPORTANCE, 1)) {
            3 -> binding.radioButtonLowInChange.isChecked = true
            2 -> binding.radioButtonMediumInChange.isChecked = true
            1 -> binding.radioButtonHighInChange.isChecked = true
        }

        binding.floatingActionButtonCalendarInChange.setOnClickListener { openCalendar() }
        binding.floatingActionButtonHomeInChange.setOnClickListener { mainScreen() }
        binding.buttonSaveInChange.setOnClickListener { changePlan() }

        if (intent.getBooleanExtra(EXTRA_IS_DARK_THEME, false)) {
            darkTheme()
        }
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
        binding.floatingActionButtonHomeInChange.startAnimation(animation)
    }

    private fun getImportance(): Importance? {
        return if (binding.radioButtonLowInChange.isChecked) {
            Importance.LOW
        } else if (binding.radioButtonMediumInChange.isChecked) {
            Importance.MEDIUM
        } else if (binding.radioButtonHighInChange.isChecked) {
            Importance.HIGH
        } else {
            null
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun darkTheme() {
        binding.main.background = getDrawable(R.drawable.app_background_black)

        binding.editTextPlanTitleInChange.background = getDrawable(R.drawable.frame_black)
        binding.editTextPlanTitleInChange.setTextColor(getColor(R.color.white))
        binding.editTextPlanTitleInChange.setHintTextColor(getColor(R.color.white))
        binding.editTextPlanTitleInChange.alpha = 0.8f

        binding.floatingActionButtonCalendarInChange.alpha = 0.7f
        binding.floatingActionButtonHomeInChange.alpha = 0.7f

        binding.textViewLastDate.setTextColor(getColor(R.color.white_text_black))

        binding.radioButtonLowInChange.background = getDrawable(R.drawable.green_black_background_for_importance)
        binding.radioButtonMediumInChange.background =
            getDrawable(R.drawable.orange_black_background_for_importance)
        binding.radioButtonHighInChange.background = getDrawable(R.drawable.red_black_background_for_importance)
        binding.radioButtonLowInChange.alpha = 0.7f
        binding.radioButtonMediumInChange.alpha = 0.7f
        binding.radioButtonHighInChange.alpha = 0.7f

        binding.buttonSaveInChange.alpha = 0.7f
    }

    private fun changePlan() {
        val animation =
            android.view.animation.AnimationUtils.loadAnimation(this, R.anim.touch_save_button)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationRepeat(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                val title = binding.editTextPlanTitleInChange.text.toString().trim()
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
        binding.buttonSaveInChange.startAnimation(animation)
    }

    private fun getToast(stringOfToast: String) {
        Toast.makeText(
            this,
            stringOfToast,
            Toast.LENGTH_SHORT
        ).show()
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
        binding.floatingActionButtonCalendarInChange.startAnimation(animation)
    }

    private fun openCalendarPicker() {
        if (intent.getBooleanExtra(EXTRA_IS_DARK_THEME, false)) {
            viewModel.openDatePickerBlack(this)
        } else {
            viewModel.openDatePickerWhite(this)
        }
    }

    private fun getDate(): String {
        return viewModel.date
    }

    companion object {
        private const val EXTRA_IS_DARK_THEME = "IS_DARK_THEME"
        private const val EXTRA_ID = "id"
        private const val EXTRA_TITLE = "title"
        private const val EXTRA_DATE = "date"
        private const val EXTRA_IMPORTANCE = "importance"

        fun newIntent(
            context: Context,
            id: Int,
            title: String,
            date: String,
            importance: Int,
            isDarkTheme: Boolean
        ): Intent {
            val intent = Intent(context, ChangePlanActivity::class.java)
            intent.putExtra(EXTRA_ID, id)
            intent.putExtra(EXTRA_TITLE, title)
            intent.putExtra(EXTRA_DATE, date)
            intent.putExtra(EXTRA_IMPORTANCE, importance)
            intent.putExtra(EXTRA_IS_DARK_THEME, isDarkTheme)
            return intent
        }
    }
}