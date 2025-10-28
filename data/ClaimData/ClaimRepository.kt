package com.example.stayeasehotel.data.ClaimData

import com.example.stayeasehotel.data.Base.BaseRepository

import com.example.stayeasehotel.data.ClaimData.ClaimDao
import com.example.stayeasehotel.data.ClaimData.ClaimEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await




//middle-man between   UI (ViewModel) and the database (HotelRoomDatabase)
class ClaimRepository(private val claimDao: ClaimDao): BaseRepository<ClaimEntity>("claim_Items",entityClass = ClaimEntity::class.java) {

    val allClaimItems: Flow<List<ClaimEntity>> = claimDao.getAllClaimItems()
    //


    override suspend fun insert(item: ClaimEntity) {
        val id = if (item.claimId.isBlank()) {
            generateSequentialIdFromFireBase("C")
        } else {
            item.claimId
        }

        val newItem = item.copy(claimId = id)
        claimDao.insertItem(newItem)
        collection.document(newItem.claimId).set(newItem).await()
    }





    override suspend fun update(item: ClaimEntity) {
        claimDao.updateItem(item)
        collection.document(item.claimId).set(item).await()
    }

    override suspend fun delete(item: ClaimEntity) {
        claimDao.deleteItem(item)
        collection.document(item.claimId).delete().await()
    }

    suspend fun getClaimsForItemByUser(itemId: String, userId: String): List<ClaimEntity> {
        return claimDao.getClaimsForItemByUser(itemId, userId)
    }

    override suspend fun syncFromFirestore() {
        sync(
            clearAll = { claimDao.clearAllData() },
            insertItem = { claimDao.insertItem(it) },
            mapEntity = { entity, id -> entity.copy(claimId = id) }
        )
    }









}

