package com.example.roadmaintenance.fragments.login

import android.app.Dialog
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.roadmaintenance.MainActivity
import com.example.roadmaintenance.R
import com.example.roadmaintenance.auth.LoginDialog
import com.example.roadmaintenance.databinding.FragmentCheckBinding
import com.example.roadmaintenance.fragments.getStringText
import com.example.roadmaintenance.util.*
import com.example.roadmaintenance.viewmodels.UserViewModel
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CheckUserFragment : Fragment() {

    private var _binding: FragmentCheckBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by activityViewModels()
    private lateinit var idInput: TextInputLayout
    private lateinit var checkBtn: Button
    private lateinit var navController: NavController
    private lateinit var progressDialog: Dialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCheckBinding.inflate(
            inflater,
            container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpFields()
        checkUserExists()
        (activity as MainActivity).supportActionBar?.hide()
        subscribeListeners()
    }

    private fun checkUserExists() {
        if (userViewModel.isUserSaved())
            navController.navigate(R.id.action_checkFragment_to_homeFragment)
    }

    private fun setUpFields() {
        idInput = binding.userId
        checkBtn = binding.checkUser
        progressDialog = createProgressDialog(requireContext())
        navController = findNavController()
    }

    private fun subscribeListeners() {
        checkBtn.setOnClickListener {
            onCheckUser()
        }
        lifecycleScope.launch {
            userViewModel.results.collectLatest {
                if (it != Results.Status.LOADING)
                    finishLoading()
                idInput.clearError()
                when (it) {
                    Results.Status.LOADING -> loading()
                    Results.Status.First_Login -> onUserCheckedFirstTime()
                    Results.Status.User_Checked -> onUserChecked()
                    Results.Status.Success -> onLoggedIn()
                    else -> idInput.showWrongInputError()
                }
            }
        }
    }

    private fun onCheckUser() {
        if (FormValidation.validateNotEmptyFields(idInput))
            if (validatePhoneOrEmail(idInput.getStringText().trim()))
                userViewModel.checkUser(idInput.getStringText())
            else
                idInput.error = "Id should be email or phone"
    }

    private fun validatePhoneOrEmail(text: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(text).matches() ||
                Patterns.PHONE.matcher(text).matches()
    }

    private fun onUserCheckedFirstTime() {
        val action =
            CheckUserFragmentDirections.actionCheckFragmentToLoginFragment(idInput.getStringText())
        navController.navigate(action)
    }

    private fun onUserChecked() {
        val dialog = LoginDialog()
        val bundle = Bundle()
        bundle.putString("id", idInput.getStringText())
        dialog.arguments = bundle
        dialog.show(this.parentFragmentManager, "Login Dialog")
    }

    private fun onLoggedIn() {
        if (isVisible && isResumed)
            navController.navigate(R.id.action_checkFragment_to_homeFragment)
    }


    private fun finishLoading() {
        progressDialog.dismiss()
        progressDialog.cancel()
    }

    private fun loading() {
        progressDialog.show()
    }

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).supportActionBar?.hide()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}