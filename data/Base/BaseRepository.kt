package com.example.stayeasehotel.data.Base

import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await



abstract class BaseRepository<T>(
    private val collectionName: String,
    private val entityClass: Class<T>
) {
    protected val firestore = FirebaseFirestore.getInstance()
    protected val collection = firestore.collection(collectionName)

    suspend fun generateSequentialIdFromFireBase(prefix: String): String {
        // Decide which document to use based on prefix
        val documentName = when (prefix) {
            "L" -> "lostItemsCounter"
            "C" -> "claimItemsCounter"
            else -> throw IllegalArgumentException("Invalid prefix: $prefix")
        }

        val docRef = Firebase.firestore.collection("counters").document(documentName)

        return firestore.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)
            val current = snapshot.getLong("lastId") ?: 0
            val next = current + 1
            transaction.set(docRef, mapOf("lastId" to next))
            "$prefix${"%04d".format(next)}"
        }.await()
    }




    protected suspend fun sync(
        clearAll: suspend () -> Unit,
        insertItem: suspend (T) -> Unit,
        mapEntity: (T, String) -> T
    ) {
        try {
            clearAll()
            val snapshot = collection.get().await()
            snapshot.documents.mapNotNull {
                it.toObject(entityClass)?.let { entity -> mapEntity(entity, it.id) }
            }.forEach { insertItem(it) }
        } catch (e: Exception) {

            throw e
        }
    }

    abstract suspend fun insert(item: T)
    abstract suspend fun update(item: T)
    abstract suspend fun delete(item: T)
    abstract suspend fun syncFromFirestore()
}
