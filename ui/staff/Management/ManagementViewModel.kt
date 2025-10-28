package com.example.stayeasehotel.ui.staff.Management

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.stayeasehotel.data.ClaimData.ClaimEntity
import com.example.stayeasehotel.data.ClaimData.ClaimItemDatabase
import com.example.stayeasehotel.data.ClaimData.ClaimRepository
import com.example.stayeasehotel.data.LostItemData.HotelLostItemDatabase
import com.example.stayeasehotel.data.LostItemData.LostItemEntity
import com.example.stayeasehotel.data.LostItemData.LostItemRepository
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


enum class ClaimAction { APPROVE, REJECT, IN_PROGRESS, DELETE, RESTORE }
enum class LostAction { APPROVE, REJECT, DELETE, RESTORE }


sealed class DialogState {
    object None : DialogState()
    data class ConfirmClaim(val action: ClaimAction, val item: ClaimEntity) : DialogState()
    data class ConfirmLost(val action: LostAction, val item: LostItemEntity) : DialogState()
    data class Reason(val action: String, val itemId: String) : DialogState()
}


class ManagementViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(ManagementUiState())
    val uiState: StateFlow<ManagementUiState> = _uiState.asStateFlow()

    private val lostItemRepository: LostItemRepository
    private val claimItemRepository: ClaimRepository

    val allLostItems: LiveData<List<LostItemEntity>>
    val allClaimItems: LiveData<List<ClaimEntity>>

    init {
        val lostItemDao = HotelLostItemDatabase.getLostItemDatabase(application).lostItemDao()
        lostItemRepository = LostItemRepository(lostItemDao)

        val claimItemDao = ClaimItemDatabase.getClaimItemDatabase(application).claimItemDao()
        claimItemRepository = ClaimRepository(claimItemDao)

        allLostItems = lostItemRepository.allLostItems.asLiveData()
        allClaimItems = claimItemRepository.allClaimItems.asLiveData()


        viewModelScope.launch {
            claimItemRepository.allClaimItems.collect { items ->
                _uiState.update { it.copy(claimItems = items) }
            }
        }
        viewModelScope.launch {
            lostItemRepository.allLostItems.collect { items ->
                _uiState.update { it.copy(lostItems = items) }
            }
        }

        viewModelScope.launch { claimItemRepository.syncFromFirestore() }
        viewModelScope.launch { lostItemRepository.syncFromFirestore() }
    }



    fun toggleExpandClaim(claimId: String) {
        _uiState.update {
            it.copy(expandedClaimId = if (it.expandedClaimId == claimId) null else claimId)
        }
    }

    fun setActionReason(reason: String) {
        _uiState.update { it.copy(actionReason = reason) }
    }

    fun dismissDialog() {
        _uiState.update { it.copy(dialogState = DialogState.None, actionReason = "") }
    }

    fun setClaimAction(item: ClaimEntity, action: ClaimAction) {
        val needsReason = (action == ClaimAction.REJECT || action == ClaimAction.DELETE)
        _uiState.update {
            it.copy(
                dialogState = if (needsReason) DialogState.Reason(action.name, item.claimId)
                else DialogState.ConfirmClaim(action, item)
            )
        }
    }

    fun setLostAction(item: LostItemEntity, action: LostAction) {
        val needsReason = (action == LostAction.REJECT || action == LostAction.DELETE)
        _uiState.update {
            it.copy(
                dialogState = if (needsReason) DialogState.Reason(action.name, item.id)
                else DialogState.ConfirmLost(action, item)
            )
        }
    }

    fun performClaimAction(action: ClaimAction, item: ClaimEntity) {
        val reason = if (action == ClaimAction.REJECT || action == ClaimAction.DELETE)
            _uiState.value.actionReason else defaultReason(action)

        viewModelScope.launch {
            when (action) {
                ClaimAction.APPROVE     -> updateClaim(item, "Approved", reason)
                ClaimAction.REJECT      -> updateClaim(item, "Rejected", reason)
                ClaimAction.DELETE      -> updateClaim(item, "Deleted", reason)
                ClaimAction.IN_PROGRESS -> updateClaim(item, "In Progress", reason)
                ClaimAction.RESTORE     -> updateClaim(item, "Pending", "")
            }
            dismissDialog()
        }
    }

    fun performLostAction(action: LostAction, item: LostItemEntity) {
        val reason = if (action == LostAction.REJECT || action == LostAction.DELETE)
            _uiState.value.actionReason else ""

        viewModelScope.launch {
            when (action) {
                LostAction.APPROVE -> updateLost(item, "Approved", "✅ Your reported lost item has been approved.")
                LostAction.REJECT  -> updateLost(item, "Rejected", reason)
                LostAction.DELETE  -> updateLost(item, "Deleted", reason)
                LostAction.RESTORE -> updateLost(item, "Pending", "")
            }
            dismissDialog()
        }
    }


    private fun defaultReason(action: ClaimAction): String = when (action) {
        ClaimAction.APPROVE     -> "✅ Your reported claim item has been approved."
        ClaimAction.IN_PROGRESS -> "⏳ Your claim is currently being processed."
        else -> ""
    }

    private suspend fun updateClaim(item: ClaimEntity, status: String, reason: String) {
        val updated = item.copy(claimStatus = status, claimReason = reason)
        Firebase.firestore.collection("claim_Items").document(item.claimId)
            .update(mapOf("status" to status, "reason" to reason)).await()
        claimItemRepository.update(updated)
        _uiState.update { it.copy(claimItems = it.claimItems.map { if (it.claimId == item.claimId) updated else it }) }
    }

    private suspend fun updateLost(item: LostItemEntity, status: String, reason: String) {
        val updated = item.copy(status = status, reportReason = reason)
        Firebase.firestore.collection("lost_Items").document(item.id)
            .update(mapOf("status" to status, "reason" to reason)).await()
        lostItemRepository.update(updated)
        _uiState.update { it.copy(lostItems = it.lostItems.map { if (it.id == item.id) updated else it }) }
    }

    fun toggleInfoExpanded(itemId: String) {
        val current = _uiState.value.expandedStates[itemId] ?: false
        val updated = _uiState.value.expandedStates.toMutableMap()
        updated[itemId] = !current

        _uiState.value = _uiState.value.copy(expandedStates = updated)
    }
}
