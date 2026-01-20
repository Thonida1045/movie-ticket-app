
package com.example.movie_ticket_app.activity

import android.content.Intent
import android.icu.text.DecimalFormat
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.movie_ticket_app.adapter.DateAdapter
import com.example.movie_ticket_app.adapter.SeatListAdapter
import com.example.movie_ticket_app.adapter.TimeAdapter
import com.example.movie_ticket_app.databinding.ActivitySeatListBinding
import com.example.movie_ticket_app.model.Film
import com.example.movie_ticket_app.model.Seat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class SeatListActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySeatListBinding
    private lateinit var film: Film

    private var price: Double = 0.0
    private var number: Int = 0

    // Track selections
    private var selectedDate: String? = null
    private var selectedTime: String? = null
    private var selectedSeatsLabel: String = ""

    // Activity result launcher for login
    private val loginLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            // User logged in successfully, proceed with booking
            if (FirebaseAuth.getInstance().currentUser != null) {
                createBookingAndGoToHistory()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySeatListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getIntentExtra()
        setVariable()
        initTimeDateList()
        initSeatsList()

        // Set up booking button click listener
        binding.bookingTicket.setOnClickListener {
            onBookTicketClicked()
        }
    }

    private fun setVariable() {
        binding.backBtn.setOnClickListener { finish() }
    }

    private fun getIntentExtra() {
        // Ensure your Film implements Serializable/Parcelable
        film = intent.getSerializableExtra("film") as Film
        // If needed, show film title/poster on this screen
        // binding.titleText.text = film.title
        // Glide.with(this).load(film.posterUrl).into(binding.posterImage)
    }

    private fun initTimeDateList() = binding.run {
        dateRecyclerview.layoutManager =
            LinearLayoutManager(this@SeatListActivity, LinearLayoutManager.HORIZONTAL, false)
        dateRecyclerview.adapter = DateAdapter(generateDates()) { picked ->
            selectedDate = picked
        }

        timeRecyclerview.layoutManager =
            LinearLayoutManager(this@SeatListActivity, LinearLayoutManager.HORIZONTAL, false)
        timeRecyclerview.adapter = TimeAdapter(generateTimeSlots()) { picked ->
            selectedTime = picked
        }
    }

    private fun initSeatsList() = binding.run {
        val gridLayoutManager = GridLayoutManager(this@SeatListActivity, 7)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int = 1
        }
        seatRecyclerview.layoutManager = gridLayoutManager

        // Build a simple 9x9 grid (81 seats); mark some as UNAVAILABLE
        val seatList = mutableListOf<Seat>()
        val numberSeats = 81
        for (i in 0 until numberSeats) {
            val seatName = "" // adapter can display row/col label
            val seatStatus =
                if (i == 2 || i == 20 || i == 33 || i == 41 || i == 50 || i == 72 || i == 73)
                    Seat.SeatStatus.UNAVAILABLE
                else Seat.SeatStatus.AVAILABLE

            seatList.add(Seat(seatStatus, seatName))
        }

        val seatAdapter = SeatListAdapter(
            seatList,
            this@SeatListActivity,
            object : SeatListAdapter.SelectedSeat {
                override fun Return(selecedName: String, num: Int) {
                    // Save selected seats label and update price
                    selectedSeatsLabel = selecedName
                    numberSelectedTxt.text = "$num Seat Selected"
                    val df = DecimalFormat("#.##")
                    price = df.format(num * film.price).toDouble() // TODO film.price field
                    number = num
                    priceTxt.text = "$$price"
                }
            }
        )
        seatRecyclerview.adapter = seatAdapter
        seatRecyclerview.isNestedScrollingEnabled = false
    }

    private fun generateDates(): List<String> {
        val dates = mutableListOf<String>()
        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("EEE/dd/MMM")
        for (i in 0 until 7) dates.add(today.plusDays(i.toLong()).format(formatter))
        return dates
    }

    private fun generateTimeSlots(): List<String> {
        val timeSlots = mutableListOf<String>()
        val formatter = DateTimeFormatter.ofPattern("hh:mm a")
        for (i in 0 until 24 step 2) {
            val time = LocalDate.now().atTime(i, 0)
            timeSlots.add(time.format(formatter))
        }
        return timeSlots
    }

    // ==========================
    // BOOKING + AUTH FLOW
    // ==========================
    private fun onBookTicketClicked() {
        val auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            // Not logged in → go to Login with pending booking data
            val intent = Intent(this, LoginActivity::class.java).apply {
                putExtra("pendingTitle", film.Title)
                putExtra("pendingPoster", film.Poster ?: "")
                putExtra("pendingSeat", selectedSeatsLabel)
                putExtra("pendingDate", selectedDate)
                putExtra("pendingTime", selectedTime)
                putExtra("pendingPrice", price)
            }
            loginLauncher.launch(intent)
            return
        }
        createBookingAndGoToHistory()
    }

    private fun createBookingAndGoToHistory() {
        // Validate minimal input
        if (selectedSeatsLabel.isBlank() || selectedDate.isNullOrBlank() || selectedTime.isNullOrBlank()) {
            Toast.makeText(this, "Please select seats, date & time.", Toast.LENGTH_SHORT).show()
            return
        }

        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val rootRef = FirebaseDatabase.getInstance().reference
        val bookingRef = rootRef.child("bookings").push()
        val bookingId = bookingRef.key!!

        // Build booking map (server timestamp is recommended)
        val bookingMap = hashMapOf<String, Any?>(
            "id" to bookingId,
            "userId" to uid,               // TODO adjust field name
            "movieTitle" to film.Title,           // TODO adjust
            "posterUrl" to (film.Poster ?: ""),// TODO adjust if null safe
            "cinema" to "Cinema A",
            "seat" to selectedSeatsLabel,
            "showDate" to selectedDate,
            "showTime" to selectedTime,
            "price" to price,
            "createdAt" to ServerValue.TIMESTAMP  // server time
        )

        // Atomic fan-out: write global + per-user history
        val updates = hashMapOf<String, Any?>(
            "/bookings/$bookingId" to bookingMap,
            "/users/$uid/history/$bookingId" to bookingMap
        )

        setLoading(true)
        rootRef.updateChildren(updates)
            .addOnSuccessListener {
                setLoading(false)
                startActivity(Intent(this, HistoryActivity::class.java))
            }
            .addOnFailureListener { e ->
                setLoading(false)
                Toast.makeText(this, "Booking failed: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun setLoading(show: Boolean) {
        binding.bookingTicket.isEnabled = !show
    }
}
