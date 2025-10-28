package com.example.stayeasehotel.data

data class UserEntity(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val phoneNum: String = "",
    val gender: String = "",
    val dateOfBirth: String = "",
    val profileImageUrl: String = "",

    //for sign in
    val confirmPassword: String = "",
    val areaCode: String = "",
    val phoneNumber: String = "",
    val day: String = "",
    val month: String = "",
    val year: String = "",

    //check error
    val isLoading: Boolean = false,
    val errorMessage: String? = null,

    // Edit state properties
    val editUserName: String = "",
    val editUserPhoneNum: String = "",
    val editUserCurrentPassword: String = "",
    var editUserNewPassword: String = "",
    val editUserConfirmPassword: String = "",
    val editUserIsLoading: Boolean = false,
    val editUserErrorMessage: String? = null,
    val editUserIsSuccess: Boolean = false
)