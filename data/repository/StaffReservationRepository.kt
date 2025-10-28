package com.example.stayeasehotel.data.repository


import com.example.stayeasehotel.ui.uiState.ReservationUiState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class StaffReservationRepository(private val db: FirebaseFirestore) {
    fun getReservationList(onResult: (List<ReservationUiState>) -> Unit) {
        db.collection("Reservation")
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    onResult(emptyList())
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val resList = snapshot.documents.mapIndexed { index, doc ->
                        ReservationUiState(
                            id = doc.id,
                            bookingNo = "R" + (index + 1).toString().padStart(4, '0'),
                            userName = doc.getString("userId") ?: "",
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
            .orderBy("createdAt", Query.Direction.ASCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                val docs = snapshot.documents
                val index = docs.indexOfFirst { it.id == reservationId }

                if (index != -1) {
                    val doc = docs[index]
                    val reservation = doc.toObject(ReservationUiState::class.java)?.copy(
                        id = doc.id,
                        bookingNo = "R" + (index + 1).toString().padStart(4, '0')
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

    fun updateReservationStatus(reservationId: String, newStatus: String) {
        db.collection("Reservation").document(reservationId)
            .update("bookingStatus", newStatus)
    }
}