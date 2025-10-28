package com.example.stayeasehotel.ui.user.LostFoundCenterScreen.LostAndFoundScreen

import android.app.Application
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.stayeasehotel.data.ClaimData.ClaimItemDatabase
import com.example.stayeasehotel.data.ClaimData.ClaimRepository
import com.example.stayeasehotel.data.LostItemData.HotelLostItemDatabase

import com.example.stayeasehotel.data.LostItemData.LostItemEntity
import com.example.stayeasehotel.data.LostItemData.LostItemRepository
import com.example.stayeasehotel.shareFilterScreen.FilterUiState
import com.example.stayeasehotel.ui.UserSession
import kotlinx.coroutines.flow.MutableSharedFlow

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted

import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.text.equals

sealed class UiEvent {
    data class ShowToast(val message: String) : UiEvent()
    data class MarkAsMine(val item: LostItemEntity) : UiEvent()
}
class LostandFoundCenterViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(LostandFoundCenterUiState())
    val uiState: StateFlow<LostandFoundCenterUiState> = _uiState.asStateFlow()
    private val lostRepository: LostItemRepository
    private val claimRepository: ClaimRepository
    private val _lostItems = MutableStateFlow<List<LostItemEntity>>(emptyList())
    val lostItems: StateFlow<List<LostItemEntity>> = _lostItems

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent



    init {

        val lostItemDao = HotelLostItemDatabase.getLostItemDatabase(application).lostItemDao()
        lostRepository = LostItemRepository(lostItemDao)
        val claimItemDao = ClaimItemDatabase.getClaimItemDatabase(application).claimItemDao()
        claimRepository = ClaimRepository(claimItemDao)


        val userId = UserSession.currentUser?.userId
        _uiState.update { it.copy(currentUserId = userId) }
        viewModelScope.launch {
            // Collect Room data immediately so UI updates right away
            launch {
                lostRepository.allLostItems
                    .combine(claimRepository.allClaimItems) { lostItems, claims ->
                        // Keep only approved lost items that do NOT have an approved claim
                        lostItems.filter { lostItem ->
                            lostItem.status.equals("approved", ignoreCase = true) &&
                                    claims.none { claim ->
                                        claim.item?.id == lostItem.id &&
                                                claim.claimStatus.equals("approved", ignoreCase = true)
                                    }
                        }
                    }
                    .collect { filteredItems ->
                        _uiState.update { it.copy(lostItems = filteredItems) }
                        _lostItems.value = filteredItems
                    }
            }



            // Show loading indicator while syncing
            _uiState.update { it.copy(inserting = true) }

            // Sync from Firestore in background, updating local DB
            lostRepository.syncFromFirestore()

            // Hide loading indicator when done syncing
            _uiState.update { it.copy(inserting = false) }
        }


    }




    fun getFormattedReportTime(reportTime: Long): String {
         val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")

        return try {
            val dateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(reportTime),
                ZoneId.systemDefault()
            )
            dateTime.format(formatter)
        } catch (e: Exception) {
            "Unknown"
        }
    }
    fun canClaim(item: LostItemEntity, userId: String): Boolean {
        val reporterId = item.reporter?.id?.trim()
        return reporterId != userId.trim()
    }


    fun handleMarkAsMine(item: LostItemEntity, currentUserId: String?) {
        if (currentUserId == null) return

        viewModelScope.launch {
            if (!canClaim(item, currentUserId)) {
                _uiEvent.emit(UiEvent.ShowToast("You cannot claim your own reported item."))
            } else {
                // Check if user already claimed
                val alreadyClaimed = checkIfAlreadyClaimed(item.id, currentUserId)

                // Update uiState accordingly
                _uiState.update { currentState ->
                    currentState.copy(isAlreadyClaimedByUser = alreadyClaimed)
                }

                if (alreadyClaimed) {
                    _uiEvent.emit(UiEvent.ShowToast("Claim already submitted. Please wait for review."))
                } else {
                    _uiEvent.emit(UiEvent.MarkAsMine(item))
                }


            }
        }
    }

    suspend fun checkIfAlreadyClaimed(itemId: String, userId: String): Boolean {
        val claims = claimRepository.getClaimsForItemByUser(itemId, userId)
        return claims.isNotEmpty()
    }













}
