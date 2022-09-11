package com.example.roadmaintenance.adapter

import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.roadmaintenance.R
import com.example.roadmaintenance.fragments.MapsLayout
import com.example.roadmaintenance.fragments.MapsLayoutDirections
import com.example.roadmaintenance.models.RegisteredRoad
import com.google.android.material.card.MaterialCardView


class BottomSheetListAdapter :
    RecyclerView.Adapter<BottomSheetListAdapter.BottomSheetItemHolder>() {

    var registeredRoads: List<RegisteredRoad> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BottomSheetItemHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.bottom_sheet_road_item, parent, false)
        return BottomSheetItemHolder(view)
    }

    override fun onBindViewHolder(holder: BottomSheetItemHolder, position: Int) {
        val roadway = registeredRoads[position]
        holder.apply {
            roadName.text = roadway.roadPath?.region
            roadInfoSummary.text = roadInfoSummary.text.toString()
                .replace("x", roadway.lightPostCounts.toString())
            expanderBtn.setOnClickListener {
                TransitionManager.beginDelayedTransition(
                    itemView.rootView as ViewGroup,
                    AutoTransition()
                )
                if (roadInfoLayout.visibility == View.GONE) {
                    roadInfoLayout.visibility = View.VISIBLE
                    expanderBtn.setImageResource(R.drawable.ic_arrow_up)
                } else {
                    expanderBtn.setImageResource(R.drawable.ic_arrow_down)
                    roadInfoLayout.visibility = View.GONE
                }
            }
            goToLightPosts.setOnClickListener {
                val action = MapsLayoutDirections.actionMapsLayoutToLightPostFragment(roadway)
                it.findNavController().navigate(action)
            }
            view.setOnClickListener {
                expanderBtn.callOnClick()
                val mapsLayout = FragmentManager.findFragment<MapsLayout>(holder.view)
                mapsLayout.selectedRoad = roadway
                (holder.view as MaterialCardView).apply {
                    strokeWidth = 4
                    strokeColor = ContextCompat.getColor(context,R.color.secondary)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return if (registeredRoads.isNullOrEmpty())
            0
        else
            registeredRoads.size
    }


    inner class BottomSheetItemHolder(val view: View) : RecyclerView.ViewHolder(view) {

        val roadName: AppCompatTextView = view.findViewById<AppCompatTextView>(R.id.road_name)
        val expanderBtn: AppCompatImageButton =
            view.findViewById<AppCompatImageButton>(R.id.expander_btn)
        val roadInfoLayout: ConstraintLayout =
            view.findViewById<ConstraintLayout>(R.id.hidden_road_layout_info)
        val goToLightPosts: AppCompatImageButton =
            view.findViewById<AppCompatImageButton>(R.id.go_to_lightpost_btn)
        val roadInfoSummary: AppCompatTextView =
            view.findViewById<AppCompatTextView>(R.id.road_info_summary)
    }
}