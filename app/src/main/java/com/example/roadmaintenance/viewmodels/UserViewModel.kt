package com.example.roadmaintenance.viewmodels

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.roadmaintenance.data.repository.UserRepository
import com.example.roadmaintenance.models.User
import com.example.roadmaintenance.util.Results
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val tag = "Login View Model"
    private val userRepository = UserRepository(
        application.getSharedPreferences("User", Context.MODE_PRIVATE)
    )
    private var _results = MutableSharedFlow<Results.Status>()
    val results get() = _results

    fun checkUser(id: String) {
        viewModelScope.launch {
            results.emit(Results.Status.LOADING)
            try {
                val checkState = userRepository.checkUser(id)
                results.emit(checkState)
            } catch (e: Exception) {
                Log.e(tag + "Request to login user", e.stackTraceToString())
                results.emit(Results.Status.Access_Denied)
            }
        }
    }

    fun verifyUser(
        verificationCode: Int,
        password: String,
        id: String
    ) {
        viewModelScope.launch {
            results.emit(Results.Status.LOADING)
            try {
                val verifyState = userRepository.verifyUser(
                    verificationCode,
                    password,
                    id
                )
                results.emit(verifyState)
            } catch (e: Exception) {
                Log.e(tag + "verify user", e.stackTraceToString())
                results.emit(Results.Status.Access_Denied)
            }
        }
    }

    fun logIn(user: User, shouldSave: Boolean) {
        viewModelScope.launch {
            results.emit(Results.Status.LOADING)
            try {
                val response = userRepository.logIn(user)
                if (shouldSave)
                    saveUser(user)
                results.emit(Results.Status.Success)
            } catch (e: Exception) {
                Log.e(tag + "save user", e.stackTraceToString())
                results.emit(Results.Status.Access_Denied)
            }
        }
    }

    private fun saveUser(user: User) {
        viewModelScope.launch {
            try {
                userRepository.saveUser(user)
            } catch (e: Exception) {
                Log.e(tag + "save user", e.stackTraceToString())
            }
        }
    }

    fun isUserSaved(): Boolean = userRepository.isUserExists()

    fun getPassword(id: String): String? = userRepository.getPassword(id)

    fun logOut() {
        viewModelScope.launch {
            userRepository.logOut()
        }
    }

}