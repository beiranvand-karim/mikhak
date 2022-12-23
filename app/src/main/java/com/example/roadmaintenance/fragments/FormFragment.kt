package com.example.roadmaintenance.fragments

import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.example.roadmaintenance.R
import com.example.roadmaintenance.databinding.FragmentFormBinding
import com.example.roadmaintenance.models.CustomPoint
import com.example.roadmaintenance.models.LightPost
import com.example.roadmaintenance.models.RegisteredRoad
import com.example.roadmaintenance.models.Road
import com.example.roadmaintenance.models.enums.CablePass
import com.example.roadmaintenance.models.enums.LightPostSides
import com.example.roadmaintenance.models.enums.LightPostStatus
import com.example.roadmaintenance.util.FormValidation
import com.example.roadmaintenance.viewmodels.RoadViewModel
import com.google.android.material.textfield.TextInputLayout

fun TextInputLayout.getStringText(): String = this.editText?.text.toString()

class FormFragment : Fragment() {

    private var _binding: FragmentFormBinding? = null
    private val binding get() = _binding!!
    private val roadBinding get() = _binding!!.roadForm
    private val lPFormBinding get() = _binding!!.lightpostForm
    private val roadViewModel: RoadViewModel by activityViewModels()
    private val args by navArgs<FormFragmentArgs>()
    private lateinit var roadIdInput: TextInputLayout
    private lateinit var firstPointInput: TextInputLayout
    private lateinit var secondPointInput: TextInputLayout
    private lateinit var widthInput: TextInputLayout
    private lateinit var distanceLps: TextInputLayout
    private lateinit var cablePassInput: AutoCompleteTextView
    private lateinit var lPIdInput: TextInputLayout
    private lateinit var sidesInput: AutoCompleteTextView
    private lateinit var heightInput: TextInputLayout
    private lateinit var powerInput: TextInputLayout
    private lateinit var lightProduction: TextInputLayout
    private lateinit var statusInput: AutoCompleteTextView
    private lateinit var causeOfFailure: TextInputLayout
    private lateinit var contractingCo: TextInputLayout
    private lateinit var costs: TextInputLayout
    private var road: RegisteredRoad? = null
    private var lightPost: LightPost? = null
    private val TextInputLayout.getDouble get() = this.getStringText().toDoubleOrNull()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFormBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        setFields()
        setDropDownMenus()
        return binding.root
    }

    private fun setFields() {
        roadIdInput = roadBinding.roadIdInput
        widthInput = roadBinding.widthInput
        firstPointInput = roadBinding.firstPointInput
        secondPointInput = roadBinding.secondPointInput
        distanceLps = roadBinding.distanceBetweenLpsInput
        cablePassInput = roadBinding.cablePassInput
        lPIdInput = lPFormBinding.lpIdInput
        heightInput = lPFormBinding.heightInput
        powerInput = lPFormBinding.powerInput
        lightProduction = lPFormBinding.productionTypeInput
        causeOfFailure = lPFormBinding.causeOfFailure
        contractingCo = lPFormBinding.contractingCo
        costs = lPFormBinding.costs
        sidesInput = lPFormBinding.sidesInput
        statusInput = lPFormBinding.statusInput
    }

    private fun setDropDownMenus() {
        val cableConnections = resources.getStringArray(R.array.cable_connection)
        val status = resources.getStringArray(R.array.status)
        val sides = resources.getStringArray(R.array.sides)
        val cableAdapter = getAdapter(cableConnections)
        val statusAdapter = getAdapter(status)
        val sidesAdapter = getAdapter(sides)
        cablePassInput.setAdapter(cableAdapter)
        statusInput.setAdapter(statusAdapter)
        sidesInput.setAdapter(sidesAdapter)
    }

    private fun getAdapter(values: Array<String>) = ArrayAdapter(
        requireContext(),
        R.layout.drop_down_item,
        values
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        statusInput.setOnItemClickListener { _, _, i, _ ->
            lPFormBinding.causeOfFailure.isEnabled = i != 0
        }
        args.road.takeUnless { it == null }?.apply {
            setRoadForm(this)
        }
    }

    private fun setRoadForm(road: RegisteredRoad) {
        road.let {
            roadIdInput.setValue(it.roadId.toString())
            cablePassInput.setText(it.cablePass.toString())
            distanceLps.setValue(it.distanceEachLightPost.toString())
            widthInput.setValue(it.width.toString())
            firstPointInput.setValue(it.points!![0].toString())
            secondPointInput.setValue(it.points!![1].toString())
        }
    }

    private fun TextInputLayout.setValue(value: String) {
        this.editText?.setText(value)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.form_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        super.onOptionsItemSelected(item)
        when (item.itemId) {
            R.id.submit -> {
                if (validateRequireFields()) {
                    createRoad()
                    createLightPost()
                    submitRoad()
                    navigateToHome()
                    return true
                }
            }
        }
        return false
    }

    private fun validateRequireFields(): Boolean {
        val fieldsAreFilled = FormValidation.validateNotEmptyFields(
            roadIdInput,
            lPIdInput,
        )
        val pointsAreFilled = FormValidation.verifyAllPointInputs(
            firstPointInput,
            secondPointInput
        )

        return pointsAreFilled && fieldsAreFilled
    }

    private fun createRoad() {
        road = RegisteredRoad(
            roadIdInput.getStringText().toDouble(),
            widthInput.getDouble,
            distanceLps.getDouble,
            CablePass.valueOf(cablePassInput.text.toString()),
            1,
            getPoints(
                firstPointInput.getStringText(),
                secondPointInput.getStringText()
            )
        )
    }

    private fun getPoints(vararg strPoint: String): List<CustomPoint> {
        val points = arrayListOf<CustomPoint>()
        strPoint.forEach {
            points.add(extractLocations(it))
        }
        return points
    }

    private fun extractLocations(str: String): CustomPoint {
        val firstLocs = str.split(",")
        return CustomPoint(
            firstLocs[0].toDouble(),
            firstLocs[1].toDouble()
        )
    }

    private fun createLightPost() {
        lightPost = LightPost(
            lPIdInput.getStringText().toDouble(),
            LightPostSides.valueOf(sidesInput.text.toString()),
            heightInput.getDouble,
            powerInput.getDouble,
            lightProduction.getStringText(),
            LightPostStatus.valueOf(statusInput.text.toString()),
            causeOfFailure.getStringText(),
            contractingCo.getStringText(),
            costs.getDouble?.toLong(),
            Road(road!!.roadId)
        )
    }

    private fun submitRoad() {
        road!!.lightPosts = arrayListOf(lightPost!!)
        roadViewModel.registerEntireLightState(road!!)
    }

    private fun navigateToHome() {
        view?.findNavController()?.popBackStack()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}