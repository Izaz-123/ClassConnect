package com.example.classconnect

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class Signup: AppCompatActivity() {

    lateinit var fullName: EditText
    lateinit var regNumber: EditText
    lateinit var section: EditText
    lateinit var email: EditText
    lateinit var password: EditText
    lateinit var btnRegister: Button
    lateinit var tvLoginLink: TextView
    lateinit var auth: FirebaseAuth

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

            if (e.isNotEmpty() && p.isNotEmpty() && name.isNotEmpty()) {
                registerUser(e, p)
            } else {
                Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show()
            }
        }

        tvLoginLink.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
            finish()
        }
    }

    private fun registerUser(e: String, p: String) {
        auth.createUserWithEmailAndPassword(e, p)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.sendEmailVerification()
                        ?.addOnSuccessListener {
                            Toast.makeText(
                                this,
                                "Registration successful! Please verify your email.",
                                Toast.LENGTH_LONG
                            ).show()

                            //go to login page
                            startActivity(Intent(this, Login::class.java))
                            finish()
                        }
                        ?.addOnFailureListener { err ->
                            Log.e("Signup", "Verification email failed", err)
                            Toast.makeText(this, "Error: ${err.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Log.e("Signup", "Registration failed", task.exception)
                    Toast.makeText(
                        this,
                        "Registration failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}