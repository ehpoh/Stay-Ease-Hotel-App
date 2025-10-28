package com.example.stayeasehotel.data.repository

import android.content.Context
import com.example.stayeasehotel.data.roomDatabase.BookingDatabase
import com.example.stayeasehotel.data.roomDatabase.PaymentEntity
import com.example.stayeasehotel.data.roomDatabase.ReservationEntity
import com.example.stayeasehotel.data.roomDatabase.ReservationWithPayment
import kotlinx.coroutines.flow.Flow

class BookingRepository(
    context: Context
): InterfaceBookingRepository {
    private val db = BookingDatabase.Companion.getDatabase(context)
    private val reservationDAO = db.reservationDao()
    private val paymentDao = db.paymentDao()

    override suspend fun insertReservation(reservation: ReservationEntity) {
        reservationDAO.insertReservation(reservation)
    }

    override suspend fun insertPayment(payment: PaymentEntity) {
        paymentDao.insertPayment(payment)
    }

    override fun getUserReservationsWithPayments(userId: String): Flow<List<ReservationWithPayment>> {
        return reservationDAO.getReservationsWithPaymentByUserId(userId)
    }

    override fun getReservationById(reservationId: String): Flow<ReservationEntity?> {
        return reservationDAO.getReservationById(reservationId)
    }

    override fun getPaymentForReservation(reservationId: String): Flow<PaymentEntity?> {
        return paymentDao.getPaymentForReservation(reservationId)
    }

    override fun getAllReservations(): Flow<List<ReservationEntity>> {
        return reservationDAO.getAllReservations()
    }

    override fun getAllPayments(): Flow<List<PaymentEntity>> {
        return paymentDao.getAllPayments()
    }

    override suspend fun updateReservationStatus(reservationId: String, newStatus: String) {
        reservationDAO.updateReservationStatus(reservationId, newStatus)
    }

    override suspend fun updatePaymentStatus(paymentId: String, newStatus: String) {
        paymentDao.updatePaymentStatus(paymentId, newStatus)
    }

}