package com.example.roadmaintenance.repository

import android.content.SharedPreferences
import com.example.roadmaintenance.ID
import com.example.roadmaintenance.PASSWORD
import com.example.roadmaintenance.USERNAME
import com.example.roadmaintenance.models.User

class UserRepository(
    private val sharedPreferences: SharedPreferences
) {

    private lateinit var editor: SharedPreferences.Editor

    fun validateUser(): Boolean {
        if (sharedPreferences.contains(ID) &&
            sharedPreferences.contains(USERNAME) &&
            sharedPreferences.contains(PASSWORD)
        ) {
            return true
        }
        return false
    }

    fun addUser(user: User) {
        editor = sharedPreferences.edit()
        editor.putInt(ID, user.id)
        editor.putString(USERNAME, user.name)
        editor.putString(PASSWORD, user.password)
        editor.apply()
    }

    fun getUser(): User? {
        if (validateUser()) {
            return User(
                sharedPreferences.getInt(ID, 0),
                sharedPreferences.getString(USERNAME, null),
                sharedPreferences.getString(
                    PASSWORD, null
                )
            )
        }
        return null
    }

    fun deleteUser() {
        if (validateUser()) {
            editor.remove(ID)
            editor.remove(USERNAME)
            editor.remove(PASSWORD)
            editor.apply()
        }
    }
}