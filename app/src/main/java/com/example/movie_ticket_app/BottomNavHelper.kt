package com.example.movie_ticket_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import com.example.movie_ticket_app.activity.FavoritesActivity
import com.example.movie_ticket_app.activity.HistoryActivity
import com.example.movie_ticket_app.activity.MainActivity
import com.example.movie_ticket_app.activity.ProfileActivity
import com.ismaeldivita.chipnavigation.ChipNavigationBar

class BottomNavHelper {

    fun AppCompatActivity.setupBottomNav(chipNav: ChipNavigationBar) {
        chipNav.setOnItemSelectedListener { itemId ->
            when (itemId) {
                R.id.explorer -> {
                    if (this !is MainActivity) {
                        startActivity(Intent(this, MainActivity::class.java))
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                        finish()
                    }
                }
                R.id.favorites -> {
                    if (this !is FavoritesActivity) {
                        startActivity(Intent(this, FavoritesActivity::class.java))
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                        finish()
                    }
                }
                R.id.cart -> {
                    if (this !is HistoryActivity) {
                        startActivity(Intent(this, HistoryActivity::class.java))
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                        finish()
                    }
                }
                R.id.profile -> {
                    if (this !is ProfileActivity) {
                        startActivity(Intent(this, ProfileActivity::class.java))
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                        finish()
                    }
                }
            }
        }
    }
}