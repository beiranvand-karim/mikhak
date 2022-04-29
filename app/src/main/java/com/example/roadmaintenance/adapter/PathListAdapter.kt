package com.example.roadmaintenance.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.findFragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.roadmaintenance.R
import com.example.roadmaintenance.REQUEST_KEY_PASS_DATA_TO_LIGHT_POST
import com.example.roadmaintenance.REQUEST_KEY_PASS_PATHWAY
import com.example.roadmaintenance.fragments.HomeFragment
import com.example.roadmaintenance.fragments.LightPostFragment
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
            holder.points.text = "${pathway.firstPoint} - ${pathway.secondPoint}"
            holder.lightposts.text = pathway.lightPosts.size.toString()
            holder.moreDetails.setOnClickListener { view ->
                holder.onCardDetailsClick(view, pathway)
            }
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

        private lateinit var homeFragment : HomeFragment

        var title: TextView = view.findViewById(R.id.path_id)
        var points: TextView = view.findViewById(R.id.points)
        var lightposts: TextView = view.findViewById(R.id.lightpost_count)
        var moreDetails: MaterialButton = view.findViewById(R.id.more_details)

        init {
            view.setOnClickListener(this)
            view.isClickable = true
            view.isFocusable = true
            view.isHovered = true
            moreDetails.setOnClickListener {

                val selectedPath = pathList?.find { pathway ->
                    title.text.contains(pathway.pathId.toString())
                }
                selectedPath?.apply {
                    onCardDetailsClick(it, this)
                }
            }
        }

        override fun onClick(view: View?) {
            view?.isHovered = true
            view?.isSelected = true
            val selectedPath = pathList?.find { pathway ->
                title.text.contains(pathway.pathId.toString())
            }
            onCardDetailsClick(view, selectedPath!!)
        }

        fun onCardDetailsClick(view: View?, pathway: Pathway) {
            view?.findNavController()?.navigate(R.id.action_home_navigation_to_lightPostFragment)
            homeFragment = view!!?.findFragment<HomeFragment>()

            val bundle = Bundle()

            bundle.putParcelable(REQUEST_KEY_PASS_PATHWAY,pathway)
            homeFragment.setFragmentResult(REQUEST_KEY_PASS_DATA_TO_LIGHT_POST,bundle)
        }
    }

}