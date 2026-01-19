package com.example.movie_ticket_app.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.movie_ticket_app.R
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var email: EditText
    private lateinit var pass: EditText
    private lateinit var auth: FirebaseAuth
    private lateinit var progressBar: ProgressBar
    private lateinit var btnLogin: Button

    // Pending booking data from SeatListActivity
    private var hasPendingBooking = false
    private var pendingTitle: String? = null
    private var pendingPoster: String? = null
    private var pendingSeat: String? = null
    private var pendingDate: String? = null
    private var pendingTime: String? = null
    private var pendingPrice: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        // Get pending booking data if coming from SeatListActivity
        pendingTitle = intent.getStringExtra("pendingTitle")
        pendingPoster = intent.getStringExtra("pendingPoster")
        pendingSeat = intent.getStringExtra("pendingSeat")
        pendingDate = intent.getStringExtra("pendingDate")
        pendingTime = intent.getStringExtra("pendingTime")
        pendingPrice = intent.getDoubleExtra("pendingPrice", 0.0)
        hasPendingBooking = !pendingTitle.isNullOrEmpty()

        // Check if user is already logged in
        if (auth.currentUser != null) {
            handleSuccessfulLogin()
            return
        }

        email = findViewById(R.id.edtEmail)
        pass = findViewById(R.id.edtPassword)
        progressBar = findViewById(R.id.progressBar)
        btnLogin = findViewById(R.id.btnLogin)

        // Back button
        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        btnLogin.setOnClickListener {
            val e = email.text.toString().trim()
            val p = pass.text.toString().trim()
            if (e.isEmpty() || p.isEmpty()) {
                Toast.makeText(this, "Enter email & password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            
            setLoading(true)
            auth.signInWithEmailAndPassword(e, p)
                .addOnSuccessListener {
                    setLoading(false)
                    Toast.makeText(this, "Welcome back!", Toast.LENGTH_SHORT).show()
                    handleSuccessfulLogin()
                }
                .addOnFailureListener {
                    setLoading(false)
                    Toast.makeText(this, "Login failed: ${it.message}", Toast.LENGTH_LONG).show()
                }
        }

        findViewById<Button>(R.id.btnGoRegister).setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            // Pass pending booking data to register activity too
            if (hasPendingBooking) {
                intent.putExtra("pendingTitle", pendingTitle)
                intent.putExtra("pendingPoster", pendingPoster)
                intent.putExtra("pendingSeat", pendingSeat)
                intent.putExtra("pendingDate", pendingDate)
                intent.putExtra("pendingTime", pendingTime)
                intent.putExtra("pendingPrice", pendingPrice)
            }
            startActivity(intent)
        }
    }

    private fun handleSuccessfulLogin() {
        if (hasPendingBooking) {
            // Go back to SeatListActivity to complete booking
            // The SeatListActivity should handle the actual booking
            setResult(RESULT_OK)
            finish()
        } else {
            // Normal login - go to MainActivity
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun setLoading(show: Boolean) {
        progressBar.visibility = if (show) View.VISIBLE else View.GONE
        btnLogin.text = if (show) "" else "Sign In"
        btnLogin.isEnabled = !show
    }
}
