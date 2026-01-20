package com.example.movie_ticket_app.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.movie_ticket_app.BottomNavHelper
import com.example.movie_ticket_app.R
import com.example.movie_ticket_app.adapter.FilmListAdapter
import com.example.movie_ticket_app.model.Film
import com.ismaeldivita.chipnavigation.ChipNavigationBar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FavoritesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyState: View
    private lateinit var progressBar: View
    private val favoriteFilms = ArrayList<Film>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        recyclerView = findViewById(R.id.rvFavorites)
        emptyState = findViewById(R.id.emptyState)
        progressBar = findViewById(R.id.progressBar)

        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        loadFavorites()
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        val chipNav = findViewById<ChipNavigationBar>(R.id.chipNav)
        chipNav?.let {
            it.setItemSelected(R.id.favorites, true)
            with(BottomNavHelper()) {
                this@FavoritesActivity.setupBottomNav(it)
            }
        }
    }

    private fun loadFavorites() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            showEmptyState()
            return
        }

        showLoading()

        val ref = FirebaseDatabase.getInstance().reference
            .child("users")
            .child(uid)
            .child("favorites")

        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                favoriteFilms.clear()
                snapshot.children.forEach { child ->
                    child.getValue(Film::class.java)?.let { favoriteFilms.add(it) }
                }

                if (favoriteFilms.isEmpty()) {
                    showEmptyState()
                } else {
                    showContent()
                    recyclerView.adapter = FilmListAdapter(favoriteFilms)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                showEmptyState()
            }
        })
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.GONE
        emptyState.visibility = View.GONE
    }

    private fun showContent() {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.VISIBLE
        emptyState.visibility = View.GONE
    }

    private fun showEmptyState() {
        progressBar.visibility = View.GONE
        recyclerView.visibility = View.GONE
        emptyState.visibility = View.VISIBLE
    }
}
