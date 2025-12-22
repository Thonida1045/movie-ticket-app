package com.example.movie_ticket_app.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movie_ticket_app.R
import com.example.movie_ticket_app.adapter.DateAdapter
import com.example.movie_ticket_app.databinding.ActivitySeatListBinding
import com.example.movie_ticket_app.model.Film
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class SeatListActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySeatListBinding
    private lateinit var film: Film
    private var price: Double = 0.0
    private var number: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySeatListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getIntentExtra()
        setVaraible()
        initTimeDateList()

    }

    private fun initTimeDateList() {
    binding.apply{
        dateRecyclerview.layoutManager=
            LinearLayoutManager(this@SeatListActivity, LinearLayoutManager.HORIZONTAL,false)
            dateRecyclerview.adapter= DateAdapter(generateDates())
    }
    }

    private fun setVaraible() {
        binding.backBtn.setOnClickListener {
            finish()
        }
    }

        private fun getIntentExtra() {
            film = intent.getSerializableExtra("film") as Film
        }

    private fun generateDates(): List<String>{
        val dates = mutableListOf<String>()
        val today = LocalDate.now()
        val formatter= DateTimeFormatter.ofPattern("EEE/dd/MMM")

        for(i in 0 until 7){
            dates.add(today.plusDays(i.toLong()).format(formatter))
        }
        return dates
    }
}