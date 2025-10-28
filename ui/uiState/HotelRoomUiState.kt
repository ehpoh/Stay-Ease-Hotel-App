package com.example.stayeasehotel.ui.uiState

data class HotelRoomUiState(
    val roomId: String = "",
    val roomType: String = "",
    val pricePerNight: Double = 0.0,
    val capacity: Int = 0,
    val amenities: List<String> = emptyList(),
    val description: String = "",
    val squareFoot: Int = 0,
    val floor: String = "",
    val bedType: String = "",
    val image: String? = null, // Use URL instead of DrawableRes
    val availableRequests: List<String> = emptyList(),
    val totalRooms: Int = 0
)