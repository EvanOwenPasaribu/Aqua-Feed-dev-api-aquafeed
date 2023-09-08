package com.aqua_feed.app.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.aqua_feed.app.databinding.ActivityVerifyOtpBinding

class VerifyOtpActivity : AppCompatActivity() {
    private lateinit var binding : ActivityVerifyOtpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifyOtpBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.btnVerify.setOnClickListener {
            val otpCode = binding.edtCode.text.toString()
            when {
                otpCode.isEmpty() -> binding.edtCode.error = "Must be filled"
                otpCode.length != 6 -> binding.edtCode.error = "OTP Code only contains 6 characters"
                else -> {
                    startActivity(
                        Intent(this, LoginActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                    )
                }
            }
        }
    }
}