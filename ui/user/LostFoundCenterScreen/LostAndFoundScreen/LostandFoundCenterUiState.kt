package com.example.stayeasehotel.ui.user.LostFoundCenterScreen.LostAndFoundScreen

import android.net.Uri
import androidx.compose.runtime.remember
import com.example.stayeasehotel.data.LostItemData.LostItemEntity
import java.time.LocalDate
import java.time.LocalTime


data class LostandFoundCenterUiState(

    val inserting: Boolean = false,

    val lostItems: List<LostItemEntity> = emptyList(),


    val expandedStates: Map<String, Boolean> = emptyMap(),
    val selectedItem: LostItemEntity? = null,
    val expandedLostItemCard: Boolean = false,
    val expandedCardIds: Set<String> = emptySet(),
    val currentUserId: String? = null,
    val isAlreadyClaimedByUser :Boolean=false

)

