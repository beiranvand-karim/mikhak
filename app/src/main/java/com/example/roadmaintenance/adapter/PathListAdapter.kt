package com.example.roadmaintenance.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.roadmaintenance.R
import com.example.roadmaintenance.models.Pathway

class PathListAdapter(
    private var pathList: MutableList<Pathway>?
) :
    RecyclerView.Adapter<PathListAdapter.ListViewHolder>() {

    fun setPathList(pathList: MutableList<Pathway>?) {
        this.pathList = pathList
        this.notifyDataSetChanged()
    }

    inner class ListViewHolder(private val view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {

        var imagePath: ImageView = view.findViewById(R.id.path_image)
        var title: TextView = view.findViewById(R.id.path_id)
        var firstPoint: TextView = view.findViewById(R.id.first_point)
        var secondPoint: TextView = view.findViewById(R.id.second_point)

        init {
            view.setOnClickListener(this)
            view.isClickable = true
            view.isFocusable = true
            view.isHovered = true
        }

        override fun onClick(view: View?) {
            println(title)
        }


    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.path_card_view, parent, false)
        return ListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        var path: Pathway? = null
        pathList?.let {
            path = it[position]
        }
        path?.let {
            holder.title.text = "# ${it.pathId}"
            holder.firstPoint.text = it.firstPoint
            holder.secondPoint.text = it.secondPoint
        }
    }

    override fun getItemCount(): Int {
        return if (pathList.isNullOrEmpty())
            0
        else
            pathList!!.size
    }

}