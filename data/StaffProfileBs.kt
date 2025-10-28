package com.example.stayeasehotel.data

import com.example.stayeasehotel.R
import com.example.stayeasehotel.model.StaffProfileB

object StaffProfileBs {
    val StaffProfileBarList = listOf(
        StaffProfileB(
            buttonStaffBName = R.string.staffP1,
            buttonStaffBImage = R.drawable.profile
        ),
        StaffProfileB(
            buttonStaffBName = R.string.staffP2,
            buttonStaffBImage = R.drawable.staff
        ),
        StaffProfileB(
            buttonStaffBName = R.string.staffP3,
            buttonStaffBImage = R.drawable.logout
        )
    )
}