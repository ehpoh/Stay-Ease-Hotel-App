package com.example.stayeasehotel.data.ClaimData

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

import com.example.stayeasehotel.data.LostItemData.Converters
import com.example.stayeasehotel.data.LostItemData.LostItemEntity
import com.example.stayeasehotel.model.UserEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName="claim_Items")
@TypeConverters(Converters::class)
data class ClaimEntity(
    @PrimaryKey val claimId: String ="",
    var  claimDescription: String="",
    var  marks: String="",
    var  proofFileUri: List<String> = emptyList(),
    var  notes: String="",
    val claimTime: Long = System.currentTimeMillis(),
    var claimStatus:String="",
    val claimReason: String?=null,

    @Embedded(prefix = "claimer_")
    val claimer: UserEntity?=null,
    @Embedded
    var item: LostItemEntity?=null



){
    val formattedDate: String
        get() = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(claimTime))

    val formattedTime: String
        get() = SimpleDateFormat("hh:mm a", Locale.getDefault())
            .format(Date(claimTime))
            .lowercase(Locale.getDefault())
}