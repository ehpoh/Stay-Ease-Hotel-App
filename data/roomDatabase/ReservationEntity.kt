package com.example.stayeasehotel.data.roomDatabase

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "reservations")
data class ReservationEntity (
    @PrimaryKey val reservationId: String,  // local ID
    val userName: String,
    val userEmail: String,
    val userPhone: String,
    val roomId: String?,
    val roomType: String?,
    val checkInDate: Long?,
    val checkOutDate: Long?,
    val roomCount: Int,
    val nights: Int?,
    val totalPrice: Double?,
    val requests: List<String> = emptyList(),// needs converter
    val bookingStatus: String,
    val createdAt: Long
)

@Entity(
    tableName = "payments",
    foreignKeys = [
        ForeignKey(
            entity = ReservationEntity::class,
            parentColumns = ["reservationId"],
            childColumns = ["reservationId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("reservationId")]
)
data class PaymentEntity(
    @PrimaryKey val paymentId: String,
    val reservationId: String,
    val paymentOption: String,
    val cardLast4: String?,
    val amount: Double,
    val paymentStatus: String
)