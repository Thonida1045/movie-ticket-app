package com.example.movie_ticket_app.activity

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.movie_ticket_app.R
import com.example.movie_ticket_app.adapter.HistoryAdapter
import com.example.movie_ticket_app.model.Booking
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase

class HistoryActivity : AppCompatActivity() {
        private lateinit var rv: RecyclerView
        private val adapter = HistoryAdapter()

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_history)
            rv = findViewById(R.id.rvHistory)
            rv.layoutManager = LinearLayoutManager(this)
            rv.adapter = adapter

            val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (uid == null) {
                Toast.makeText(this, "Please login first.", Toast.LENGTH_SHORT).show()
                finish()
                return
            }

            val ref = FirebaseDatabase.getInstance().reference
                .child("users").child(uid).child("history")

            // Listen in real-time and sort by createdAt DESC
            ref.orderByChild("createdAt").addValueEventListener(object : ValueEventListener {
                @Override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<Booking>()
                    snapshot.children.forEach { child ->
                        child.getValue(Booking::class.java)?.let { list.add(it) }
                    }
                    list.sortByDescending { it.createdAt ?: 0L }
                    adapter.submit(list)
                }

                @Override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        this@HistoryActivity,
                        "Load failed: ${error.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
        }
    }
