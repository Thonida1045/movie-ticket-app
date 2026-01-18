package com.example.movie_ticket_app.model

    import com.google.firebase.auth.FirebaseAuth
    import com.google.firebase.database.FirebaseDatabase
    import com.google.firebase.database.ServerValue
    import kotlinx.coroutines.tasks.await

    class BookingRepository(
        private val db: FirebaseDatabase = FirebaseDatabase.getInstance(),
        private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    ) {
        suspend fun bookTicket(
            movieId: String,
            movieTitle: String,
            posterUrl: String,
            cinema: String,
            seat: String,
            showDate: String,
            showTime: String,
            price: Double
        ): String {
            val uid = auth.currentUser?.uid ?: throw IllegalStateException("User not signed in")

            val rootRef = db.reference
            val bookingRef = rootRef.child("bookings").push()
            val bookingId = bookingRef.key ?: throw IllegalStateException("No push key")

            // Map for atomic multi-location update
            val bookingMap = hashMapOf<String, Any?>(
                "id" to bookingId,
                "userId" to uid,
                "movieId" to movieId,
                "movieTitle" to movieTitle,
                "posterUrl" to posterUrl,
                "cinema" to cinema,
                "seat" to seat,
                "showDate" to showDate,
                "showTime" to showTime,
                "price" to price,
                "createdAt" to ServerValue.TIMESTAMP
            )

            val updates = hashMapOf<String, Any?>(
                "/bookings/$bookingId" to bookingMap,
                "/users/$uid/history/$bookingId" to bookingMap
            )

            // Use await() (KTX) to suspend until complete
            rootRef.updateChildren(updates).await()  // throws if fails
            return bookingId
        }
    }
