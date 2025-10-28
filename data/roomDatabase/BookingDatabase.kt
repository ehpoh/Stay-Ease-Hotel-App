package com.example.stayeasehotel.data.roomDatabase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [ReservationEntity::class, PaymentEntity::class], version = 3, exportSchema = false)
@TypeConverters(Converters::class)
abstract class BookingDatabase: RoomDatabase() {
    abstract fun reservationDao(): ReservationDAO
    abstract fun paymentDao(): PaymentDAO
    companion object {
        @Volatile
        private var Instance: BookingDatabase? = null

        fun getDatabase(context: Context): BookingDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    BookingDatabase::class.java,
                    "booking_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also{ Instance = it }
            }
        }
    }
}