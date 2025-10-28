package com.example.stayeasehotel.data.LostItemData



import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.stayeasehotel.data.Base.BaseRepository
import com.example.stayeasehotel.shareFilterScreen.FilterUiState

import kotlinx.coroutines.flow.Flow
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.tasks.await



//middle-man between   UI (ViewModel) and the database (HotelRoomDatabase)
class LostItemRepository(private val lostItemDao: LostItemDao): BaseRepository<LostItemEntity>("lost_Items",LostItemEntity::class.java) {


    val allLostItems: Flow<List<LostItemEntity>> = lostItemDao.getAllLostItems()
    //
    override suspend fun insert(item: LostItemEntity) {

        lostItemDao.insertItem(item)
        collection.document(item.id).set(item).await()
    }

    suspend fun getItemById(itemId: String): LostItemEntity? {
        return lostItemDao.getItemById(itemId)
    }






    override suspend fun update(item: LostItemEntity) {
        lostItemDao.updateItem(item)
        collection.document(item.id).set(item).await()
    }

    override suspend fun delete(item: LostItemEntity) {
        lostItemDao.deleteItem(item)
        collection.document(item.id).delete().await()
    }


    override suspend fun syncFromFirestore() {
        sync(
            clearAll = { lostItemDao.clearAllData() },
            insertItem = { lostItemDao.insertItem(it) },
            mapEntity = { entity, id -> entity.copy(id = id) }
        )
    }


    suspend fun getAllRoomIds(): List<String> {
        return try {
            val snapshot = FirebaseFirestore.getInstance()
                .collection("Rooms")
                .get()
                .await()

            snapshot.documents.mapNotNull { it.getString("roomId") }
        } catch (e: Exception) {
            emptyList()
        }
    }








}

