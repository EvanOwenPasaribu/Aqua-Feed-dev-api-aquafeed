package com.aqua_feed.app.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.aqua_feed.app.databinding.ActivityLoginBinding
import com.aqua_feed.app.utils.PreferencesUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLoginBinding
    private val preferencesUtils by lazy { PreferencesUtils(applicationContext) }
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        if (preferencesUtils.getEmail().isNotEmpty()) {
            startActivity(
                Intent(this, HomeActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            )
        }
        binding.btnLogin.setOnClickListener {
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()
            val isRemember = binding.checkRemember.isChecked
            when {
                email.isEmpty() -> binding.edtEmail.error = "Must be filled"
                !email.contains("@") -> binding.edtEmail.error = "Email invalid"
                !email.contains(".") -> binding.edtEmail.error = "Email invalid"
                password.isEmpty() -> binding.edtPassword.error = "Must be filled"
                password.length < 6 -> binding.edtPassword.error = "Must be greater than 6 characters"
                else -> {
                    loginUser(email, password, isRemember)
                }
            }
        }

        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
    private fun loginUser(email: String, password: String, isRemember: Boolean) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user: FirebaseUser? = auth.currentUser
                    if (user != null) {
                        if (isRemember) {
                            preferencesUtils.setUserLogin(email)
                        }
                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    }
                } else {
                    Toast.makeText(
                        this,
                        "Authentication failed. Check your email and password.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}