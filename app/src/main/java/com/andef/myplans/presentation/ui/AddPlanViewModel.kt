package com.andef.myplans.presentation.ui

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.andef.myplans.R
import com.andef.myplans.domain.entities.Plan
import com.andef.myplans.domain.usecases.AddPlan
import com.applandeo.materialcalendarview.CalendarDay
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.builders.DatePickerBuilder
import com.applandeo.materialcalendarview.listeners.OnSelectDateListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.time.LocalDate
import java.util.Calendar

class AddPlanViewModel(application: Application) : AndroidViewModel(application), OnSelectDateListener {
    private val compositeDisposable = CompositeDisposable()

    @RequiresApi(Build.VERSION_CODES.O)
    var date: String = "${LocalDate.now().dayOfMonth}/${LocalDate.now().month.value}/${LocalDate.now().year}"
        private set

    fun openDatePicker(context: Context) {
        DatePickerBuilder(context, this)
            .pickerType(CalendarView.ONE_DAY_PICKER)
            .headerColor(R.color.my_blue)
            .headerLabelColor(R.color.white)
            .pagesColor(R.color.my_white)
            .selectionColor(R.color.my_blue)
            .selectionLabelColor(R.color.white)
            .daysLabelsColor(R.color.black)
            .dialogButtonsColor(R.color.black)
            .todayColor(Color.BLACK)
            .build()
            .show()
    }

    fun savePlan(plan: Plan) {
        val disposable = AddPlan.execute(getApplication(), plan)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
        compositeDisposable.add(disposable)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onSelect(calendar: List<Calendar>) {
        val selectedDate = calendar[0]
        val day = selectedDate.get(Calendar.DAY_OF_MONTH)
        val month = selectedDate.get(Calendar.MONTH) + 1
        val year = selectedDate.get(Calendar.YEAR)
        date = "$day/$month/$year"
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}