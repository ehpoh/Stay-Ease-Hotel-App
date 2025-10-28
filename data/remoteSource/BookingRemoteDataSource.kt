package com.example.stayeasehotel.data.remoteSource

import com.example.stayeasehotel.ui.uiState.BookingUiState
import com.example.stayeasehotel.ui.uiState.HotelRoomUiState
import com.example.stayeasehotel.data.roomDatabase.PaymentEntity
import com.example.stayeasehotel.data.roomDatabase.ReservationEntity
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

class BookingRemoteDataSource(private val db: FirebaseFirestore) {
    fun saveBooking(
        booking: BookingUiState,
        onResult: (success: Boolean, reservation: ReservationEntity?, payment: PaymentEntity?) -> Unit
    ) {
        val reservationData = hashMapOf(
            "checkInDate" to booking.checkInDate,
            "checkOutDate" to booking.checkOutDate,
            "roomId" to booking.selectedRoom?.roomId,
            "roomType" to booking.selectedRoom?.roomType,
            "roomCount" to booking.roomCount,
            "nights" to booking.nights,
            "totalPrice" to booking.totalPrice,
            "userName" to booking.userName,
            "userEmail" to booking.userEmail,
            "userPhone" to booking.userPhone,
            "requests" to booking.selectedRequests,
            "bookingStatus" to "Pending",
            "createdAt" to FieldValue.serverTimestamp(),
            "isHidden" to false
        )

        db.collection("Reservation")
            .add(reservationData)
            .addOnSuccessListener { reservationDoc ->
                val paymentStatus = if (booking.paymentOption.name == "CREDIT_CARD") {
                    "Completed"
                } else {
                    "On-Hold"
                }
                val paymentData = hashMapOf(
                    "reservationId" to reservationDoc.id,
                    "paymentOption" to booking.paymentOption.name,
                    "cardLast4" to booking.cardNumber.takeLast(4), // only last 4 digits
                    "amount" to booking.totalPrice,
                    "paymentStatus" to paymentStatus,
                    "createdAt" to FieldValue.serverTimestamp()
                )

                db.collection("Payment")
                    .add(paymentData)
                    .addOnSuccessListener { paymentDoc ->
                        reservationDoc.update("paymentId", paymentDoc.id)
                            .addOnSuccessListener {
                                reservationDoc.get().addOnSuccessListener { resSnap ->
                                    val resCreatedAt =
                                        resSnap.getTimestamp("createdAt")?.toDate()?.time ?: 0L

                                    val reservationEntity = ReservationEntity(
                                        reservationId = reservationDoc.id,
                                        userName = booking.userName,
                                        userPhone = booking.userPhone,
                                        userEmail = booking.userEmail ?: "",
                                        roomId = booking.selectedRoom?.roomId ?: "",
                                        roomType = booking.selectedRoom?.roomType ?: "",
                                        roomCount = booking.roomCount,
                                        checkInDate = booking.checkInDate ?: 0L,
                                        checkOutDate = booking.checkOutDate ?: 0L,
                                        nights = booking.nights ?: 0,
                                        totalPrice = booking.totalPrice ?: 0.0,
                                        bookingStatus = "Pending",
                                        requests = booking.selectedRequests,
                                        createdAt = resCreatedAt
                                    )

                                    val paymentEntity = PaymentEntity(
                                        paymentId = paymentDoc.id,
                                        reservationId = reservationDoc.id,
                                        paymentOption = booking.paymentOption.name,
                                        cardLast4 = booking.cardNumber.takeLast(4),
                                        amount = booking.totalPrice ?: 0.0,
                                        paymentStatus = paymentStatus
                                    )

                                    onResult(true, reservationEntity, paymentEntity)
                                }
                                    .addOnFailureListener { onResult(false, null, null) }
                            }
                            .addOnFailureListener { onResult(false, null, null) }
                    }
                    .addOnFailureListener { onResult(false, null, null) }
            }
    }


    fun checkRoomAvailability(
        room: HotelRoomUiState,
        checkInDate: Long?,
        checkOutDate: Long?,
        onResult: (Boolean, Int) -> Unit
    ) {
        db.collection("Rooms").document(room.roomId)
            .get()
            .addOnSuccessListener { doc ->
                val totalRooms = doc.getLong("totalRooms")?.toInt() ?: 0
                db.collection("Reservation")
                    .whereEqualTo("roomType", room.roomType)
                    .get()
                    .addOnSuccessListener { resDocs ->
                        var bookedRooms = 0

                        for (res in resDocs) {
                            val status = res.getString("bookingStatus") ?: continue

                            if (status != "Pending" && status != "Confirmed") continue

                            val resCheckInDate = res.getLong("checkInDate") ?: continue
                            val resCheckOutDate = res.getLong("checkOutDate") ?: continue
                            val roomsBooked = res.getLong("roomCount")?.toInt() ?: 0

                            val isBefore = checkOutDate!! <= resCheckInDate
                            val isAfter = checkInDate!! >= resCheckOutDate
                            val overlap = !(isBefore || isAfter)

                            if (overlap) {
                                bookedRooms += roomsBooked
                            }
                        }
                        val availableRooms = totalRooms - bookedRooms
                        onResult(availableRooms > 0, availableRooms)
                    }
                    .addOnFailureListener { onResult(false, 0) }
            }
            .addOnFailureListener { onResult(false, 0) }
    }

    // My Booking
    fun getUserBookings(
        userEmail: String,
        onResult: (List<Pair<ReservationEntity, PaymentEntity>>) -> Unit
    ) {
        db.collection("Reservation")
            .whereEqualTo("userEmail", userEmail)
            .get()
            .addOnSuccessListener { resDocs ->
                if (resDocs.isEmpty) {
                    onResult(emptyList())
                    return@addOnSuccessListener
                }

                val bookings = mutableListOf<Pair<ReservationEntity, PaymentEntity>>()

                var processed = 0

                for (res in resDocs) {
                    val reservation = ReservationEntity(
                        reservationId = res.id,
                        userName = res.getString("userName") ?: "",
                        userEmail = res.getString("userEmail") ?: "",
                        userPhone = res.getString("userPhone") ?: "",
                        roomId = res.getString("roomId"),
                        roomType = res.getString("roomType"),
                        checkInDate = res.getLong("checkInDate"),
                        checkOutDate = res.getLong("checkOutDate"),
                        roomCount = res.getLong("roomCount")?.toInt() ?: 0,
                        nights = res.getLong("nights")?.toInt(),
                        totalPrice = res.getDouble("totalPrice"),
                        requests = res.get("requests") as? List<String> ?: emptyList(),
                        bookingStatus = res.getString("bookingStatus") ?: "",
                        createdAt = res.getTimestamp("createdAt")?.toDate()?.time ?: 0L
                    )

                    val paymentRef = res.getString("paymentId")
                    if (paymentRef != null) {
                        db.collection("Payment").document(paymentRef).get()
                            .addOnSuccessListener { payDoc ->
                                val payment = PaymentEntity(
                                    paymentId = payDoc.id,
                                    reservationId = reservation.reservationId,
                                    paymentOption = payDoc.getString("paymentOption") ?: "",
                                    cardLast4 = payDoc.getString("cardLast4"),
                                    amount = payDoc.getDouble("amount") ?: 0.0,
                                    paymentStatus = payDoc.getString("paymentStatus") ?: ""
                                )
                                bookings.add(reservation to payment)
                                processed++
                                if (processed == resDocs.size()) onResult(bookings)
                            }
                            .addOnFailureListener {
                                processed++
                                if (processed == resDocs.size()) onResult(bookings)
                            }
                    } else {
                        val emptyPayment = PaymentEntity(
                            paymentId = "",
                            reservationId = reservation.reservationId,
                            paymentOption = "",
                            cardLast4 = "",
                            amount = 0.0,
                            paymentStatus = "N/A"
                        )
                        bookings.add(reservation to emptyPayment)
                        processed++
                        if (processed == resDocs.size()) onResult(bookings)
                    }
                }
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }
}