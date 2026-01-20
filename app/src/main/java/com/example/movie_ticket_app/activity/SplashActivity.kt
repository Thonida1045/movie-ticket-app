package com.example.movie_ticket_app.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.movie_ticket_app.databinding.ActivitySpashBinding


class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySpashBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySpashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startBtn.setOnClickListener{
            startActivity(Intent (this, MainActivity::class.java))
        }
    }
}