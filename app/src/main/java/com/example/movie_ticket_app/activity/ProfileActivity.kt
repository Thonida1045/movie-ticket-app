package com.example.movie_ticket_app.activity

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.movie_ticket_app.BottomNavHelper
import com.example.movie_ticket_app.R
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class ProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser

        // Check Authentication Status
        if (currentUser == null) {
            navigateToLogin()
            return
        }

        // Load User Data
        displayUserData(currentUser)

        // Setup Logout
        findViewById<android.view.View>(R.id.btnLogout)?.setOnClickListener {
            auth.signOut()
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
            navigateToLogin()
        }

        // Setup My Tickets / Booking History
        findViewById<android.view.View>(R.id.btnMyTickets)?.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }

        // Setup Settings (placeholder)
        findViewById<android.view.View>(R.id.btnSettings)?.setOnClickListener {
            Toast.makeText(this, "Settings coming soon", Toast.LENGTH_SHORT).show()
        }

        // Setup bottom navigation
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        val chipNav = findViewById<ChipNavigationBar>(R.id.chipNav)
        chipNav?.let {
            it.setItemSelected(R.id.profile, true)
            with(BottomNavHelper()) {
                this@ProfileActivity.setupBottomNav(it)
            }
        }
    }

    private fun displayUserData(user: FirebaseUser) {
        val displayName = user.displayName
        val email = user.email
        val photoUrl = user.photoUrl
        
        // Set user name
        val tvUserName = findViewById<TextView>(R.id.tvUserName)
        tvUserName?.text = when {
            !displayName.isNullOrEmpty() -> displayName
            !email.isNullOrEmpty() -> email.substringBefore("@")
            else -> "User"
        }
        
        // Set user email
        val tvUserEmail = findViewById<TextView>(R.id.tvUserEmail)
        tvUserEmail?.text = email ?: ""
        
        // Load profile photo from Google account
        val profileImage = findViewById<ImageView>(R.id.profileImage)
        if (photoUrl != null && profileImage != null) {
            Glide.with(this)
                .load(photoUrl)
                .circleCrop()
                .placeholder(R.drawable.ic_movie_ticket)
                .error(R.drawable.ic_movie_ticket)
                .into(profileImage)
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}
