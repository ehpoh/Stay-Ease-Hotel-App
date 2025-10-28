package com.example.stayeasehotel.data.LostItemData

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

//HotelLostItemDatabase real database
@Database(entities = [LostItemEntity::class], version =2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class HotelLostItemDatabase : RoomDatabase() {
    abstract fun lostItemDao(): LostItemDao

    companion object {
        @Volatile
        private var Instance: com.example.stayeasehotel.data.LostItemData.HotelLostItemDatabase? = null

        fun getLostItemDatabase(context: Context): com.example.stayeasehotel.data.LostItemData.HotelLostItemDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    HotelLostItemDatabase::class.java,
                    "LostItem_Database"
                ).fallbackToDestructiveMigration().build().also { Instance = it}

            }
        }
    }
}
