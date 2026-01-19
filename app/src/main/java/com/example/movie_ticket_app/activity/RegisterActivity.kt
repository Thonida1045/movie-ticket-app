package com.example.movie_ticket_app.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.movie_ticket_app.R
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {
    private lateinit var email: EditText
    private lateinit var pass: EditText
    private lateinit var auth: FirebaseAuth
    private lateinit var progressBar: ProgressBar
    private lateinit var btnRegister: Button

    // Pending booking data
    private var hasPendingBooking = false
    private var pendingTitle: String? = null
    private var pendingPoster: String? = null
    private var pendingSeat: String? = null
    private var pendingDate: String? = null
    private var pendingTime: String? = null
    private var pendingPrice: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        // Get pending booking data if available
        pendingTitle = intent.getStringExtra("pendingTitle")
        pendingPoster = intent.getStringExtra("pendingPoster")
        pendingSeat = intent.getStringExtra("pendingSeat")
        pendingDate = intent.getStringExtra("pendingDate")
        pendingTime = intent.getStringExtra("pendingTime")
        pendingPrice = intent.getDoubleExtra("pendingPrice", 0.0)
        hasPendingBooking = !pendingTitle.isNullOrEmpty()

        email = findViewById(R.id.edtEmail)
        pass = findViewById(R.id.edtPassword)
        progressBar = findViewById(R.id.progressBar)
        btnRegister = findViewById(R.id.btnRegister)

        // Back button (both the image and text link)
        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        findViewById<TextView>(R.id.btnGoBack).setOnClickListener {
            finish()
        }

        btnRegister.setOnClickListener {
            val e = email.text.toString().trim()
            val p = pass.text.toString().trim()
            if (e.isEmpty()) {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (p.length < 6) {
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            setLoading(true)
            auth.createUserWithEmailAndPassword(e, p)
                .addOnSuccessListener {
                    setLoading(false)
                    Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show()
                    handleSuccessfulRegistration()
                }
                .addOnFailureListener {
                    setLoading(false)
                    Toast.makeText(this, "Registration failed: ${it.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun handleSuccessfulRegistration() {
        if (hasPendingBooking) {
            // Go back through to SeatListActivity to complete booking
            setResult(RESULT_OK)
            finish()
        } else {
            // Normal registration - go to MainActivity
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun setLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        btnRegister.text = if (show) "" else "Create Account"
        btnRegister.isEnabled = !show
    }
}