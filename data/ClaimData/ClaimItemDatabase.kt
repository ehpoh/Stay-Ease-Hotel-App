package com.example.stayeasehotel.data.ClaimData

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

import com.example.stayeasehotel.data.ClaimData.ClaimItemDatabase
import com.example.stayeasehotel.data.ClaimData.ClaimDao
import com.example.stayeasehotel.data.LostItemData.LostItemEntity


@Database(entities = [ClaimEntity::class], version = 2, exportSchema = false)

abstract class ClaimItemDatabase : RoomDatabase() {
    abstract fun claimItemDao(): ClaimDao

    companion object {
        @Volatile
        private var Instance: com.example.stayeasehotel.data.ClaimData.ClaimItemDatabase? = null

        fun getClaimItemDatabase(context: Context): com.example.stayeasehotel.data.ClaimData.ClaimItemDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    ClaimItemDatabase::class.java,
                    "ClaimItem_Database"
                ).fallbackToDestructiveMigration().build().also { Instance = it}

            }
        }
    }
}