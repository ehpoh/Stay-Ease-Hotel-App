package com.example.stayeasehotel.data.roomDatabase

import androidx.room.Embedded
import androidx.room.Relation

data class ReservationWithPayment(
    @Embedded val reservation: ReservationEntity,

    @Relation(
        parentColumn = "reservationId",
        entityColumn = "reservationId"
    )
    val payment: PaymentEntity?
)