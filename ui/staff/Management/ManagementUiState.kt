package com.example.stayeasehotel.ui.staff.Management;

import com.example.stayeasehotel.data.ClaimData.ClaimEntity
import com.example.stayeasehotel.data.LostItemData.LostItemEntity


data class ManagementUiState(
    val claimItems: List<ClaimEntity> = emptyList(),
    val lostItems: List<LostItemEntity> = emptyList(),

    // For expanding claim cards
    val expandedClaimId: String? = null,

    // For expanding lost items
    val expandedStates: Map<String, Boolean> = emptyMap(),

    // Dialog handling (Confirm, Reason, or None)
    val dialogState: DialogState = DialogState.None,

    // For text input in Reason dialog
    val actionReason: String = ""
)

