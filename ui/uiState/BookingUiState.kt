package com.example.stayeasehotel.ui.uiState

import com.example.stayeasehotel.ui.navigation.CardFieldError
import com.example.stayeasehotel.ui.navigation.PaymentOption

data class BookingUiState(
    val checkInDate: Long? = null,
    val checkOutDate: Long? = null,
    val roomCount: Int = 1,
    val selectedRoom: HotelRoomUiState? = null,
    val nights: Int? = null,
    val subtotal: Double? = null,
    val totalPrice: Double? = null,
    val userName: String = "", // Default values for testing
    val userEmail: String? = null,
    val userPhone: String = "",
    val selectedRequests: List<String> = emptyList(),
    val paymentOption: PaymentOption = PaymentOption.CREDIT_CARD,
    val cardNumber: String = "",
    val expMonth: String = "",
    val expYear: String = "",
    val cvv: String = "",
    val cardNumberError: CardFieldError = CardFieldError.NONE,
    val expMonthError: CardFieldError = CardFieldError.NONE,
    val expYearError: CardFieldError = CardFieldError.NONE,
    val cvvError: CardFieldError = CardFieldError.NONE
)