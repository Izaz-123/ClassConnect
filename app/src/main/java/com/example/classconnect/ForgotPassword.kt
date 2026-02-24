package com.example.classconnect

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class ForgotPassword : AppCompatActivity() {

    private lateinit var etEmail: TextInputEditText
    private lateinit var btnSendReset: MaterialButton
    private lateinit var btnBack: ImageButton
    private lateinit var tvLoginLink: TextView
    private lateinit var layoutSuccess: LinearLayout
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_forgot_password)

        auth = FirebaseAuth.getInstance()

        etEmail = findViewById(R.id.etEmail)
        btnSendReset = findViewById(R.id.btnSendReset)
        btnBack = findViewById(R.id.btnBack)
        tvLoginLink = findViewById(R.id.tvLoginLink)
        layoutSuccess = findViewById(R.id.layoutSuccess)

        // Pre-fill email if passed via intent
        val prefillEmail = intent.getStringExtra("email")
        if (!prefillEmail.isNullOrBlank()) {
            etEmail.setText(prefillEmail)
        }

        btnSendReset.setOnClickListener {
            sendResetLink()
        }

        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        tvLoginLink.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
            finish()
        }
    }

    private fun sendResetLink() {
        val email = etEmail.text.toString().trim()

        if (email.isEmpty()) {
            etEmail.error = "Please enter your email address"
            etEmail.requestFocus()
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Please enter a valid email address"
            etEmail.requestFocus()
            return
        }

        btnSendReset.isEnabled = false
        btnSendReset.text = getString(R.string.btn_sending)

        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                btnSendReset.isEnabled = true
                btnSendReset.text = getString(R.string.btn_send_reset)
                layoutSuccess.visibility = View.VISIBLE
                Toast.makeText(
                    this,
                    "Password reset link sent to $email",
                    Toast.LENGTH_LONG
                ).show()
            }
            .addOnFailureListener { e ->
                btnSendReset.isEnabled = true
                btnSendReset.text = getString(R.string.btn_send_reset)
                layoutSuccess.visibility = View.GONE
                Toast.makeText(
                    this,
                    "Error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}

