package com.andef.myplans.presentation.ui.fragment

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
import com.andef.myplans.databinding.FragmentPlansBinding
import com.andef.myplans.domain.entities.Plan
import com.andef.myplans.presentation.adapter.PlanAdapter
import com.andef.myplans.presentation.app.MyPlansApplication
import com.andef.myplans.presentation.factory.ViewModelFactory
import com.andef.myplans.presentation.ui.activity.PlanActivity
import com.andef.myplans.presentation.ui.state.State
import com.andef.myplans.presentation.ui.viewmodel.PlansFragmentViewModel
import javax.inject.Inject

class PlansFragment : Fragment() {
    private var _binding: FragmentPlansBinding? = null
    private val binding
        get() = _binding!!

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[PlansFragmentViewModel::class.java]
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
        _binding = FragmentPlansBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerViewAndAdapter()
        observeViewModel()
        initItemTouchHelper()
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
                }
            })
            .apply {
                attachToRecyclerView(binding.recyclerViewPlansAndDates)
            }
    }

    private fun initRecyclerViewAndAdapter() {
        adapter.setOnPlanClickListener { plan ->
            showChangeScreen(plan)
        }
        binding.recyclerViewPlansAndDates.adapter = adapter
    }

    private fun showChangeScreen(plan: Plan) {
        val intent = PlanActivity.newIntentChange(requireActivity(), plan)
        startActivity(intent)
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
        fun newInstance() = PlansFragment()
    }
}