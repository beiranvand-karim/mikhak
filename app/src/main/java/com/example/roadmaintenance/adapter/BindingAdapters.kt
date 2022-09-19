package com.example.roadmaintenance.adapter

import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.navigation.findNavController
import com.example.roadmaintenance.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class BindingAdapters {
    companion object {

        @BindingAdapter("android:expandFloatingButton")
        @JvmStatic
        fun expandFloatingButtons(view: View, isExpanded: Boolean) {
            val fab = (view as FloatingActionButton)
            if (isExpanded)
                fab.setDrawable(R.drawable.ic_close)
            else
                fab.setDrawable(R.drawable.ic_add)
        }

        private fun FloatingActionButton.setDrawable(resId: Int) {
            this.setImageDrawable(
                ContextCompat.getDrawable(
                    this.context,
                    resId
                )
            )
        }

        @BindingAdapter("android:showView")
        @JvmStatic
        fun showView(view: View, shouldShow: Boolean) {
            if (shouldShow)
                view.visibility = View.VISIBLE
            else
                view.visibility = View.GONE
        }

        @BindingAdapter("android:navigateToFormFragment")
        @JvmStatic
        fun navigateToFormFragment(view: View, navigate: Boolean) {
            view.setOnClickListener {
                if (navigate) {
                    val navController = view.findNavController()
                    navController.navigate(R.id.action_homeFragment_to_formFragment)
                }
            }
        }
    }
}