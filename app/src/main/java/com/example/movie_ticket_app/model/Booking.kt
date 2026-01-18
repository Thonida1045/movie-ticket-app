package com.example.movie_ticket_app.model

data class Booking (
        val id: String = "",            // bookingId (push key)
        val userId: String = "",
        val movieId: String = "",
        val movieTitle: String = "",
        val posterUrl: String = "",
        val cinema: String = "",
        val seat: String = "",
        val showDate: String = "",      // e.g., "2026-01-16"
        val showTime: String = "",      // e.g., "19:00"
        val price: Double = 0.0,
        val createdAt: Long? = null     // server timestamp filled by DB

)
