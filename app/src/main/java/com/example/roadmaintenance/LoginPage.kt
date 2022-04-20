package com.example.roadmaintenance

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.example.roadmaintenance.models.User
import com.example.roadmaintenance.repositories.UserRepository

class LoginPage : AppCompatActivity() {

    private lateinit var user: User
    private lateinit var nameInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: Button
    private lateinit var rememberMe: CheckBox
    private lateinit var userRepository: UserRepository


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)

        nameInput = findViewById(R.id.username_field)
        passwordInput = findViewById(R.id.password_field)
        loginButton = findViewById(R.id.login)
        rememberMe = findViewById(R.id.remember_me)
        supportActionBar?.hide()

        userRepository = UserRepository(getSharedPreferences(USER_PREFERENCES, MODE_PRIVATE))
        onCheckLoginState()
    }

    private fun onCheckLoginState() {
        if (userRepository.validateUser()) {
            user = userRepository.getUser()!!
            Toast.makeText(applicationContext, user.name, Toast.LENGTH_LONG).show()
            onVerifiedUser(user)
        }
        else {
            loginButton.setOnClickListener {
                onLoginUser(it)
            }
        }
    }

    private fun onLoginUser(view: View) {
        if (validationLogin()) {
            user = User(
                1,
                nameInput.text.toString(),
                passwordInput.text.toString()
            )
            onVerifiedUser(user)
        }
    }

    private fun validationLogin(): Boolean {
        if (nameInput.text.toString().trim().isEmpty()) {
            nameInput.error = "Please Enter your name !!!"
            return false
        } else if (passwordInput.text.toString().trim().isEmpty()) {
            passwordInput.error = "Please Enter your password !!!"
            return false
        }
        return true
    }

    private fun onVerifiedUser(user: User) {
        val mainIntent = Intent(applicationContext, MainActivity::class.java)
        mainIntent.putExtra(REMEMBER_ME, rememberMe.isChecked)
        mainIntent.putExtra(ID, user.id)
        mainIntent.putExtra(USERNAME, user.name)
        mainIntent.putExtra(PASSWORD, user.password)
        startActivity(mainIntent)
        finish()
    }

}
