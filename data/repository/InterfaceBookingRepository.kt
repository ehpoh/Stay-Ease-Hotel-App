package com.example.stayeasehotel.data.repository

import com.example.stayeasehotel.data.roomDatabase.PaymentEntity
import com.example.stayeasehotel.data.roomDatabase.ReservationEntity
import com.example.stayeasehotel.data.roomDatabase.ReservationWithPayment
import kotlinx.coroutines.flow.Flow

interface InterfaceBookingRepository {
    fun getUserReservationsWithPayments(userId: String): Flow<List<ReservationWithPayment>>

    fun getReservationById(reservationId: String): Flow<ReservationEntity?>

    fun getPaymentForReservation(reservationId: String): Flow<PaymentEntity?>

    fun getAllReservations(): Flow<List<ReservationEntity>>
    fun getAllPayments(): Flow<List<PaymentEntity>>

    suspend fun insertReservation(reservation: ReservationEntity)
    suspend fun insertPayment(payment: PaymentEntity)

    suspend fun updateReservationStatus(reservationId: String, status: String)

    suspend fun updatePaymentStatus(paymentId: String, status: String)
}