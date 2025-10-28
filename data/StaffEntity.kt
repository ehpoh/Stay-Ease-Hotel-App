package com.example.stayeasehotel.data

data class StaffEntity(
    val staffId: String = "",
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val phoneNum: String = "",
    val gender: String = "",
    val position: String = "",

    val areaCode: String = "",
    val phoneNumber: String = "",
    //check error
    val isLoading: Boolean = false,
    val errorMessage: String? = null,

    // Edit state properties
    val editStaffName: String = "",
    val editStaffPhoneNum: String = "",
    val editStaffCurrentPassword: String = "",
    var editStaffNewPassword: String = "",
    val editStaffConfirmPassword: String = "",
    val editStaffIsLoading: Boolean = false,
    val editStaffErrorMessage: String? = null,
    val editStaffIsSuccess: Boolean = false
)

object StaffPositions {
    const val BOSS = "Boss"
    const val ADMIN = "Admin"
    const val STAFF = "Staff"
}