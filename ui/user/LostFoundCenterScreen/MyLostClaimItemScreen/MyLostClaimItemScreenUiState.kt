package com.example.stayeasehotel.ui.LostItemUi.LostFoundCenterScreen.MyLostClaimItemScreen

import com.example.stayeasehotel.data.ClaimData.ClaimEntity
import com.example.stayeasehotel.data.LostItemData.LostItemEntity



data class MyLostClaimItemScreenUiState(

    val lostItems: List<LostItemEntity> = emptyList(),
    val claimItems: List<ClaimEntity> = emptyList(),
    val showLostDetails : Boolean= false,
    val showClaimDetails : Boolean= false,
    val selectedLostItem: LostItemEntity? = null,
    val selectedClaimItem: ClaimEntity? = null,



    )
