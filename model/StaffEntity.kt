package com.example.stayeasehotel.model

data class StaffEntity(
    val staffId: String = "",
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val phoneNum: String = "",
    val gender: String = "",
    val post: String = "",

    //check error
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)