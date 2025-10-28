package com.example.stayeasehotel.data.LostItemData

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.stayeasehotel.model.UserEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName="lost_Items")
@TypeConverters(Converters::class)

data class LostItemEntity (
    @PrimaryKey val  id: String = "",
    var itemTitle: String = "",
    var foundLocation: String = "",
    var dateFound: String = "",
    var timeFound: String = "",
    var category: String = "",
    var description: String? = null,
    var imageUrls: List<String> = emptyList(),
    val reportTime: Long = System.currentTimeMillis(),
    var status: String = "Pending",
    val reporter: ReporterInfo = ReporterInfo("", "","","",""),
    val reportReason: String?=null



){
    val formattedDate: String
        get() = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(reportTime))

    val formattedTime: String
        get() = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(reportTime))
}


