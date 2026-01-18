package com.example.movie_ticket_app.activity

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.movie_ticket_app.R
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {
        private lateinit var email: EditText
        private lateinit var pass: EditText
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_register)

            email = findViewById(R.id.edtEmail)
            pass = findViewById(R.id.edtPassword)
            findViewById<Button>(R.id.btnRegister).setOnClickListener {
                val e = email.text.toString().trim()
                val p = pass.text.toString().trim()
                if (e.isEmpty() || p.length < 6) {
                    Toast.makeText(this, "Enter valid email & 6+ char password", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(e, p)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Account created!", Toast.LENGTH_SHORT).show()
                        finish() // go back to Login or previous
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Register failed: ${it.message}", Toast.LENGTH_LONG).show()
                    }
            }
        }
    }