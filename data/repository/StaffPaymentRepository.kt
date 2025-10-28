package com.example.stayeasehotel.data.repository

import com.example.stayeasehotel.ui.uiState.PaymentUiState
import com.google.firebase.firestore.FirebaseFirestore

class StaffPaymentRepository(private val db: FirebaseFirestore) {
    fun getPaymentLists(onResult: (List<PaymentUiState>) -> Unit) {
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

    fun getPaymentByReservationId(
        reservationId: String,
        onResult: (PaymentUiState?) -> Unit
    ) {
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
            .addOnFailureListener {
                onResult(null)
            }
    }

    fun updatePaymentStatus(paymentId: String, newStatus: String) {
        db.collection("Payment").document(paymentId)
            .update("paymentStatus", newStatus)
    }
}