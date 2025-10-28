package com.example.stayeasehotel.data.LostItemData

import androidx.room.*
import com.example.stayeasehotel.data.Base.BaseDao
import kotlinx.coroutines.flow.Flow

@Dao
abstract class LostItemDao : BaseDao<LostItemEntity>() {

    @Query("SELECT * FROM lost_Items")
    abstract  fun getAllLostItems(): kotlinx.coroutines.flow.Flow<List<LostItemEntity>>

    @Query("SELECT * FROM lost_Items ORDER BY id DESC LIMIT 1")
    abstract suspend fun getLatestLostItem(): LostItemEntity?

    @Query("DELETE FROM lost_items")
    abstract suspend fun clearAllData()

    @Query("SELECT * FROM lost_items WHERE id = :itemId LIMIT 1")
    abstract suspend fun getItemById(itemId: String): LostItemEntity?

}

