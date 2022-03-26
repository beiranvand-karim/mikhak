package com.example.roadmaintenance.repositories

import android.content.SharedPreferences
import com.example.roadmaintenance.ID
import com.example.roadmaintenance.PASSWORD
import com.example.roadmaintenance.USERNAME
import com.example.roadmaintenance.models.User

class UserRepository(
    private val sharedPreferences: SharedPreferences
) {

    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

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