package com.example.stayeasehotel.data.LostItemData

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import com.example.stayeasehotel.R
import com.example.stayeasehotel.ui.user.ThisIsMine.ExpandableTextField
import com.example.stayeasehotel.ui.user.ThisIsMine.SectionCard

object LostItemDataSource {
    val fieldKeys = listOf(

        R.string.item_title,
        R.string.found_location, R.string.date_found,
        R.string.time_found,
        R.string.category,R.string.description

    )

    val categories = listOf(
        R.string.electronics,
        R.string.clothing_accessories,
        R.string.documents_cards,
        R.string.jewelry_valuables,
        R.string.bags_luggage,
        R.string.keys,
        R.string.others
    )

    val locations = listOf(
        R.string.lobby,
        R.string.restaurant,
        R.string.gym, R.string.room,
        R.string.pool,R.string.others
    )

    val thisIsMineQuestion = listOf(
        R.string.Question1,
        R.string.Question2,
        R.string.Question3,
        R.string.Question4
    )

    val urgentCategoryIds = listOf(
        R.string.documents_cards,
        R.string.keys,
        R.string.bags_luggage
    )


    val submitter = listOf(R.string.staff, R.string.user)


}