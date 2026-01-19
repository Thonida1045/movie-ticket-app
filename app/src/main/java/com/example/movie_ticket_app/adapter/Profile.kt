package com.example.movie_ticket_app.adapter

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.movie_ticket_app.R
import com.example.movie_ticket_app.activity.HistoryActivity
import com.example.movie_ticket_app.activity.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class Profile : Fragment(R.layout.activity_profile) {

    private lateinit var auth: FirebaseAuth
    private var tvUserName: TextView? = null
    private var btnLogout: View? = null
    private var btnMyTickets: View? = null
    private var btnSettings: View? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Initialize views manually
        tvUserName = view.findViewById(R.id.tvUserName)
        btnLogout = view.findViewById(R.id.btnLogout)
        btnMyTickets = view.findViewById(R.id.btnMyTickets)
        btnSettings = view.findViewById(R.id.btnSettings)

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        // 1. Check Authentication Status
        if (currentUser == null) {
            // If not logged in, send to LoginActivity
            navigateToLogin()
        } else {
            // 2. Load User Data
            displayUserData(currentUser)
        }

        // 3. Setup Logout
        btnLogout?.setOnClickListener {
            auth.signOut()
            navigateToLogin()
        }

        // 4. Setup My Tickets / Booking History
        btnMyTickets?.setOnClickListener {
            if (auth.currentUser != null) {
                val intent = Intent(activity, HistoryActivity::class.java)
                startActivity(intent)
            } else {
                navigateToLogin()
            }
        }

        // 5. Setup Settings (placeholder)
        btnSettings?.setOnClickListener {
            // TODO: Implement account settings
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh user data when returning to the fragment
        if (!::auth.isInitialized) return
        
        val currentUser = auth.currentUser
        if (currentUser == null) {
            navigateToLogin()
        } else {
            displayUserData(currentUser)
        }
    }

    private fun displayUserData(user: FirebaseUser) {
        // Display user's email if display name is not set
        val displayName = user.displayName
        tvUserName?.text = if (!displayName.isNullOrEmpty()) displayName else user.email ?: "User"
    }

    private fun navigateToLogin() {
        val intent = Intent(activity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        activity?.finish()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        tvUserName = null
        btnLogout = null
        btnMyTickets = null
        btnSettings = null
    }
}