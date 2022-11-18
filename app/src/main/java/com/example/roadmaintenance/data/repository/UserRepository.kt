package com.example.roadmaintenance.data.repository

import android.content.SharedPreferences
import com.example.roadmaintenance.auth.UserEndpoints
import com.example.roadmaintenance.models.User
import com.example.roadmaintenance.retrofitServices.ServiceBuilder
import com.example.roadmaintenance.retrofitServices.serviceFactory.BackendServiceFactory
import com.example.roadmaintenance.retrofitServices.serviceFactory.LoginServiceFactory
import com.example.roadmaintenance.util.Results
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(private val sharedPreferences: SharedPreferences) {

    private val backendService =
        ServiceBuilder(BackendServiceFactory()).buildService(UserEndpoints::class.java)
    private val editor = sharedPreferences.edit()

    suspend fun saveUser(user: User) {
        withContext(Dispatchers.IO) {
            editor.putString("id", user.id)
            editor.putString(user.id, user.password)
            editor.commit()
        }
    }

    suspend fun checkUser(id: String): Results.Status {
        return withContext(Dispatchers.IO) {
            Results.Status.valueOf(backendService.checkUser(id))
        }
    }

    suspend fun verifyUser(verificationCode: Int, password: String): Results.Status {
        return withContext(Dispatchers.IO) {
            val strResult = backendService.verifyUser(verificationCode, password)
            Results.Status.valueOf(strResult)
        }
    }

    fun isUserExists(): Boolean = sharedPreferences.getString("id", null) != null

    fun getPassword(id: String): String? = sharedPreferences.getString(id, null)

    suspend fun logIn(user: User) {
        val loginService =
            ServiceBuilder(LoginServiceFactory(user)).buildService(UserEndpoints::class.java)
        withContext(Dispatchers.IO) {
            loginService.login()
        }
    }

    fun logOut() {
        val id = sharedPreferences.getString("id", "")
        editor.remove(id)
        editor.commit()
    }
}

