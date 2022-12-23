package com.example.roadmaintenance.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.roadmaintenance.R
import com.example.roadmaintenance.fragments.getStringText
import com.example.roadmaintenance.models.User
import com.example.roadmaintenance.util.FormValidation
import com.example.roadmaintenance.viewmodels.UserViewModel
import com.google.android.material.textfield.TextInputLayout

class LoginDialog : DialogFragment() {

    private lateinit var loginBtn: Button
    private lateinit var passwordField: TextInputLayout
    private lateinit var rememberMe: CheckBox
    private lateinit var closeBtn: ImageButton
    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.login_dialog,container,false)
        setUpFields(view)
        return view
    }

    private fun setUpFields(dialogView: View) {
        rememberMe = dialogView.findViewById(R.id.remember_me_login_dialog)
        loginBtn = dialogView.findViewById(R.id.login_btn_dialog)
        passwordField = dialogView.findViewById(R.id.user_password)
        closeBtn = dialogView.findViewById(R.id.close_dialog)
        closeBtn.setOnClickListener {
            this.dismiss()
        }
        loginBtn.setOnClickListener {
            loginUser(rememberMe.isChecked)
        }
    }

    private fun loginUser(shouldSaveUser: Boolean) {
        if (FormValidation.validatePasswordFields(passwordField)) {
            arguments?.let {
                it.getString("id")?.let { id ->
                    val user = User(id, passwordField.getStringText())
                    userViewModel.logIn(user, shouldSaveUser)
                }
            }
        }
        dismiss()
    }
}