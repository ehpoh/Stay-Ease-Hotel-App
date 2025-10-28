package com.example.stayeasehotel.shareFilterScreen

data class FilterOptions(
    val showCategory: Boolean = true,
    val showLocation: Boolean = true,
    val showDateTime: Boolean = true,
    val showSubmitter : Boolean = false,
    val showStatus: Boolean = false
)