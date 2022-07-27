package com.example.roadmaintenance.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.roadmaintenance.R
import com.example.roadmaintenance.fragments.HomeFragmentDirections
import com.example.roadmaintenance.models.Pathway
import com.google.android.material.button.MaterialButton

class PathListAdapter(
    _pathList: List<Pathway> = emptyList<Pathway>()
) :
    RecyclerView.Adapter<PathListAdapter.ListViewHolder>() {

    var pathList = _pathList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.path_card_view, parent, false)
        return ListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        var path: Pathway

        pathList.let {
            path = it[position]
        }
        path.let { pathway ->
            holder.title.text = "# ${pathway.pathId.toInt()}"
            val pointsText = pathway.routeShape?.region?.toString()?.trim()
            holder.points.text = pointsText
            holder.lightposts.text = pathway.lightPosts.size.toString()
        }

        holder.mapBtn.setOnClickListener {
            val action =
                HomeFragmentDirections.actionHomeFragmentToMapsLayout(pathList.toTypedArray(), path)
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
            HomeFragmentDirections.actionHomeFragmentToLightPostFragment(pathList[position])
        view?.findNavController()?.navigate(action)
    }

    override fun getItemCount(): Int {
        return if (pathList.isNullOrEmpty())
            0
        else
            pathList.size
    }

    inner class ListViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        var title: TextView = view.findViewById(R.id.path_id)
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