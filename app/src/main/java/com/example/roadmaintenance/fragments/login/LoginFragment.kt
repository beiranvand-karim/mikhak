package com.example.roadmaintenance.fragments.login

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.roadmaintenance.MainActivity
import com.example.roadmaintenance.R
import com.example.roadmaintenance.databinding.FragmentLoginBinding
import com.example.roadmaintenance.fragments.getStringText
import com.example.roadmaintenance.models.User
import com.example.roadmaintenance.util.FormValidation
import com.example.roadmaintenance.util.Results
import com.example.roadmaintenance.util.clearError
import com.example.roadmaintenance.util.createProgressDialog
import com.example.roadmaintenance.viewmodels.UserViewModel
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private var user: User? = null
    private lateinit var verificationCodeInput: TextInputLayout
    private lateinit var passwordInput: TextInputLayout
    private lateinit var passwordVerify: TextInputLayout
    private lateinit var rememberMe: CheckBox
    private lateinit var loginBtn: Button
    private lateinit var progressDialog: Dialog
    private val args: LoginFragmentArgs by navArgs()
    private var id: String? = null
    private val userViewModel: UserViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

        (activity as MainActivity).supportActionBar?.hide()
        setUpFields()
        onCollectFlows()

        loginBtn.setOnClickListener {
            verifyUser()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Toast.makeText(requireContext(), "We sent the verification code for you", Toast.LENGTH_LONG)
            .show()
    }

    private fun setUpFields() {
        id = args.id
        verificationCodeInput = binding.verificationCode!!
        passwordInput = binding.passwordField
        passwordVerify = binding.passwordVerify!!
        rememberMe = binding.rememberMe
        loginBtn = binding.loginBtn
        progressDialog = createProgressDialog(requireContext())
    }

    private fun onCollectFlows() {
        lifecycleScope.launch {
            userViewModel.results.collectLatest {
                if (it != Results.Status.LOADING)
                    finishLoading()

                when (it) {
                    Results.Status.LOADING -> loading()
                    Results.Status.User_Verified -> loginUser()
                    Results.Status.Success -> onLoggedIn()
                    else -> {
                        Toast.makeText(requireContext(), "Access denied", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun loading() {
        progressDialog.show()
    }

    private fun finishLoading() {
        progressDialog.dismiss()
        progressDialog.cancel()
    }

    private fun verifyUser() {
        if (validateFields()) {
            verificationCodeInput.clearError()
            passwordInput.clearError()
            passwordVerify.clearError()

            userViewModel.verifyUser(
                verificationCodeInput.getStringText().toInt(),
                passwordInput.getStringText(),
                id!!
            )
        }

    }

    private fun validateFields(): Boolean {
        if (FormValidation.validateNotEmptyFields(
                passwordInput,
                passwordVerify,
                verificationCodeInput
            )
        ) {
            if (FormValidation.validatePasswordFieldsAreTheSame(passwordInput, passwordVerify)) {
                return true
            }
        }
        return false
    }

    private fun loginUser() {
        user = User(
            id!!,
            passwordInput.getStringText()
        )
        user?.let {
            userViewModel.logIn(it, rememberMe.isChecked)
        }
    }

    private fun onLoggedIn() {
        if (isVisible && isResumed)
            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("id", id)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.let {
            val bundleId = it.getString("id")
            bundleId.takeUnless { it.isNullOrEmpty() }?.apply {
                id = this
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        (activity as MainActivity).supportActionBar?.show()
    }
}