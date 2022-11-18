package com.example.roadmaintenance.util

import com.example.roadmaintenance.fragments.getStringText
import com.google.android.material.textfield.TextInputLayout

fun TextInputLayout.clearError() {
    this.error = ""
}

fun TextInputLayout.showWrongInputError() {
    this.error = this.getStringText() + " is incorrect"
}

object FormValidation {

    fun validateNotEmptyFields(vararg inputLayouts: TextInputLayout): Boolean {
        inputLayouts.forEach {
            if (it.getStringText().trim().isEmpty()) {
                it.showEmptyFieldError()
            } else {
                it.clearError()
            }
        }
        return inputLayouts.all {
            it.getStringText().trim().isNotEmpty()
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
                    point.clearError()
                else
                    point.showPointError()
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

    fun validatePasswordFieldsAreTheSame(
        passwordInput: TextInputLayout,
        passwordVerify: TextInputLayout
    ): Boolean {
        return if (validatePasswordFields(passwordInput) &&
            validatePasswordFields(passwordVerify)
        ) {
            if (passwordInput.getStringText() == passwordVerify.getStringText()) {
                true
            } else {
                passwordVerify.error = "Passwords doesn't match"
                false
            }
        } else
            false
    }

    fun validatePasswordFields(passwordInput: TextInputLayout): Boolean {
        val password = passwordInput.getStringText().trim()
        return if (password.length <= 8) {
            passwordInput.error = "Password must be at least 8 character"
            false
        } else validateNotEmptyFields(passwordInput)
    }

}