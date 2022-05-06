package com.example.roadmaintenance.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.findFragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.roadmaintenance.R
import com.example.roadmaintenance.SEND_PATHWAY
import com.example.roadmaintenance.SEND_SELECTED_PATHWAY
import com.example.roadmaintenance.fragments.HomeFragment
import com.example.roadmaintenance.models.Pathway
import com.google.android.material.button.MaterialButton

class PathListAdapter(
    private var pathList: MutableList<Pathway>?
) :
    RecyclerView.Adapter<PathListAdapter.ListViewHolder>() {

    fun setPathList(pathList: MutableList<Pathway>?) {
        this.pathList = pathList
        this.notifyDataSetChanged()
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
        path?.let { pathway ->
            holder.title.text = "# ${pathway.pathId}"
            holder.points.text = "${pathway.latitude_1} - ${pathway.longitude_1}"
            holder.lightposts.text = pathway.lightPosts.size.toString()
        }
    }

    override fun getItemCount(): Int {
        return if (pathList.isNullOrEmpty())
            0
        else
            pathList!!.size
    }

    inner class ListViewHolder(private val view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {

        private lateinit var homeFragment: HomeFragment

        var title: TextView = view.findViewById(R.id.path_id)
        var points: TextView = view.findViewById(R.id.points)
        var lightposts: TextView = view.findViewById(R.id.lightpost_count)
        var mapBtn: MaterialButton = view.findViewById(R.id.show_map)

        init {
            view.setOnClickListener(this)
            view.isClickable = true
            view.isFocusable = true
            view.isHovered = true

            mapBtn.setOnClickListener {
                setFragmentResults(view,getSelectedPath()!!)
                it.findNavController()?.navigate(R.id.action_home_fragment_to_mapsFragment)
            }
        }

        override fun onClick(view: View?) {
            view?.isHovered = true
            view?.isSelected = true

            setFragmentResults(view, getSelectedPath()!!)
            view?.findNavController()?.navigate(R.id.action_home_navigation_to_lightPostFragment)
        }

        private fun getSelectedPath() : Pathway? {
            return pathList?.find { pathway ->
                title.text.contains(pathway.pathId.toString())
            }
        }

        private fun setFragmentResults(view: View?, pathway: Pathway) {
            homeFragment = view!!.findFragment()
            val bundle = Bundle()
            bundle.putParcelable(SEND_SELECTED_PATHWAY, pathway)
            homeFragment.setFragmentResult(SEND_PATHWAY, bundle)
        }

    }

}