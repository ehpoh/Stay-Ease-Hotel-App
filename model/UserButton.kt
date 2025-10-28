package com.example.stayeasehotel.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class UserButton(
    @StringRes val buttonUserName: Int,
    @DrawableRes val buttonUserImage: Int
)
