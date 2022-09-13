package com.example.roadmaintenance.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.example.roadmaintenance.R
import com.example.roadmaintenance.models.LightPost

class LightPostAdapter :
    RecyclerView.Adapter<LightPostAdapter.LightPostHolder>() {

    var lightPostList: List<LightPost>? = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LightPostHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.lightpost_card_view, parent, false)

        return LightPostHolder(view)
    }

    override fun onBindViewHolder(holder: LightPostHolder, position: Int) {
        var lp: LightPost? = null
        lp = lightPostList?.let {
            it[position]
        }
        lp?.let {
            holder.id.text = "#${it.lightPostId.toInt()}"
            holder.power.text = "${it.power.toInt()} W"
            holder.lightProduction.text = it.lightProductionType
            holder.sides.text = it.sides.name
            holder.height.text = "${it.height.toInt()} M"
        }
    }

    override fun getItemCount(): Int {
        return if (lightPostList.isNullOrEmpty())
            0
        else
            lightPostList!!.size
    }


    inner class LightPostHolder(view: View) : RecyclerView.ViewHolder(view) {

        val id: AppCompatTextView = view.findViewById(R.id.id)
        val power: AppCompatTextView = view.findViewById(R.id.power)
        val sides: AppCompatTextView = view.findViewById(R.id.sides)
        val lightProduction: AppCompatTextView = view.findViewById(R.id.light_production)
        val height: AppCompatTextView = view.findViewById(R.id.height)
    }
}