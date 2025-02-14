package com.andef.myplans.presentation.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.andef.myplans.R
import com.andef.myplans.domain.entities.Plan
import com.andef.myplans.domain.usecases.ChangePlanByIdUseCase
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.builders.DatePickerBuilder
import com.applandeo.materialcalendarview.listeners.OnSelectDateListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.time.LocalDate
import java.util.Calendar
import javax.inject.Inject

class ChangePlanViewModel @Inject constructor(
    private val changePlanByIdUseCase: ChangePlanByIdUseCase
) : ViewModel(),
    OnSelectDateListener {
    private val compositeDisposable = CompositeDisposable()

    var date: String =
        "${LocalDate.now().dayOfMonth}/${LocalDate.now().month.value}/${LocalDate.now().year}"
        private set

    fun lastDate(lastDate: String) {
        date = lastDate
    }

    fun openDatePickerWhite(context: Context) {
        DatePickerBuilder(context, this)
            .pickerType(CalendarView.ONE_DAY_PICKER)
            .headerColor(R.color.my_blue)
            .abbreviationsLabelsColor(R.color.black)
            .headerLabelColor(R.color.white)
            .pagesColor(R.color.my_white)
            .selectionColor(R.color.my_blue)
            .selectionLabelColor(R.color.white)
            .daysLabelsColor(R.color.black)
            .dialogButtonsColor(R.color.black)
            .todayLabelColor(R.color.my_blue)
            .build()
            .show()
    }

    fun openDatePickerBlack(context: Context) {
        DatePickerBuilder(context, this)
            .pickerType(CalendarView.ONE_DAY_PICKER)
            .headerColor(R.color.black)
            .headerLabelColor(R.color.white_text_black)
            .abbreviationsBarColor(R.color.white_text_black)
            .abbreviationsLabelsColor(R.color.black)
            .pagesColor(R.color.white_text_black)
            .selectionColor(R.color.black)
            .selectionLabelColor(R.color.white_text_black)
            .daysLabelsColor(R.color.black)
            .dialogButtonsColor(R.color.black)
            .todayLabelColor(R.color.my_blue)
            .build()
            .show()
    }

    fun changePlan(plan: Plan) {
        val disposable = changePlanByIdUseCase.execute(
            plan.id,
            plan.title,
            plan.date,
            plan.importance
        )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe()
        compositeDisposable.add(disposable)
    }

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