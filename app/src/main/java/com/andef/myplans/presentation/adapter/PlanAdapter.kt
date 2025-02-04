package com.andef.myplans.presentation.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.andef.myplans.R
import com.andef.myplans.domain.entities.Importance
import com.andef.myplans.domain.entities.Plan
import com.andef.myplans.domain.entities.PlanItem
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class PlanAdapter : Adapter<PlanAdapter.PlanViewHolder>() {
    var isDarkTheme = false
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private var _plans = ArrayList<PlanItem>()
    var plans = listOf<Plan>()
        get() {
            val plansOut = ArrayList<Plan>()
            _plans.forEach {
                if (it is PlanItem.DateInItem) {
                    plansOut.add(Plan(0, "0", "0", Importance.LOW))
                } else {
                    plansOut.add((it as PlanItem.PlanInItem).plan)
                }
            }
            return plansOut
        }
        set(value) {
            field = value.toList()
            val groupedPlans = field.groupBy { it.date }
                .toSortedMap { date1, date2 ->
                    val formatter: (String) -> String = { date ->
                        val parts = date.split("/")
                        "%04d-%02d-%02d".format(parts[2].toInt(), parts[1].toInt(), parts[0].toInt())
                    }
                    formatter(date1).compareTo(formatter(date2))
                }
            val items = ArrayList<PlanItem>()
            groupedPlans.forEach { (date, plans) ->
                items.add(PlanItem.DateInItem(date))
                items.addAll(plans.map { PlanItem.PlanInItem(it) } )
            }
            _plans = items
            notifyDataSetChanged()
        }

    private var onPlanClickListener: OnPlanClickListener? = null
    fun setOnPlanClickListener(onPlanClickListener: OnPlanClickListener) {
        this.onPlanClickListener = onPlanClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanViewHolder {
        val resId = if (viewType == VIEW_DATE){
            R.layout.date_item
        } else {
            R.layout.plan_item
        }
        val view = LayoutInflater.from(parent.context).inflate(
            resId,
            parent,
            false
        )
        return PlanViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        return if (_plans[position] is PlanItem.DateInItem) {
            VIEW_DATE
        } else {
            VIEW_PLAN
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("UseCompatLoadingForDrawables", "SimpleDateFormat", "SetTextI18n")
    override fun onBindViewHolder(holder: PlanViewHolder, position: Int) {
        val plan = _plans[position]
        if (plan is PlanItem.DateInItem) {
            if (isDarkTheme) {
                holder.textViewPlanText.background = holder.itemView.context.getDrawable(R.drawable.frame_black)
                holder.textViewPlanText.setTextColor(Color.rgb(212, 212, 212))
            } else {
                holder.textViewPlanText.background = holder.itemView.context.getDrawable(R.drawable.frame)
                holder.textViewPlanText.setTextColor(Color.BLACK)
            }
            val dayMonthYear = plan.date.split("/")
            val date = "${dayMonthYear[0]}.${dayMonthYear[1]}.${dayMonthYear[2]}"
            val sdf = SimpleDateFormat("d.M.yyyy")
            val currentDate = sdf.format(Date())
            val formatter = DateTimeFormatter.ofPattern("d.M.yyyy")
            val yesterday = LocalDate.now().minusDays(1).format(formatter)
            val tomorrow = LocalDate.now().plusDays(1).format(formatter)
            when (date) {
                currentDate -> holder.textViewPlanText.text = holder.itemView.context.getString(R.string.today)
                yesterday -> holder.textViewPlanText.text = holder.itemView.context.getString(R.string.yesterday)
                tomorrow -> holder.textViewPlanText.text = holder.itemView.context.getString(R.string.tomorrow)
                else -> {
                    val inputFormatter = DateTimeFormatter.ofPattern("d.M.yyyy")
                    val outputFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
                    val outputDate = LocalDate.parse(date, inputFormatter)
                    val formattedDate = outputDate.format(outputFormatter)
                    holder.textViewPlanText.text = formattedDate
                }
            }
        } else {
            val planItem = (plan as PlanItem.PlanInItem).plan
            if (isDarkTheme) {
                holder.cardViewPlanItem.setCardBackgroundColor(Color.rgb(85, 85, 85))
                holder.textViewPlanText.setTextColor(Color.rgb(212, 212, 212))
                val background = when (planItem.importance) {
                    Importance.LOW -> {
                        holder.itemView.context.getDrawable(R.drawable.green_circle_black)
                    }

                    Importance.MEDIUM -> {
                        holder.itemView.context.getDrawable(R.drawable.orange_circle_black)
                    }

                    else -> {
                        holder.itemView.context.getDrawable(R.drawable.red_circle)
                    }
                }
                holder.imageViewImportance.background = background
            } else {
                holder.cardViewPlanItem.setCardBackgroundColor(Color.rgb(253, 253, 253))
                holder.textViewPlanText.setTextColor(Color.BLACK)
                val background = when (planItem.importance) {
                    Importance.LOW -> {
                        holder.itemView.context.getDrawable(R.drawable.green_circle)
                    }

                    Importance.MEDIUM -> {
                        holder.itemView.context.getDrawable(R.drawable.orange_circle)
                    }

                    else -> {
                        holder.itemView.context.getDrawable(R.drawable.red_circle)
                    }
                }
                holder.imageViewImportance.background = background
            }
            holder.textViewPlanText.text = planItem.title
            holder.textViewPlanText.setOnClickListener {
                if (onPlanClickListener != null) {
                    onPlanClickListener!!.onClick(planItem)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return _plans.size
    }

    interface OnPlanClickListener {
        fun onClick(plan: Plan)
    }

    class PlanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewPlanText = itemView.findViewById<TextView>(R.id.textViewPlanText)
        val imageViewImportance = itemView.findViewById<ImageView>(R.id.imageViewImportance)
        val cardViewPlanItem = itemView.findViewById<CardView>(R.id.cardViewPlanItem)
    }

    companion object {
        private const val VIEW_DATE = 1
        private const val VIEW_PLAN = 2
    }
}