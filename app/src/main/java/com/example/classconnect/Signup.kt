package com.example.classconnect

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class Signup : AppCompatActivity() {

    private lateinit var fullName: TextInputEditText
    private lateinit var regNumber: TextInputEditText
    private lateinit var section: TextInputEditText
    private lateinit var email: TextInputEditText
    private lateinit var password: TextInputEditText
    private lateinit var btnRegister: MaterialButton
    private lateinit var tvLoginLink: TextView
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signup)

        auth = FirebaseAuth.getInstance()

        fullName = findViewById(R.id.etFullName)
        regNumber = findViewById(R.id.etRegNumber)
        section = findViewById(R.id.etSection)
        email = findViewById(R.id.etEmail)
        password = findViewById(R.id.etPassword)
        btnRegister = findViewById(R.id.btnRegister)
        tvLoginLink = findViewById(R.id.tvLoginLink)

        btnRegister.setOnClickListener {
            val e = email.text.toString().trim()
            val p = password.text.toString().trim()
            val name = fullName.text.toString().trim()

            when {
                name.isEmpty() -> {
                    fullName.error = "Full name is required"
                    fullName.requestFocus()
                }
                e.isEmpty() -> {
                    email.error = "Email is required"
                    email.requestFocus()
                }
                p.isEmpty() -> {
                    password.error = "Password is required"
                    password.requestFocus()
                }
                p.length < 6 -> {
                    password.error = "Password must be at least 6 characters"
                    password.requestFocus()
                }
                else -> registerUser(e, p, name)
            }
        }

        tvLoginLink.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
            finish()
        }
    }

    private fun registerUser(e: String, p: String, name: String) {
        btnRegister.isEnabled = false
        btnRegister.text = getString(R.string.btn_creating_account)

        auth.createUserWithEmailAndPassword(e, p)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser

                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build()

                    user?.updateProfile(profileUpdates)

                    user?.sendEmailVerification()
                        ?.addOnSuccessListener {
                            btnRegister.isEnabled = true
                            btnRegister.text = getString(R.string.btn_create_account)
                            Toast.makeText(
                                this,
                                "Account created! Please verify your email to login.",
                                Toast.LENGTH_LONG
                            ).show()
                            startActivity(Intent(this, Login::class.java))
                            finish()
                        }
                        ?.addOnFailureListener { err ->
                            btnRegister.isEnabled = true
                            btnRegister.text = getString(R.string.btn_create_account)
                            Log.e("Signup", "Verification email failed", err)
                            Toast.makeText(this, "Registered, but failed to send verification email.", Toast.LENGTH_LONG).show()
                        }
                } else {
                    btnRegister.isEnabled = true
                    btnRegister.text = getString(R.string.btn_create_account)
                    Toast.makeText(
                        this,
                        "Registration failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}