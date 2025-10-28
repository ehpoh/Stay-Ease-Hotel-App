package com.example.stayeasehotel.data.remoteSource


import com.example.stayeasehotel.ui.uiState.PaymentUiState
import com.example.stayeasehotel.ui.uiState.ReservationUiState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class StaffBookingRemoteDataSource(private val db: FirebaseFirestore) {
    fun getReservationList(onResult: (List<ReservationUiState>) -> Unit) {
        db.collection("Reservation")
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    onResult(emptyList())
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val resList = snapshot.documents
                        .filter { doc -> doc.getBoolean("isHidden") != true } // treat null as false
                        .mapIndexed { index, doc ->
                            ReservationUiState(
                                id = doc.id,
                                bookingNo = "R" + (index + 1).toString().padStart(4, '0'),
                                userName = doc.getString("userName") ?: "",
                                userEmail = doc.getString("userEmail") ?: "",
                                userPhone = doc.getString("userPhone") ?: "",
                                roomId = doc.getString("roomId") ?: "",
                                roomType = doc.getString("roomType") ?: "",
                                bookingStatus = doc.getString("bookingStatus") ?: "",
                                paymentId = doc.getString("paymentId"),
                                createdAt = doc.getTimestamp("createdAt")
                            )
                        }
                    onResult(resList)
                } else {
                    onResult(emptyList())
                }
            }
    }

    fun getReservationById(reservationId: String, onResult: (ReservationUiState?) -> Unit) {
        db.collection("Reservation")
            .document(reservationId) // Direct query for the document
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val reservation = ReservationUiState(
                        id = doc.id,
                        // The following fields are now correctly retrieved from the document
                        bookingNo = doc.id, // Assumed field from the database
                        userName = doc.getString("userName") ?: "",
                        userEmail = doc.getString("userEmail") ?: "",
                        userPhone = doc.getString("userPhone") ?: "",
                        roomId = doc.getString("roomId") ?: "",
                        roomType = doc.getString("roomType") ?: "",
                        bookingStatus = doc.getString("bookingStatus") ?: "",
                        checkInDate = doc.getLong("checkInDate"),
                        checkOutDate = doc.getLong("checkOutDate"),
                        roomCount = (doc.getLong("roomCount") ?: 0).toInt(),
                        nights = (doc.getLong("nights") ?: 0).toInt(),
                        totalPrice = doc.getDouble("totalPrice"),
                        requests = doc.get("requests") as? List<String> ?: emptyList(),
                        createdAt = doc.getTimestamp("createdAt") // This is the key fix
                    )
                    onResult(reservation)
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    suspend fun updateReservationStatus(reservationId: String, newStatus: String) {
        db.collection("Reservation").document(reservationId)
            .update("bookingStatus", newStatus)
            .await()
    }

    fun getPaymentList(onResult: (List<PaymentUiState>) -> Unit) {
        db.collection("Payment")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    onResult(emptyList())
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val payList = snapshot.documents.map { doc ->
                        PaymentUiState(
                            id = doc.id,
                            reservationId = doc.getString("reservationId") ?: "",
                            paymentOption = doc.getString("paymentOption") ?: "",
                            cardLast4 = doc.getString("cardLast4"),
                            amount = doc.getDouble("amount") ?: 0.0,
                            paymentStatus = doc.getString("paymentStatus") ?: ""
                        )
                    }
                    onResult(payList)
                } else {
                    onResult(emptyList())
                }
            }
    }

    fun getPaymentByReservationId(reservationId: String, onResult: (PaymentUiState?) -> Unit) {
        db.collection("Payment")
            .whereEqualTo("reservationId", reservationId)
            .get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.isEmpty) {
                    val doc = snapshot.documents.first()
                    val payment = doc.toObject(PaymentUiState::class.java)?.copy(id = doc.id)
                    onResult(payment)
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener { onResult(null) }
    }

    suspend fun updatePaymentStatus(paymentId: String, newStatus: String) {
        db.collection("Payment").document(paymentId)
            .update("paymentStatus", newStatus)
            .await()
    }

    suspend fun hideReservation(reservationId: String) {
        db.collection("Reservation").document(reservationId)
            .update("isHidden", true)
            .await()
    }
}