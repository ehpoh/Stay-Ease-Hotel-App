package com.example.stayeasehotel.ui.LostItemUi.LostFoundCenterScreen.MyLostClaimItemScreen

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope

import com.example.stayeasehotel.data.ClaimData.ClaimEntity
import com.example.stayeasehotel.data.ClaimData.ClaimItemDatabase
import com.example.stayeasehotel.data.ClaimData.ClaimRepository
import com.example.stayeasehotel.data.LostItemData.HotelLostItemDatabase
import com.example.stayeasehotel.data.LostItemData.LostItemEntity
import com.example.stayeasehotel.data.LostItemData.LostItemRepository
import com.example.stayeasehotel.shareFilterScreen.FilterUiState
import com.example.stayeasehotel.ui.UserSession

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter


class MyLostClaimItemScreenViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(MyLostClaimItemScreenUiState())
    val uiState: StateFlow<MyLostClaimItemScreenUiState> = _uiState.asStateFlow()
    private val lostItemRepository: LostItemRepository
    private val claimItemRepository: ClaimRepository

    val currentUserId = UserSession.currentUser?.userId ?: ""





    init {

        val lostItemDao = HotelLostItemDatabase.getLostItemDatabase(application).lostItemDao()
        val claimItemDao = ClaimItemDatabase.getClaimItemDatabase(application).claimItemDao()

        lostItemRepository = LostItemRepository(lostItemDao)
        claimItemRepository=ClaimRepository(claimItemDao)


        viewModelScope.launch {
            // Step 1: Start collecting local Room data *first*, and do it in parallel

            launch {
                lostItemRepository.allLostItems.collect { items ->
                    val userLostItems = items.filter { it.reporter?.id == currentUserId }
                    _uiState.update { it.copy(lostItems = userLostItems) }
                }
            }


            launch {
                claimItemRepository.allClaimItems.collect { items ->
                    val userClaimItems = items.filter { it.claimer?.userId == currentUserId }
                    _uiState.update { it.copy(claimItems = userClaimItems) }
                }
            }






            // Step 2: Firestore sync in parallel (no blocking UI)
            launch {
                lostItemRepository.syncFromFirestore()
            }

            launch {
                claimItemRepository.syncFromFirestore()
            }


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

    fun showLostItemDetails(item: LostItemEntity) {
        _uiState.value = _uiState.value.copy(
            showLostDetails = true,
            selectedLostItem = item
        )
    }

    fun hideLostItemDetails() {
        _uiState.value = _uiState.value.copy(
            showLostDetails = false,
            selectedLostItem = null
        )
    }

    fun showClaimItemDetails(item: ClaimEntity) {
        _uiState.value = _uiState.value.copy(
            showClaimDetails = true,
            selectedClaimItem = item
        )
    }

    fun hideClaimItemDetails() {
        _uiState.value = _uiState.value.copy(
            showClaimDetails = false,
            selectedClaimItem = null
        )
    }










}
