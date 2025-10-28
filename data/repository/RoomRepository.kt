package com.example.stayeasehotel.data.repository

import com.example.stayeasehotel.ui.uiState.HotelRoomUiState
import com.google.firebase.firestore.FirebaseFirestore

class RoomRepository(private val db: FirebaseFirestore) {
    fun fetchRooms(onResult: (List<HotelRoomUiState>) -> Unit) {
        db.collection("Rooms")
            .get()
            .addOnSuccessListener { result ->
                val rooms = result.mapNotNull { doc ->
                    doc.toObject(HotelRoomUiState::class.java).copy(roomId = doc.id)
                }
                onResult(rooms)
            }
            .addOnFailureListener {
                onResult(emptyList())
            }
    }
}