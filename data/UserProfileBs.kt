package com.example.stayeasehotel.data

import com.example.stayeasehotel.R
import com.example.stayeasehotel.model.UserProfileB

object UserProfileBs {
    val UserProfileBarList = listOf(
        UserProfileB(
            buttonUserBName = R.string.userP1,
            buttonUserBImage = R.drawable.profile
        ),
        UserProfileB(
            buttonUserBName = R.string.userP2,
            buttonUserBImage = R.drawable.logout
        ),
        UserProfileB(
            buttonUserBName = R.string.userP3,
            buttonUserBImage = R.drawable.delete
        )
    )
}