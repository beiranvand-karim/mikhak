package com.example.roadmaintenance.fragments.lp

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.roadmaintenance.R
import com.example.roadmaintenance.databinding.FragmentProfileBinding
import com.example.roadmaintenance.models.LightPost
import com.example.roadmaintenance.models.enums.LightPostStatus

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val args: ProfileFragmentArgs by navArgs()
    private lateinit var lightPost: LightPost

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lightPost = args.lightpost

        val powerStatusDrawable = ContextCompat.getDrawable(
            requireContext(),
            R.drawable.ic_power_status
        )
        val maintenanceStatusDrawable = ContextCompat.getDrawable(
            requireContext(),
            R.drawable.ic_build_circle
        )
        val lightColor = ContextCompat.getColor(requireContext(), R.color.light)

        val windowColor = ContextCompat.getColor(
            requireContext(),
            R.color.window_background
        )

        val secondaryColor = ContextCompat.getColor(
            requireContext(),
            R.color.secondary
        )

        with(binding) {
            profileId.text = lightPost.lightPostId.toString()
            profileHeight.text = lightPost.height.toString()
            profileCosts.text = lightPost.costs.toString()
            profilePower.text = lightPost.power.toString()
            profileStatus.text = lightPost.status.toString()
            profileCauseFailure.text = lightPost.causeOfFailure.toString()
            profileCompany.text = lightPost.contractingCompany.toString()
            profileSides.text = lightPost.sides.toString()
            profileLightProduction.text = lightPost.lightProductionType.toString()

            when (lightPost.status) {
                LightPostStatus.On -> setImageStatus(powerStatusDrawable, lightColor)
                LightPostStatus.Off -> setImageStatus(powerStatusDrawable, windowColor)
                LightPostStatus.Maintenance -> setImageStatus(
                    maintenanceStatusDrawable,
                    secondaryColor
                )
            }
        }
    }

    private fun setImageStatus(drawable: Drawable?, tintColor: Int) {
        with(binding.imageStatus) {
            drawable?.setTint(tintColor)
            background = drawable
        }
    }

}