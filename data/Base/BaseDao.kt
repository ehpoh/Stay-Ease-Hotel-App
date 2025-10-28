package com.example.stayeasehotel.data.Base

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Update

import kotlinx.coroutines.flow.Flow

@Dao
abstract class BaseDao<T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertItem(item: T)

    @Update
    abstract suspend fun updateItem(item: T)

    @Delete
    abstract suspend fun deleteItem(item: T)
}