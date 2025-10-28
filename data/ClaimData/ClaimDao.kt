package com.example.stayeasehotel.data.ClaimData

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

import com.example.stayeasehotel.data.Base.BaseDao
import com.example.stayeasehotel.data.LostItemData.LostItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ClaimDao : BaseDao<ClaimEntity>() {

    @Query("SELECT * FROM claim_items")
    abstract fun getAllClaimItems(): kotlinx.coroutines.flow.Flow<List<ClaimEntity>>

    @Query("DELETE FROM claim_items")
    abstract suspend fun clearAllData()

    @Query("SELECT * FROM claim_items ORDER BY claimId DESC LIMIT 1")
    abstract suspend fun getLatestClaimItem(): ClaimEntity?

    @Query("SELECT * FROM claim_items WHERE id = :itemId AND claimer_userId = :userId")
    abstract suspend fun getClaimsForItemByUser(itemId: String, userId: String): List<ClaimEntity>

}
