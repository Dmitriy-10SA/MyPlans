package com.andef.myplans.presentation.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.andef.myplans.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AddPlanActivity : AppCompatActivity() {
    private lateinit var editTextPlanTitle: EditText

    private lateinit var radioButtonLow: RadioButton
    private lateinit var radioButtonMedium: RadioButton
    private lateinit var radioButtonHigh: RadioButton

    private lateinit var floatingActionButtonCalendar: FloatingActionButton
    private lateinit var floatingActionButtonHomeInAdd: FloatingActionButton

    private lateinit var buttonSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_plan)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
    }

    private fun initViews() {
        editTextPlanTitle = findViewById(R.id.editTextPlanTitleInAdd)

        radioButtonLow = findViewById(R.id.radioButtonLowInAdd)
        radioButtonMedium = findViewById(R.id.radioButtonMediumInAdd)
        radioButtonHigh = findViewById(R.id.radioButtonHighInAdd)

        floatingActionButtonCalendar = findViewById(R.id.floatingActionButtonCalendarInAdd)
        floatingActionButtonHomeInAdd = findViewById<FloatingActionButton?>(R.id.floatingActionButtonHomeInAdd).apply {
            setOnClickListener {
               finish()
           }
        }

        buttonSave = findViewById(R.id.buttonSaveInAdd)
    }

    companion object {
        fun newIntent(context: Context, ): Intent {
            return Intent(context, AddPlanActivity::class.java)
        }
    }
}