package com.example.movie_ticket_app.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.movie_ticket_app.R
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
        private lateinit var email: EditText
        private lateinit var pass: EditText
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_login)

            email = findViewById(R.id.edtEmail)
            pass = findViewById(R.id.edtPassword)

            findViewById<Button>(R.id.btnLogin).setOnClickListener {
                val e = email.text.toString().trim()
                val p = pass.text.toString().trim()
                if (e.isEmpty() || p.isEmpty()) {
                    Toast.makeText(this, "Enter email & password", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                FirebaseAuth.getInstance().signInWithEmailAndPassword(e, p)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Welcome!", Toast.LENGTH_SHORT).show()
                        finish() // go back – SeatListActivity will proceed or user will tap Book again
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Login failed: ${it.message}", Toast.LENGTH_LONG)
                            .show()
                    }
            }

            findViewById<Button>(R.id.btnGoRegister).setOnClickListener {
                startActivity(Intent(this, RegisterActivity::class.java))
            }
        }
    }
