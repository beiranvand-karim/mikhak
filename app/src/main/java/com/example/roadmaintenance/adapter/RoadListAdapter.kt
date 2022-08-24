package com.example.roadmaintenance.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.roadmaintenance.R
import com.example.roadmaintenance.fragments.HomeFragmentDirections
import com.example.roadmaintenance.models.RegisteredRoad
import com.google.android.material.button.MaterialButton

class RoadListAdapter(
    _roadList: List<RegisteredRoad> = emptyList<RegisteredRoad>()
) :
    RecyclerView.Adapter<RoadListAdapter.ListViewHolder>() {

    var roadList = _roadList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.road_card_view, parent, false)
        return ListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val road = roadList[position]

        road.let { roadway ->
            holder.title.text = "#${roadway.pathId.toInt()}"
            val pointsText = roadway.roadPath?.region?.toString()?.trim()
            holder.points.text = pointsText
            holder.lightposts.text = roadway.lightPostCounts.toString()
        }

        holder.mapBtn.setOnClickListener {
            val action =
                HomeFragmentDirections.actionHomeFragmentToMapsLayout(roadList.toTypedArray(), road)
            it.findNavController().navigate(action)
        }

        holder.itemView.setOnClickListener {
            onItemClick(it, position)
        }
    }

    private fun onItemClick(view: View?, position: Int) {
        view?.isHovered = true
        view?.isSelected = true
        val action =
            HomeFragmentDirections.actionHomeFragmentToLightPostFragment(roadList[position])
        view?.findNavController()?.navigate(action)
    }

    override fun getItemCount(): Int {
        return if (roadList.isNullOrEmpty())
            0
        else
            roadList.size
    }

    inner class ListViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        var title: TextView = view.findViewById(R.id.road_id)
        var points: TextView = view.findViewById(R.id.points)
        var lightposts: TextView = view.findViewById(R.id.lightpost_count)
        var mapBtn: MaterialButton = view.findViewById(R.id.show_map)

        init {
            view.isClickable = true
            view.isFocusable = true
            view.isHovered = true
        }
    }

}