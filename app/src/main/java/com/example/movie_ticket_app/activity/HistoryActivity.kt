
package com.example.movie_ticket_app.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.movie_ticket_app.R
import com.example.movie_ticket_app.adapter.HistoryAdapter
import com.example.movie_ticket_app.model.Booking
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HistoryActivity : AppCompatActivity() {
    private lateinit var rv: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyState: LinearLayout
    private val adapter = HistoryAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        rv = findViewById(R.id.rvHistory)
        progressBar = findViewById(R.id.progressBar)
        emptyState = findViewById(R.id.emptyState)
        
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = adapter

        // Back button
        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // Browse movies button (in empty state)
        findViewById<Button>(R.id.btnBrowseMovies).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            Toast.makeText(this, "Please login first.", Toast.LENGTH_SHORT).show()
            // Navigate to login
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        loadBookingHistory(uid)
    }

    private fun loadBookingHistory(uid: String) {
        showLoading()
        
        val ref = FirebaseDatabase.getInstance().reference
            .child("users")
            .child(uid)
            .child("history")

        ref.orderByChild("createdAt").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Booking>()
                snapshot.children.forEach { child ->
                    child.getValue(Booking::class.java)?.let { list.add(it) }
                }

                list.sortByDescending { it.createdAt ?: 0L }
                
                if (list.isEmpty()) {
                    showEmptyState()
                } else {
                    showContent()
                    adapter.submit(list)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                showEmptyState()
                Toast.makeText(
                    this@HistoryActivity,
                    "Load failed: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
        rv.visibility = View.GONE
        emptyState.visibility = View.GONE
    }

    private fun showContent() {
        progressBar.visibility = View.GONE
        rv.visibility = View.VISIBLE
        emptyState.visibility = View.GONE
    }

    private fun showEmptyState() {
        progressBar.visibility = View.GONE
        rv.visibility = View.GONE
        emptyState.visibility = View.VISIBLE
    }
}
