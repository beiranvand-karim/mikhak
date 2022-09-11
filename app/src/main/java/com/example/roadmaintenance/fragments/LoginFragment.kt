package com.example.roadmaintenance.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.roadmaintenance.MainActivity
import com.example.roadmaintenance.R
import com.example.roadmaintenance.databinding.FragmentLoginBinding
import com.example.roadmaintenance.models.User
import com.google.android.material.textfield.TextInputLayout

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private var user: User? = null
    private lateinit var nameInput: TextInputLayout
    private lateinit var passwordInput: TextInputLayout
    private lateinit var rememberMe: CheckBox

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        (activity as MainActivity).supportActionBar?.hide()

        nameInput = binding.usernameField
        passwordInput = binding.passwordField
        rememberMe = binding.rememberMe


        binding.loginBtn.setOnClickListener {
            onLoginUser()
        }

        return binding.root
    }


    private fun onLoginUser() {
        if (validationLogin()) {
            user = User(
                1,
                nameInput.getValue(),
                passwordInput.getValue()
            )
            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
        }
    }

    private fun validationLogin(): Boolean {
        if (nameInput.getValue().trim().isEmpty()) {
            nameInput.error = "Please Enter your name !!!"
            return false
        } else if (passwordInput.getValue().trim().isEmpty()) {
            passwordInput.error = "Please Enter your password !!!"
            return false
        }
        return true
    }

    private fun TextInputLayout.getValue(): String = this.editText?.text.toString()

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        (activity as MainActivity).supportActionBar?.show()
    }

}