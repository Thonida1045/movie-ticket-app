package com.example.movie_ticket_app.activity

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.movie_ticket_app.R
import com.example.movie_ticket_app.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Check if user is already logged in and navigate to MainActivity
        // In this case, we skip setting up click listeners since they're not needed
        if (auth.currentUser != null) {
            navigateToMain()
            return
        }

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.loginBtn.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if (validateInput(email, password)) {
                loginUser(email, password)
            }
        }

        binding.signupText.setOnClickListener {
            val email = binding.emailEditText.text.toString().trim()
            val password = binding.passwordEditText.text.toString().trim()

            if (validateInput(email, password)) {
                signupUser(email, password)
            }
        }

        binding.skipText.setOnClickListener {
            navigateToMain()
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            binding.emailInputLayout.error = getString(R.string.please_enter_email)
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailInputLayout.error = getString(R.string.invalid_email)
            return false
        }

        binding.emailInputLayout.error = null

        if (password.isEmpty()) {
            binding.passwordInputLayout.error = getString(R.string.please_enter_password)
            return false
        }

        if (password.length < 6) {
            binding.passwordInputLayout.error = getString(R.string.password_min_length)
            return false
        }

        binding.passwordInputLayout.error = null
        return true
    }

    private fun loginUser(email: String, password: String) {
        showLoading(true)

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                showLoading(false)
                if (task.isSuccessful) {
                    Toast.makeText(
                        this,
                        getString(R.string.login_successful),
                        Toast.LENGTH_SHORT
                    ).show()
                    navigateToMain()
                } else {
                    val errorMessage = when ((task.exception as? FirebaseAuthException)?.errorCode) {
                        "ERROR_INVALID_CREDENTIAL" -> getString(R.string.login_error_invalid_credential)
                        "ERROR_USER_NOT_FOUND" -> getString(R.string.login_error_user_not_found)
                        "ERROR_WRONG_PASSWORD" -> getString(R.string.login_error_wrong_password)
                        else -> getString(R.string.login_failed)
                    }
                    Toast.makeText(
                        this,
                        errorMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun signupUser(email: String, password: String) {
        showLoading(true)

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                showLoading(false)
                if (task.isSuccessful) {
                    Toast.makeText(
                        this,
                        getString(R.string.account_created),
                        Toast.LENGTH_SHORT
                    ).show()
                    navigateToMain()
                } else {
                    val errorMessage = when ((task.exception as? FirebaseAuthException)?.errorCode) {
                        "ERROR_INVALID_EMAIL" -> getString(R.string.signup_error_invalid_email)
                        "ERROR_WEAK_PASSWORD" -> getString(R.string.signup_error_weak_password)
                        "ERROR_EMAIL_ALREADY_IN_USE" -> getString(R.string.signup_error_email_in_use)
                        else -> getString(R.string.signup_error_generic)
                    }
                    Toast.makeText(
                        this,
                        errorMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.loginBtn.isEnabled = !isLoading
        binding.signupText.isEnabled = !isLoading
        binding.skipText.isEnabled = !isLoading
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
