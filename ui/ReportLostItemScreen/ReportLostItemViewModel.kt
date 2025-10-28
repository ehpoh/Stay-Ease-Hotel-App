package com.example.stayeasehotel.ui.ReportLostItemScreen



import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.*
import com.example.stayeasehotel.data.LostItemData.HotelLostItemDatabase
import com.example.stayeasehotel.data.LostItemData.LostItemEntity
import com.example.stayeasehotel.R


import com.example.stayeasehotel.data.LostItemData.LostItemRepository

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


import android.net.Uri
import android.util.Log

import androidx.compose.ui.res.stringResource
import androidx.core.content.FileProvider
import androidx.core.net.toUri


import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.io.File
import java.io.FileOutputStream

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

import androidx.compose.ui.res.stringResource
import com.example.stayeasehotel.data.LostItemData.ReporterInfo
import com.example.stayeasehotel.ui.StaffSession
import com.example.stayeasehotel.ui.UserSession
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await



class ReportLostItemViewModel(application: Application) : AndroidViewModel(application) {


    private val currentUser  =UserSession.currentUser
    private val currentStaff  = StaffSession.currentStaff

    val isStaff: Boolean
        get() = currentStaff != null
    private val _uiState = MutableStateFlow(ReportLostItemUiState())
    val uiState: StateFlow<ReportLostItemUiState> = _uiState.asStateFlow()


    private val repository: LostItemRepository

    var roomIdList by mutableStateOf<List<String>>(emptyList())
        private set

    val allLostItems: LiveData<List<LostItemEntity>>

    init {
        val lostItemDao = HotelLostItemDatabase.getLostItemDatabase(application).lostItemDao()
        repository = LostItemRepository(lostItemDao)

        allLostItems = repository.allLostItems.asLiveData()




        viewModelScope.launch {

            //  Step 1: Use Room first to generate ID and get room list


            roomIdList = repository.getAllRoomIds()

            // Step 2: Then sync from Firestore in background
            repository.syncFromFirestore()
        }
    }



    private fun getReporterInfo(): ReporterInfo? {
        return when {
            currentUser != null -> ReporterInfo(
                id = currentUser.userId,
                name = currentUser.name,

                type = "User"
            )
            currentStaff != null -> ReporterInfo(
                id = currentStaff.staffId,
                name = currentStaff.name,

                type = "Staff"
            )
            else -> null
        }
    }




    fun getTodayInMillis(): Long {
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    fun isDateSelectable(dateInMillis: Long): Boolean {
        val today = getTodayInMillis()
        val cal = Calendar.getInstance().apply {
            timeInMillis = dateInMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis <= today
    }

    fun formatDate(millis: Long): String {
        return SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            .format(Date(millis))
    }


    // Example of updating fields:
    fun setTitleChange(newTitle: String) {
        _uiState.update { currentState ->
            currentState.copy(itemTitle = newTitle)
        }
    }


    fun setSelectedImages(uris: List<Uri>) {
        _uiState.value = _uiState.value.copy(selectedImages = _uiState.value.selectedImages + uris)
    }

    fun setRemoveImage(index: Int) {
        val updatedList = _uiState.value.selectedImages.toMutableList().apply { removeAt(index) }
        _uiState.value = _uiState.value.copy(selectedImages = updatedList)
    }

    fun setFoundLocationChange(newLocation: String) {
        _uiState.value = _uiState.value.copy(selectedLocation  = newLocation)
    }

    fun setDateFoundChange(newDate: String) {
        _uiState.value = _uiState.value.copy(dateFound = newDate)
    }

    fun setTimeFoundChange(newTime: String) {
        _uiState.value = _uiState.value.copy(timeFound = newTime)
    }

    fun setCategoryChange(newCategory: String) {
        _uiState.value = _uiState.value.copy(selectedCategory = newCategory)
    }



    fun setDescriptionChange(newDescription: String) {
        _uiState.value = _uiState.value.copy(description = newDescription)
    }

    fun setPreviewIndexChange(index: Int) {
        _uiState.value = _uiState.value.copy(previewIndex = index)
    }

    fun setShowDatePicker(show: Boolean) {
        _uiState.value = _uiState.value.copy(showDatePicker = show)
    }

    fun setShowTimePicker(show: Boolean) {
        _uiState.value = _uiState.value.copy(showTimePicker = show)
    }

    fun setShowDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showDialog = show)
    }



    fun setExpandedLocationChange(expanded: Boolean) {
        _uiState.value = _uiState.value.copy(expandedLocation = expanded)


    }

    fun updateSelectedRoomID(roomId: String) {
        _uiState.update { it.copy(
            selectedRoomID = roomId
        )}
    }

    fun setExpandedRoomIDChange(expanded: Boolean) {
        _uiState.update { it.copy(expandedRoomID = expanded) }
    }



//    fun setCurrentUser(user: UserEntity) {
//        _uiState.value = _uiState.value.copy(currentUser = user)
//    }
    fun setExpandedCategoryChange(expanded: Boolean) {
        _uiState.value = _uiState.value.copy(expandedCategory = expanded)
    }


    fun setOtherCategoryChange(categoty: String) {
        _uiState.value = _uiState.value.copy(otherCategory = categoty)
    }

    fun setOtherFoundLocationChange(location: String) {
        _uiState.value = _uiState.value.copy( otherLocation =  location)
    }

    fun createImageUri(context: Context): Uri {
        val imageFile = File(context.cacheDir, "captured_${System.currentTimeMillis()}.jpg")
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            imageFile
        )
        Log.d("CameraDebug", "Generated URI: $uri")
        return uri
    }






    fun setDateFoundError(error: String?) {
        _uiState.update {
            it.copy(dateFoundError = error)
        }
    }


    fun ReportNewLostItem() {
        val state = _uiState.value



        val othersString = getApplication<Application>().getString(R.string.others)
        val roomString = getApplication<Application>().getString(R.string.room)


        val foundLocationValue = when {
            state.selectedLocation == othersString -> state.otherLocation.trim()
            state.selectedLocation == roomString -> state.selectedRoomID.trim()
            else -> state.selectedLocation.trim()
        }
        val categoryValue = if (state.selectedCategory == othersString) {
            state.otherCategory.trim()
        } else {
            state.selectedCategory.trim()
        }



        _uiState.value = state.copy(isSubmitting = true)
        val requiredFieldsFilled = state.itemTitle.isNotBlank()
                && state.selectedLocation.isNotBlank()
                && state.dateFound.isNotBlank()
                && state.timeFound.isNotBlank()
                && state.selectedCategory.isNotBlank()


        viewModelScope.launch {
            if (requiredFieldsFilled) {
                try {
                    val newId = repository.generateSequentialIdFromFireBase("L")

                    _uiState.update { currentState ->
                        currentState.copy(submittedLostId = newId)
                    }

                    val reporterInfo = getReporterInfo()
                    val initialStatus = if (reporterInfo?.type == "Staff") {
                        "Approved"
                    } else {
                        "Pending"
                    }

                    val newLostItem = LostItemEntity(

                        id = newId,
                        itemTitle = state.itemTitle,
                        foundLocation = foundLocationValue,
                        dateFound = state.dateFound,
                        timeFound = state.timeFound,
                        category = categoryValue,
                        description = state.description,
                        imageUrls = state.selectedImages.map { it.toString() },
                        reporter = reporterInfo!!,

                        status=initialStatus,
                        reportTime = System.currentTimeMillis(),
                        reportReason = null

                    )



                    repository.insert(newLostItem)



                    _uiState.value = _uiState.value.copy(

                        itemTitle = "",
                        selectedLocation = "",
                        dateFound = "",
                        timeFound = "",
                        selectedCategory = "",
                        description = "",
                        otherCategory = "",
                        otherLocation = "",
                        selectedRoomID = "",
                        selectedImages = emptyList(),
                        cameraImageUri = null,
                        isSubmitting = false,
                        submissionSuccess = true,
                        errorMessage = null
                    )





                } catch (e: Exception) {
                    _uiState.value = state.copy(
                        isSubmitting = false,
                        submissionSuccess = false,
                        errorMessage = e.message
                    )
                }
            } else {
                _uiState.value = state.copy(
                    isSubmitting = false,
                    submissionSuccess = false,
                    errorMessage = "Please fill in all required fields before submitting."
                )
            }
        }
    }


    fun clearMessages() {
        _uiState.value = _uiState.value.copy(errorMessage = null, submissionSuccess = false)
    }






    fun setCameraImageUri(uri: Uri) {
        _uiState.value = _uiState.value.copy(cameraImageUri = uri)

    }

    fun addCapturedImage(uri: Uri?) {
        uri?.let { nonNullUri ->
            val updatedList = _uiState.value.selectedImages.toMutableList()
            updatedList.add(nonNullUri)
            _uiState.value = _uiState.value.copy(selectedImages = updatedList)
        }
    }



}








