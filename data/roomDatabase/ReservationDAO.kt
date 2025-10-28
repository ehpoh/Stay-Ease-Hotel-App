package com.example.stayeasehotel.data.roomDatabase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface ReservationDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReservation(booking: ReservationEntity)

    @Transaction
    @Query("Select * FROM reservations WHERE userName = :userId ORDER BY checkInDate DESC")
    fun getReservationsWithPaymentByUserId(userId: String): Flow<List<ReservationWithPayment>>

    @Query("SELECT * FROM reservations ORDER BY checkInDate DESC")
    fun getAllReservations(): Flow<List<ReservationEntity>>

    @Query("SELECT * FROM reservations WHERE reservationId = :reservationId LIMIT 1")
    fun getReservationById(reservationId: String): Flow<ReservationEntity?>

    @Query("UPDATE reservations SET bookingStatus = :newStatus WHERE reservationId = :reservationId")
    suspend fun updateReservationStatus(reservationId: String, newStatus: String)
}

@Dao
interface PaymentDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentEntity)

    @Query("SELECT * FROM payments WHERE reservationId = :reservationId LIMIT 1")
    fun getPaymentForReservation(reservationId: String): Flow<PaymentEntity>

    @Query("SELECT * FROM payments ORDER BY paymentId ASC")
    fun getAllPayments(): Flow<List<PaymentEntity>>

    @Query("UPDATE payments SET paymentStatus = :newStatus WHERE paymentId = :paymentId")
    suspend fun updatePaymentStatus(paymentId: String, newStatus: String)
}