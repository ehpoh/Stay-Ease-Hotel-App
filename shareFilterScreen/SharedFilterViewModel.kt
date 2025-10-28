package com.example.stayeasehotel.shareFilterScreen

import android.app.Application
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.stayeasehotel.data.ClaimData.ClaimEntity
import com.example.stayeasehotel.data.LostItemData.LostItemDataSource
import com.example.stayeasehotel.data.LostItemData.LostItemEntity
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale


class SharedFilterViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(FilterUiState())
    val uiState: StateFlow<FilterUiState> = _uiState.asStateFlow()

    private val _filteredLostItems = MutableStateFlow<List<LostItemEntity>>(emptyList())
    val filteredLostItems: StateFlow<List<LostItemEntity>> = _filteredLostItems
    private val _filteredClaimItems = MutableStateFlow<List<ClaimEntity>>(emptyList())
    val filteredClaimItems: StateFlow<List<ClaimEntity>> = _filteredClaimItems




    fun setAllLostItems(items: List<LostItemEntity>) {
        _uiState.update { it.copy(allLostItems = items) }
        performSearch()
    }

    fun setAllClaimItems(items: List<ClaimEntity>) {
        _uiState.update { it.copy(allClaimItems = items) }
        performSearch()
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        performSearch()
    }




    fun clearAllFilters() {
        _uiState.update { current ->
            current.copy(
                selectedCategories = listOf("All"),
                selectedLocations = listOf("All"),
                startDate = null,
                endDate = null,
                startTime = null,
                endTime = null,
                selectedSubmitters = listOf("All"),
                selectedStatuses = listOf("All"),
                hasFiltered = false
            )
        }

        performSearch()
    }




    fun toggleLocation(location: String, enabled: Boolean) {
        val location = normalizeInput(location, LostItemDataSource.locations)

        _uiState.update { state ->
            val current = state.selectedLocations.toMutableList()
            if (location == "All") {
                if (enabled) {
                    // "All" selected → clear others and keep only "All"
                    current.clear()
                    current.add("All")
                } else {
                    // "All" deselected → just remove it
                    current.remove("All")
                }
            } else {
                current.remove("All") // deselect "All" if any specific category is toggled

                if (enabled) {
                    current.add(location)
                } else {
                    current.remove(location)
                }
            }

            state.copy(selectedLocations = current)
        }

    }


    fun showClearFiltersConfirmationDialog(show: Boolean) {
        _uiState.update { it.copy(showClearFiltersConfirmation = show) }
    }

    fun toggleCategory(category: String, enabled: Boolean) {
        val category = normalizeInput(category, LostItemDataSource.categories)

        _uiState.update { state ->
            val current = state.selectedCategories.toMutableList()

            if (category == "All") {
                if (enabled) {
                    // "All" selected → clear others and keep only "All"
                    current.clear()
                    current.add("All")
                } else {
                    // "All" deselected → just remove it
                    current.remove("All")
                }
            } else {
                current.remove("All") // deselect "All" if any specific category is toggled

                if (enabled) {
                    current.add(category)
                } else {
                    current.remove(category)
                }
            }

            state.copy(selectedCategories = current)
        }

    }

    private fun normalizeInput(input: String, resIds: List<Int>): String {
        val normalizedInput = input.trim().lowercase()
        if (input.equals("All")) {
            return "All"
        }
        if (normalizedInput.matches(Regex("(?i)^RM\\d+$"))) {
            return "Room"
        }
        val context = getApplication<Application>().applicationContext
        resIds.forEach { resId ->
            val optionString = context.getString(resId)

            if (optionString.lowercase() == normalizedInput) {
                return optionString
            }
        }
        return "Others"
    }
    fun setStartDate(date: LocalDate) {
        _uiState.update { it.copy(startDate = date) }
    }

    fun setEndDate(date: LocalDate) {
        _uiState.update { it.copy(endDate = date) }
    }

    private val _errorEvent = MutableSharedFlow<String>()
    val errorEvent: SharedFlow<String> = _errorEvent

    fun setStartTime(selectedStartTime: LocalTime) {
        viewModelScope.launch {
            val latestState = _uiState.value // ← read fresh state inside coroutine
            if (latestState.endTime == null || selectedStartTime <= latestState.endTime) {
                _uiState.value = latestState.copy(
                    startTime = selectedStartTime,
                    isFilterDialogVisible = true
                )
            } else {
                _errorEvent.emit("Start time cannot be later than end time.")
            }
        }
    }

    fun setEndTime(selectedEndTime: LocalTime) {
        viewModelScope.launch {
            val latestState = _uiState.value // ← read fresh state inside coroutine
            if (latestState.startTime == null || selectedEndTime >= latestState.startTime) {
                _uiState.value = latestState.copy(
                    endTime = selectedEndTime,
                    isFilterDialogVisible = true
                )
            } else {
                _errorEvent.emit("End time cannot be earlier than start time.")
            }
        }
    }


    fun toggleFilterDialog(show: Boolean) {
        _uiState.update { it.copy(isFilterDialogVisible = show) }
    }

    fun removeFilter(type: String, value: String) {
        when(type) {
            "Category" -> _uiState.update { currentState ->
                currentState.copy(
                    selectedCategories = currentState.selectedCategories - value
                )
            }
            "Location" -> _uiState.update { currentState ->
                currentState.copy(
                    selectedLocations = currentState.selectedLocations - value
                )
            }
            "Status" -> _uiState.update { currentState ->
                currentState.copy(
                    selectedStatuses = currentState.selectedStatuses - value
                )
            }
            "Submitter" -> _uiState.update { currentState ->
                currentState.copy(
                    selectedSubmitters = currentState.selectedSubmitters - value
                )
            }
            "Start Date" -> _uiState.update { currentState ->
                currentState.copy(
                    startDate = null
                )
            }
            "End Date" -> _uiState.update { currentState ->
                currentState.copy(
                    endDate = null
                )
            }
            "Start Time" -> _uiState.update { currentState ->
                currentState.copy(
                    startTime = null
                )
            }
            "End Time" -> _uiState.update { currentState ->
                currentState.copy(
                    endTime = null
                )
            }
            else -> { /* no-op or log unknown type */}
        }
        performSearch()
    }



    fun setIsStatusExpanded(expanded: Boolean) {
        _uiState.update { it.copy(isStatusExpanded = expanded) }
    }

    fun toggleStatus(status: String, enabled: Boolean) {



        _uiState.update { state ->
            val current = state.selectedStatuses.toMutableList()
            if (status == "All") {
                if (enabled) {
                    // "All" selected → clear others and keep only "All"
                    current.clear()
                    current.add("All")
                } else {
                    // "All" deselected → just remove it
                    current.remove("All")
                }
            } else {
                current.remove("All") // deselect "All" if any specific category is toggled

                if (enabled) {
                    current.add(status)
                } else {
                    current.remove(status)
                }
            }

            state.copy(selectedStatuses = current)
        }


    }

    fun setIsSubmitterExpanded(expanded: Boolean) {
        _uiState.update { it.copy(isSubmitterExpanded = expanded) }
    }

    fun toggleSubmitter(submitter: String, enabled: Boolean) {

        _uiState.update { state ->
            val current = state.selectedSubmitters.toMutableList()
            if (submitter == "All") {
                if (enabled) {
                    // "All" selected → clear others and keep only "All"
                    current.clear()
                    current.add("All")
                } else {
                    // "All" deselected → just remove it
                    current.remove("All")
                }
            } else {
                current.remove("All") // deselect "All" if any specific category is toggled

                if (enabled) {
                    current.add(submitter)
                } else {
                    current.remove(submitter)
                }
            }

            state.copy(selectedSubmitters = current)
        }



    }



    fun performSearch() {

        val trimmedQuery = _uiState.value.searchQuery.trim()
        _uiState.update { it.copy(searchQuery = trimmedQuery) }

        val state = _uiState.value

        val selectedCategory = state.selectedCategories
        val selectedLocation = state.selectedLocations
        val selectedSubmitters = state.selectedSubmitters
        val selectedStatuses = state.selectedStatuses


        _filteredLostItems.value = state.allLostItems.filter { item ->


            val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val itemDate = try {
                LocalDate.parse(item.dateFound, dateFormatter)
            } catch (e: Exception) {
                LocalDate.MIN
            }

            val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH)


            val normalizedTimeString = item.timeFound.trim().replace("am", "AM").replace("pm", "PM")

            val itemTime = LocalTime.parse(normalizedTimeString, timeFormatter)




            val matchesCategory = "All" in selectedCategory || selectedCategory.isEmpty() || item.category in selectedCategory
            val matchesLocation = "All" in selectedLocation || selectedLocation.isEmpty() || item.foundLocation in selectedLocation
            val matchesSubmitter = "All" in selectedSubmitters || selectedSubmitters.isEmpty() || item.reporter.type in selectedSubmitters
            val matchesStatus = "All" in selectedStatuses || selectedStatuses.isEmpty() || item.status in selectedStatuses

            val matchesSearch = trimmedQuery.isBlank() ||

                    item.id.contains(trimmedQuery, ignoreCase = true) ||
                    item.itemTitle.contains(trimmedQuery, ignoreCase = true) ||
                    item.description?.contains(trimmedQuery, ignoreCase = true) == true



            val matchesDate = (state.startDate == null || !itemDate.isBefore(state.startDate)) &&
                    (state.endDate == null || !itemDate.isAfter(state.endDate))

            val matchesTime = (state.startTime == null || !itemTime.isBefore(state.startTime)) &&
                    (state.endTime == null || !itemTime.isAfter(state.endTime))

            matchesCategory &&
                    matchesLocation &&
                    matchesSubmitter &&
                    matchesStatus &&
                    matchesSearch &&
                    matchesDate &&
                    matchesTime


        }


        _filteredClaimItems.value = state.allClaimItems.filter { claim ->


            val claimDateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(claim.claimTime),
                ZoneId.systemDefault()
            )

            val claimDate = claimDateTime.toLocalDate()
            val claimTime = claimDateTime.toLocalTime()

            val matchesDate = (state.startDate == null || !claimDate.isBefore(state.startDate)) &&
                    (state.endDate == null || !claimDate.isAfter(state.endDate))

            val matchesTime = (state.startTime == null || !claimTime.isBefore(state.startTime)) &&
                    (state.endTime == null || !claimTime.isAfter(state.endTime))




            val matchesCategory = "All" in selectedCategory || selectedCategory.isEmpty() ||
                    (claim.item?.category in selectedCategory)

            val matchesLocation = "All" in selectedLocation || selectedLocation.isEmpty() ||
                    (claim.item?.foundLocation in selectedLocation)

            val matchesStatus = "All" in selectedStatuses || selectedStatuses.isEmpty() ||
                    (claim.claimStatus in selectedStatuses)



            val matchesSearch = trimmedQuery.isBlank() ||
                    claim.claimId.contains(trimmedQuery, ignoreCase = true) ||
                    (claim.item?.id?.contains(trimmedQuery, ignoreCase = true) == true) ||
                    (claim.item?.itemTitle?.contains(trimmedQuery, ignoreCase = true) == true) ||
                    (claim.item?.description?.contains(trimmedQuery, ignoreCase = true) == true) ||
                    (claim.claimDescription.contains(trimmedQuery, ignoreCase = true))



            matchesCategory &&
                    matchesLocation &&
                    matchesStatus &&

                    matchesSearch&&
                    matchesDate&&
                    matchesTime

        }


        _uiState.update { it.copy(hasFiltered = true) }
    }

    fun validateDateTimeRange(): Boolean {
        val state = _uiState.value


        if ((state.startDate == null) != (state.endDate == null)) {
            viewModelScope.launch {
                _errorEvent.emit("Both start date and end date must be set or cleared.")
            }
             return false
        }

        if ((state.startTime == null) != (state.endTime == null)) {
            viewModelScope.launch {
                _errorEvent.emit("Both start time and end time must be set or cleared.")
            }
             return false
        }
        return true
    }


    fun setIsCategoryExpanded(isCategoryExpanded: Boolean) {
        _uiState.value = _uiState.value.copy(isCategoryExpanded = isCategoryExpanded)
    }

    fun setIsLocationExpanded(isLocationExpanded: Boolean) {
        _uiState.value = _uiState.value.copy(isLocationExpanded = isLocationExpanded)
    }
}