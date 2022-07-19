package com.example.roadmaintenance.adapter

import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.findFragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.roadmaintenance.R
import com.example.roadmaintenance.SEND_SELECTED_PATHWAY
import com.example.roadmaintenance.SEND_SELECTED_PATHWAY_FROM_BOTTOM_SHEET
import com.example.roadmaintenance.fragments.LightPostFragment
import com.example.roadmaintenance.fragments.MapsLayout
import com.example.roadmaintenance.models.Pathway


class BottomSheetListAdapter() :
    RecyclerView.Adapter<BottomSheetListAdapter.BottomSheetItemHolder>() {

    var pathList: List<Pathway> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BottomSheetItemHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.bottom_sheet_path_item, parent, false)
        return BottomSheetItemHolder(view)
    }

    override fun onBindViewHolder(holder: BottomSheetItemHolder, position: Int) {

        val path = pathList[position]

        holder.apply {
            pathName.text = path.routeShape?.region?.toString()
            pathInfoSummary.text = pathInfoSummary.text.toString()
                .replace("x", path.lightPosts.size.toString())
            expanderBtn.setOnClickListener {
                TransitionManager.beginDelayedTransition(
                    holder.itemView.rootView as ViewGroup,
                    AutoTransition()
                )
                if (holder.pathInfoLayout.visibility == View.GONE) {
                    holder.pathInfoLayout.visibility = View.VISIBLE
                    holder.expanderBtn.setImageResource(R.drawable.ic_arrow_up)
                } else {
                    holder.expanderBtn.setImageResource(R.drawable.ic_arrow_down)
                    holder.pathInfoLayout.visibility = View.GONE
                }
            }
        }
        holder.goToLightPosts.setOnClickListener {
            val mapLayoutFragment = holder.view.findFragment() as MapsLayout
            val bundle = Bundle()
            bundle.putParcelable(SEND_SELECTED_PATHWAY_FROM_BOTTOM_SHEET, path)
            mapLayoutFragment.setFragmentResult(SEND_SELECTED_PATHWAY_FROM_BOTTOM_SHEET, bundle)
            it.findNavController().navigate(R.id.action_mapsLayout_to_lightPostFragment)
        }

        holder.view.setOnClickListener {
            val mapsLayout = FragmentManager.findFragment<MapsLayout>(holder.view)
            mapsLayout.selectedPath = path
        }
    }

    override fun getItemCount(): Int {
        return if (pathList.isNullOrEmpty())
            0
        else
            pathList.size
    }


    inner class BottomSheetItemHolder(val view: View) : RecyclerView.ViewHolder(view) {

        val pathName: AppCompatTextView = view.findViewById<AppCompatTextView>(R.id.path_name)
        val expanderBtn: AppCompatImageButton =
            view.findViewById<AppCompatImageButton>(R.id.expander_btn)
        val pathInfoLayout: ConstraintLayout =
            view.findViewById<ConstraintLayout>(R.id.hidden_path_info)
        val goToLightPosts: AppCompatImageButton =
            view.findViewById<AppCompatImageButton>(R.id.go_to_lightpost_btn)
        val pathInfoSummary: AppCompatTextView =
            view.findViewById<AppCompatTextView>(R.id.path_info_summary)

    }

}