package com.example.stayeasehotel.domain

import com.example.stayeasehotel.data.remoteSource.BookingRemoteDataSource
import com.example.stayeasehotel.ui.uiState.BookingUiState
import com.example.stayeasehotel.ui.uiState.HotelRoomUiState
import com.example.stayeasehotel.data.repository.BookingRepository
import com.example.stayeasehotel.data.roomDatabase.PaymentEntity
import com.example.stayeasehotel.data.roomDatabase.ReservationEntity
import com.example.stayeasehotel.data.remoteSource.StaffBookingRemoteDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.suspendCoroutine
import kotlin.coroutines.resume


class BookingUseCase(
    private val localRepository: BookingRepository,
    private val remoteGuestSource: BookingRemoteDataSource,
    private val remoteStaffSource: StaffBookingRemoteDataSource
) {
    suspend fun saveBooking(booking: BookingUiState): Boolean = suspendCoroutine { continuation ->
        remoteGuestSource.saveBooking(booking) { success, reservation, payment ->
            if (success && reservation != null && payment != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    localRepository.insertReservation(reservation)
                    localRepository.insertPayment(payment)
                }
            } else {
                continuation.resume(false)
            }
        }
    }

    // My Booking
    /*fun getUserBookings(userId: String): Flow<List<ReservationWithPayment>> {
        // Always return Room Flow, so UI is reactive
        return localRepository.getUserReservationsWithPayments(userId)
    }*/

    // My Booking
    suspend fun getUserBookings(userEmail: String): List<Pair<ReservationEntity, PaymentEntity>> =
        suspendCoroutine { continuation ->
            remoteGuestSource.getUserBookings(userEmail) { bookings ->
                continuation.resume(bookings)
            }
        }

    fun checkRoomAvailability(
        room: HotelRoomUiState,
        checkInDate: Long?,
        checkOutDate: Long?,
        onResult: (Boolean, Int) -> Unit
    ) {
        remoteGuestSource.checkRoomAvailability(room, checkInDate, checkOutDate, onResult)
    }

    fun getAllReservations(): Flow<List<ReservationEntity>> = callbackFlow{
        remoteStaffSource.getReservationList { remoteList ->
            launch {
                if (remoteList.isNotEmpty()) {
                    // Convert UiState -> Entity for Room
                    val entities = remoteList.map {
                        ReservationEntity(
                            reservationId = it.id,
                            userName = it.userName,
                            userEmail = it.userEmail,
                            userPhone = it.userPhone,
                            roomId = it.roomId,
                            roomType = it.roomType,
                            bookingStatus = it.bookingStatus,
                            checkInDate = it.checkInDate,
                            checkOutDate = it.checkOutDate,
                            roomCount = it.roomCount,
                            nights = it.nights,
                            totalPrice = it.totalPrice,
                            requests = it.requests,
                            createdAt = it.createdAt?.toDate()?.time ?: 0L
                        )
                    }
                    trySend(entities)
                } else {
                    trySend(emptyList())
                }
            }
        }
        awaitClose { }
        //return localRepository.getAllReservations()
    }

    fun getAllPayments(): Flow<List<PaymentEntity>> = callbackFlow{
        remoteStaffSource.getPaymentList { remoteList ->
            launch {
                if (remoteList.isNotEmpty()) {
                    val entities = remoteList.map {
                        PaymentEntity(
                            paymentId = it.id,
                            reservationId = it.reservationId,
                            paymentOption = it.paymentOption,
                            cardLast4 = it.cardLast4,
                            amount = it.amount,
                            paymentStatus = it.paymentStatus
                        )
                    }
                    entities.forEach { localRepository.insertPayment(it) }
                    trySend(entities)
                } else {
                    localRepository.getAllPayments().collect { trySend(it) }
                }
            }
        }
        awaitClose { }
        // return localRepository.getAllPayments()
    }

    fun getPaymentForReservation(reservationId: String): Flow<PaymentEntity?> = callbackFlow{
        remoteStaffSource.getPaymentByReservationId(reservationId) { remotePay ->
            launch {
                if (remotePay != null) {
                    val entity = PaymentEntity(
                        paymentId = remotePay.id,
                        reservationId = remotePay.reservationId,
                        paymentOption = remotePay.paymentOption,
                        cardLast4 = remotePay.cardLast4,
                        amount = remotePay.amount,
                        paymentStatus = remotePay.paymentStatus
                    )
                    localRepository.insertPayment(entity)
                    trySend(entity)
                } else {
                    localRepository.getPaymentForReservation(reservationId).collect { trySend(it) }
                }
            }
        }
        awaitClose { }
        //return localRepository.getPaymentForReservation(reservationId)
    }
    fun getReservationById(reservationId: String): Flow<ReservationEntity?> = callbackFlow{
        remoteStaffSource.getReservationById(reservationId) { remoteRes ->
            launch {
                if (remoteRes != null) {
                    val entity = ReservationEntity(
                        reservationId = remoteRes.id,
                        userName = remoteRes.userName,
                        userEmail = remoteRes.userEmail,
                        userPhone = remoteRes.userPhone,
                        roomId = remoteRes.roomId,
                        roomType = remoteRes.roomType,
                        bookingStatus = remoteRes.bookingStatus,
                        checkInDate = remoteRes.checkInDate,
                        checkOutDate = remoteRes.checkOutDate,
                        roomCount = remoteRes.roomCount,
                        nights = remoteRes.nights,
                        totalPrice = remoteRes.totalPrice,
                        requests = remoteRes.requests,
                        createdAt = remoteRes.createdAt?.toDate()?.time ?: 0L
                    )
                    localRepository.insertReservation(entity)
                    trySend(entity)
                } else {
                    localRepository.getReservationById(reservationId).collect { trySend(it) }
                }
            }
        }
        awaitClose { }
        //return localRepository.getReservationById(reservationId)
    }

    suspend fun updateReservationStatus(reservationId: String, newStatus: String) {
        try {
            // Firestore first
            remoteStaffSource.updateReservationStatus(reservationId, newStatus)
            // then local DB
            localRepository.updateReservationStatus(reservationId, newStatus)
        } catch (e: Exception) {
            throw e
        }

    }

    suspend fun updatePaymentStatus(paymentId: String, newStatus: String) {
        try {
            // Firestore first
            remoteStaffSource.updatePaymentStatus(paymentId, newStatus)
            // then local DB
            localRepository.updatePaymentStatus(paymentId, newStatus)
        } catch (e: Exception) {
            throw e
        }

    }

    suspend fun hideReservation(reservationId: String) {
        remoteStaffSource.hideReservation(reservationId)
    }
}