package com.andef.myplans.presentation.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.andef.myplans.R
import com.andef.myplans.databinding.ActivityAddPlanBinding
import com.andef.myplans.domain.entities.Importance
import com.andef.myplans.domain.entities.Plan
import com.andef.myplans.presentation.app.MyPlansApplication
import com.andef.myplans.presentation.factory.ViewModelFactory
import com.andef.myplans.presentation.ui.viewmodel.AddPlanViewModel
import javax.inject.Inject

class AddPlanActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityAddPlanBinding.inflate(layoutInflater)
    }
    private val component by lazy {
        (application as MyPlansApplication).component
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[AddPlanViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        component.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initViews()
    }

    private fun initViews() {
        binding.floatingActionButtonCalendarInAdd.setOnClickListener { openCalendar() }
        binding.floatingActionButtonHomeInAdd.setOnClickListener { mainScreen() }

        binding.buttonSaveInAdd.setOnClickListener { savePlan() }

        if (intent.getBooleanExtra(EXTRA_IS_DARK_THEME, false)) {
            darkTheme()
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun darkTheme() {
        binding.main.background = getDrawable(R.drawable.app_background_black)

        binding.editTextPlanTitleInAdd.background = getDrawable(R.drawable.frame_black)
        binding.editTextPlanTitleInAdd.setTextColor(getColor(R.color.white))
        binding.editTextPlanTitleInAdd.setHintTextColor(getColor(R.color.white))
        binding.editTextPlanTitleInAdd.alpha = 0.8f

        binding.floatingActionButtonCalendarInAdd.alpha = 0.7f
        binding.floatingActionButtonHomeInAdd.alpha = 0.7f

        binding.textViewHintDeleteChange.setTextColor(getColor(R.color.white_text_black))

        binding.radioButtonLowInAdd.background =
            getDrawable(R.drawable.green_black_background_for_importance)
        binding.radioButtonMediumInAdd.background =
            getDrawable(R.drawable.orange_black_background_for_importance)
        binding.radioButtonHighInAdd.background =
            getDrawable(R.drawable.red_black_background_for_importance)
        binding.radioButtonLowInAdd.alpha = 0.7f
        binding.radioButtonMediumInAdd.alpha = 0.7f
        binding.radioButtonHighInAdd.alpha = 0.7f

        binding.buttonSaveInAdd.alpha = 0.7f
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
        binding.floatingActionButtonHomeInAdd.startAnimation(animation)
    }

    private fun getImportance(): Importance? {
        return if (binding.radioButtonLowInAdd.isChecked) {
            Importance.LOW
        } else if (binding.radioButtonMediumInAdd.isChecked) {
            Importance.MEDIUM
        } else if (binding.radioButtonHighInAdd.isChecked) {
            Importance.HIGH
        } else {
            null
        }
    }

    private fun savePlan() {
        val animation =
            android.view.animation.AnimationUtils.loadAnimation(this, R.anim.touch_save_button)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationRepeat(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                val title = binding.editTextPlanTitleInAdd.text.toString().trim()
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
        binding.buttonSaveInAdd.startAnimation(animation)
    }

    private fun getToast(stringOfToast: String) {
        Toast.makeText(
            this,
            stringOfToast,
            Toast.LENGTH_SHORT
        ).show()
    }

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
        binding.floatingActionButtonCalendarInAdd.startAnimation(animation)
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