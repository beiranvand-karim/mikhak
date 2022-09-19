package com.example.roadmaintenance.util;

import com.example.roadmaintenance.fragments.getStringText
import com.google.android.material.textfield.TextInputLayout

object FormValidation {

    fun validateNotEmptyFields(vararg inputLayouts: TextInputLayout): Boolean {
        inputLayouts.forEach {
            if (it.getStringText().trim().isNullOrEmpty()) {
                it.showEmptyFieldError()
            } else {
                it.clearError()
            }
        }
        return inputLayouts.all {
            !it.getStringText().trim().isNullOrEmpty()
        }
    }

    fun verifyAllPointInputs(firstPoint: TextInputLayout, secondPoint: TextInputLayout): Boolean {
        val isFirstPointCorrect = validatePoints(firstPoint)
        val isSecondPointCorrect = validatePoints(secondPoint)
        return isFirstPointCorrect && isSecondPointCorrect
    }

    private fun validatePoints(point: TextInputLayout): Boolean {
        return if (!validateNotEmptyFields(point))
            false
        else {
            val text = point.getStringText()
            val values = text.split(",")
            val validated = values.all {
                it.toDoubleOrNull() is Number
            }
            (validated && text.contains(",")).apply {
                if (this)
                    point.showPointError()
                else
                    point.clearError()
                return this
            }
        }
    }

    private fun TextInputLayout.showEmptyFieldError() {
        this.error = "* Required"
    }

    private fun TextInputLayout.showPointError() {
        this.error = "Point should contains latitude and longitude like 11.1111,22.2222"
    }

    private fun TextInputLayout.clearError() {
        this.error = ""
    }
}