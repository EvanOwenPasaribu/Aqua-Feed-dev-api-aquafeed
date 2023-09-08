package com.aqua_feed.app.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.aqua_feed.app.databinding.ActivityRegisterBinding
import com.aqua_feed.app.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.security.MessageDigest

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.btnBack.setOnClickListener { finish() }
        binding.btnRegister.setOnClickListener { registerUser() }
    }

    private fun registerUser() {
        val name = binding.edtName.text.toString().trim()
        val phone = binding.edtPhone.text.toString().trim()
        val email = binding.edtEmail.text.toString().trim()
        val password = binding.edtPassword.text.toString().trim()
        val confirmPassword = binding.edtConfirmPassword.text.toString().trim()

        if (name.isEmpty()) {
            binding.edtName.error = "Must be filled"
            return
        }

        when {
            name.isEmpty() -> binding.edtName.error = "Must be filled"
            phone.isEmpty() -> binding.edtPhone.error = "Must be filled"
            !phone.startsWith("08") -> binding.edtPhone.error = "Must be started with 08"
            email.isEmpty() -> binding.edtEmail.error = "Must be filled"
            !email.contains("@") -> binding.edtEmail.error = "Email invalid"
            !email.contains(".") -> binding.edtEmail.error = "Email invalid"
            password.isEmpty() -> binding.edtPassword.error = "Must be filled"
            password.length < 6 -> binding.edtPassword.error = "Must be greater than 6 characters"
            confirmPassword.isEmpty() -> binding.edtConfirmPassword.error = "Must be filled"
            confirmPassword != password -> binding.edtConfirmPassword.error =
                "Confirm password not match with password"
        }
        val hashedPassword = hashPassword(password)

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = User(auth.currentUser?.uid ?: "", name, phone, email, hashedPassword)
                    uploadUserDataToFirebase(user)
                    finish()
                } else {
                    Toast.makeText(this, "Registration failed. Please try again.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun uploadUserDataToFirebase(user: User) {
        val database = FirebaseDatabase.getInstance()
        val usersRef: DatabaseReference = database.getReference("UserAquaFeed")
        val userId = usersRef.push().key

        if (userId != null) {
            usersRef.child(userId).setValue(user)
                .addOnSuccessListener {
                    startActivity(Intent(this, LoginActivity::class.java))
                }
                .addOnFailureListener {}
        }
    }

    private fun hashPassword(password: String): String {
        try {
            val messageDigest = MessageDigest.getInstance("SHA-256")
            val passwordBytes = password.toByteArray()
            val hashedPasswordBytes = messageDigest.digest(passwordBytes)
            return hashedPasswordBytes.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            Log.e("HashPassword", "Error hashing password: ${e.message}")
        }
        return ""
    }
}