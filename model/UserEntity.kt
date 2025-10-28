package com.example.stayeasehotel.model

data class UserEntity(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val phoneNum: String = "",
    val gender: String = "",
    val dateOfBirth: String = "",

    //for sign in
    val confirmPassword: String = "",
    val areaCode: String = "",
    val phoneNumber: String = "",
    val day: String = "",
    val month: String = "",
    val year: String = "",

    //check error
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)