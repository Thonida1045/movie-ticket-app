package com.example.movie_ticket_app.model

data class Seat(var status:SeatStatus,var name: String){
    enum class SeatStatus{
        AVAILABLE,
        SELECTED,
        UNAVAILABLE,
    }
}

