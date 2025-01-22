package com.andef.myplans.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.andef.myplans.R
import com.andef.myplans.domain.entities.Importance
import com.andef.myplans.domain.entities.Plan
import org.w3c.dom.Text

class PlanAdapter: Adapter<PlanAdapter.PlanViewHolder>() {
    private var _plans = ArrayList<Plan>()
    var plans = _plans.toList()
        get() = _plans.toList()
        set(value) {
            _plans = value as ArrayList<Plan>
            field = value.toList()
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlanViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.plan_item,
            parent,
            false
        )
        return PlanViewHolder(view)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: PlanViewHolder, position: Int) {
        val plan = _plans[position]
        val background = when (plan.importance) {
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
        holder.textViewPlanText.text = plan.title
    }

    override fun getItemCount(): Int {
        return _plans.size
    }

    class PlanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewPlanText= itemView.findViewById<TextView>(R.id.textViewPlanText)
        val imageViewImportance= itemView.findViewById<ImageView>(R.id.imageViewImportance)
    }
}