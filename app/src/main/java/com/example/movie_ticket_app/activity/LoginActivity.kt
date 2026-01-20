package com.example.movie_ticket_app.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import com.example.movie_ticket_app.R
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var email: EditText
    private lateinit var pass: EditText
    private lateinit var auth: FirebaseAuth
    private lateinit var progressBar: ProgressBar
    private lateinit var btnLogin: Button
    private lateinit var btnGoogleSignIn: Button
    private lateinit var credentialManager: CredentialManager

    // Pending booking data from SeatListActivity
    private var hasPendingBooking = false
    private var pendingTitle: String? = null
    private var pendingPoster: String? = null
    private var pendingSeat: String? = null
    private var pendingDate: String? = null
    private var pendingTime: String? = null
    private var pendingPrice: Double = 0.0

    companion object {
        private const val TAG = "LoginActivity"
        private const val WEB_CLIENT_ID = "703364450734-3cq3m46js3jcr3uonbst5een40dffrf9.apps.googleusercontent.com"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()
        credentialManager = CredentialManager.create(this)

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
        btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn)

        // Back button
        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // Email/Password login
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

        // Google Sign-In
        btnGoogleSignIn.setOnClickListener {
            signInWithGoogle()
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

    private fun signInWithGoogle() {
        setLoading(true)
        
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(WEB_CLIENT_ID)
            .setAutoSelectEnabled(true)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {
            try {
                val result = credentialManager.getCredential(
                    request = request,
                    context = this@LoginActivity
                )
                handleGoogleSignInResult(result)
            } catch (e: GetCredentialException) {
                setLoading(false)
                Log.e(TAG, "Google Sign-In failed", e)
                Toast.makeText(
                    this@LoginActivity,
                    "Google Sign-In failed: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun handleGoogleSignInResult(result: GetCredentialResponse) {
        val credential = result.credential
        
        when (credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        firebaseAuthWithGoogle(googleIdTokenCredential.idToken)
                    } catch (e: GoogleIdTokenParsingException) {
                        setLoading(false)
                        Log.e(TAG, "Invalid Google ID token", e)
                        Toast.makeText(this, "Invalid Google credentials", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    setLoading(false)
                    Log.e(TAG, "Unexpected credential type")
                }
            }
            else -> {
                setLoading(false)
                Log.e(TAG, "Unexpected credential type")
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnSuccessListener { authResult ->
                setLoading(false)
                val user = authResult.user
                val isNewUser = authResult.additionalUserInfo?.isNewUser == true
                
                if (isNewUser) {
                    Toast.makeText(this, "Welcome, ${user?.displayName}!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Welcome back, ${user?.displayName}!", Toast.LENGTH_SHORT).show()
                }
                
                handleSuccessfulLogin()
            }
            .addOnFailureListener { e ->
                setLoading(false)
                Log.e(TAG, "Firebase auth with Google failed", e)
                Toast.makeText(this, "Authentication failed: ${e.message}", Toast.LENGTH_LONG).show()
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
        btnGoogleSignIn.isEnabled = !show
    }
}
