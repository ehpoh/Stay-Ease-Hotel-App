package com.example.stayeasehotel.ui.ReportLostItemScreen

import android.net.Uri
import com.example.stayeasehotel.model.UserEntity


import java.util.Calendar


data class ReportLostItemUiState(
    val submittedLostId: String = "",
    val itemTitle: String = "",
    val selectedLocation: String = "",
    val selectedRoomID:String="",
    val dateFound: String = "",
    val timeFound: String = "",
    val selectedCategory: String = "",
    val description: String = "",
    val selectedImages: List<Uri> = emptyList(),
    val status: String = "",
    val currentUser: UserEntity? = null,
    val cameraImageUri: Uri? = null,

    val isSubmitting: Boolean = false,
    val submissionSuccess: Boolean = false,
    val errorMessage: String? = null,
    val reportTime: Long = System.currentTimeMillis(),
    val previewIndex: Int = -1,
    val showDatePicker: Boolean = false,
    val showTimePicker: Boolean = false,
    val expandedCategory: Boolean = false,
    val otherCategory: String = "",
    val otherLocation: String = "",
    val expandedLocation: Boolean = false,
    val expandedRoomID: Boolean = false,
    val showDialog: Boolean =false,
    val dateFoundError: String? = null,

    val selectedDateMillis: Long = Calendar.getInstance().timeInMillis,

    val selectedHour: Int? = null,
    val selectedMinute: Int? = null,

    val isToday: Boolean = true,

    val availableHours: List<Int> = (0..23).toList(),
    val availableMinutes: List<Int> = emptyList(),

    )
