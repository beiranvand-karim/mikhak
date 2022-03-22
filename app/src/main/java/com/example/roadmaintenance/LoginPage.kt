package com.example.roadmaintenance

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.example.roadmaintenance.models.User

class LoginPage : AppCompatActivity() {

    private lateinit var user: User
    private var nameInput: String? = null
    private var passwordInput: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)
        supportActionBar?.hide()
    }

    fun onLoginUser(view: View) {
        findViewById<EditText>(R.id.username_field).also { nameInput = it.text.toString() }
        findViewById<EditText>(R.id.password_field).also { passwordInput = it.text.toString() }
        user = User(nameInput.toString(), passwordInput.toString())
        verifyUser(user)
    }

    fun verifyUser(user: User) {
        val mapIntent = Intent(applicationContext, MainActivity::class.java)
        mapIntent.putExtra("name",user.name)
        mapIntent.putExtra("password",user.password)
        startActivity(mapIntent)
    }

}