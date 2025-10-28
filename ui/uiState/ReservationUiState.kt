package com.example.stayeasehotel.ui.uiState

import com.google.firebase.Timestamp

data class ReservationUiState (
    val id: String = "",
    val bookingNo: String = "",
    val userName: String = "",
    val userEmail: String = "",
    val userPhone: String = "",
    val checkInDate: Long? = null,
    val checkOutDate: Long? = null,
    val roomId: String = "",
    val roomType: String = "",
    val roomCount: Int = 1,
    val nights: Int = 1,
    val totalPrice: Double? = 0.0,
    val requests: List<String> = emptyList(),
    val bookingStatus: String = "",
    val paymentId: String? = null,
    val createdAt: Timestamp? = null
)