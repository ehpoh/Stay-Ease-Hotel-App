package com.example.stayeasehotel.ui.uiState

data class PaymentUiState(
    val id: String = "",
    val reservationId: String = "",
    val paymentOption: String = "",
    val cardLast4: String? = null,
    val amount: Double = 0.0,
    val paymentStatus: String = ""
)