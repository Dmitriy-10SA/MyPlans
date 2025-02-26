package com.andef.myplans.presentation.ui.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.andef.myplans.R
import com.andef.myplans.databinding.FragmentCalendarBinding
import com.andef.myplans.domain.entities.Plan
import com.andef.myplans.presentation.adapter.PlanAdapter
import com.andef.myplans.presentation.app.MyPlansApplication
import com.andef.myplans.presentation.factory.ViewModelFactory
import com.andef.myplans.presentation.ui.activity.PlanActivity
import com.andef.myplans.presentation.ui.state.State
import com.andef.myplans.presentation.ui.viewmodel.CalendarFragmentViewModel
import com.applandeo.materialcalendarview.CalendarDay
import com.applandeo.materialcalendarview.EventDay
import com.applandeo.materialcalendarview.listeners.OnCalendarDayClickListener
import java.time.LocalDate
import java.util.Calendar
import javax.inject.Inject

class CalendarFragment : Fragment() {
    private var _binding: FragmentCalendarBinding? = null
    private val binding
        get() = _binding!!

    private val localDate = LocalDate.now()
    private var lastDate = "${localDate.dayOfMonth}/${localDate.month.value}/${localDate.year}"

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[CalendarFragmentViewModel::class.java]
    }

    private val component by lazy {
        (requireActivity().application as MyPlansApplication).component
    }

    private val adapter by lazy {
        PlanAdapter()
    }

    private lateinit var itemTouchHelper: ItemTouchHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        component.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerViewAndAdapter()
        observeViewModel()
        initItemTouchHelper()
        initViewModel()
        initCalendar()
    }

    private fun initCalendar() {
        binding.calendarView.setOnCalendarDayClickListener(object : OnCalendarDayClickListener {
            override fun onClick(calendarDay: CalendarDay) {
                loadPlansByDate(calendarDay.calendar)
            }
        })
        viewModel.getPlansByDate(lastDate)
    }

    @SuppressLint("SetTextI18n")
    private fun loadPlansByDate(calendar: Calendar) {
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)
        val date = "$day/$month/$year"
        viewModel.getPlansByDate(date)
        lastDate = date
    }

    private fun initRecyclerViewAndAdapter() {
        adapter.setOnPlanClickListener { plan ->
            showChangeScreen(plan)
        }
        binding.recyclerViewPlansInCalendar.adapter = adapter
    }

    private fun showChangeScreen(plan: Plan) {
        val intent = PlanActivity.newIntentChange(requireActivity(), plan)
        startActivity(intent)
    }

    @Suppress("DEPRECATION")
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
        viewModel.plans.observe(viewLifecycleOwner) { state ->
            when(state) {
                State.Error -> showErrorToast()
                State.Initial -> {}
                is State.Plans -> {
                    showEventDays(state.plans)
                }
            }
        }
        viewModel.plansByDate.observe(viewLifecycleOwner) { state ->
            when (state) {
                State.Error -> showErrorToast()
                State.Initial -> {}
                is State.Plans -> {
                    adapter.plans = state.plans
                }
            }
        }
    }

    private fun initItemTouchHelper() {
        itemTouchHelper = ItemTouchHelper(
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
                    val plan = adapter.plans[position]
                    viewModel.removePlan(plan.id)
                    viewModel.getPlansByDate(lastDate)
                }
            })
            .apply {
                attachToRecyclerView(binding.recyclerViewPlansInCalendar)
            }
    }

    private fun observeViewModel() {
        viewModel.plans.observe(viewLifecycleOwner) {
            when (it) {
                State.Error -> {
                    showErrorToast()
                }

                is State.Plans -> {
                    adapter.plans = it.plans
                }

                State.Initial -> {}
            }
        }
    }

    private fun showErrorToast() {
        Toast.makeText(
            requireActivity(),
            R.string.error,
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        @JvmStatic
        fun newInstance() = CalendarFragment()
    }
}