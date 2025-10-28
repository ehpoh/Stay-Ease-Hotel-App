package com.example.stayeasehotel.shareFilterScreen

import androidx.lifecycle.ViewModel
import com.example.stayeasehotel.data.ClaimData.ClaimEntity
import com.example.stayeasehotel.data.LostItemData.LostItemEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.LocalTime

data class FilterUiState(
    val searchQuery: String = "",
    val selectedCategories: List<String> = listOf("All"),
    val selectedLocations: List<String> = listOf("All"),
    val selectedStatuses: List<String> = listOf("All"),
    val selectedSubmitters: List<String> = listOf("All"),


    val isSubmitterExpanded: Boolean = false,
    val isStatusExpanded: Boolean = false,


    val recentItems: List<LostItemEntity> = emptyList(),
    val urgentItems: List<LostItemEntity> = emptyList(),
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
    val startTime: LocalTime? = null,
    val endTime: LocalTime? = null,
    val isFilterDialogVisible: Boolean = false,
    val isClearFilter: Boolean = false,
    val hasFiltered: Boolean = false,
    val allLostItems: List<LostItemEntity> = emptyList(),
    val filteredLostItems: List<LostItemEntity> = emptyList(),
    val allClaimItems: List<ClaimEntity> = emptyList(),
    val filteredClaimItems: List<ClaimEntity> = emptyList(),
    val isCategoryExpanded: Boolean = false,
    val isLocationExpanded: Boolean = false,
    val showClearFiltersConfirmation:Boolean=false
)
