package com.example.stayeasehotel.ui.user.LostFoundCenterScreen.LostAndFoundScreen
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stayeasehotel.R
import com.example.stayeasehotel.data.LostItemData.LostItemEntity
import com.example.stayeasehotel.helper.ImageCarousel
import kotlinx.coroutines.launch

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LostItemDetailScreen(
    item: LostItemEntity,
    onNavigateBack: () -> Unit,
    onMarkAsMine: (LostItemEntity) -> Unit,
    lostAndFoundViewModel: LostandFoundCenterViewModel = viewModel()
) {
    val lostAndFoundUiState by lostAndFoundViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val formattedReportTime = lostAndFoundViewModel.getFormattedReportTime(item.reportTime)

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Lost Item Details") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },actions = {
                    IconButton(onClick = {
                        val shareText = buildString {
                            append("Lost Item Details\n\n")
                            append("Title: ${item.itemTitle}\n")
                            append("ID: ${item.id}\n")
                            append("Category: ${item.category}\n")
                            append("Location Found: ${item.foundLocation}\n")
                            append("Date Found: ${item.dateFound}\n")
                            append("Time Found: ${item.timeFound}\n")
                            append("Reported By: ${item.reporter?.name ?: "Anonymous"}\n")
                            append("Reported On: ${formattedReportTime}\n")
                            item.description?.let {
                                append("Description: $it\n")
                            }
                        }








                        if (item.imageUrls.isNotEmpty()) {
                            coroutineScope.launch {
                                context.shareTextAndImages(item.imageUrls, shareText)
                            }
                        } else {
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_TEXT, shareText)
                            }
                            context.startActivity(Intent.createChooser(intent, null))
                        }
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(16.dp)
        ) {

            // Image Carousel
            if (item.imageUrls.isNotEmpty()) {
                ImageCarousel(imageUrls = item.imageUrls)
                Spacer(modifier = Modifier.height(8.dp))
            }else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No Image Available",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Red
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }


            // Info Rows
            InfoRow("\uD83D\uDCE6 ${stringResource(R.string.item_title)}:", item.itemTitle)
            InfoRow("🆔 ID:", item.id)
            InfoRow("📂 Category:", item.category)
            InfoRow("📍 Location Found:", item.foundLocation)
            InfoRow("📅 Date Found:", item.dateFound)
            InfoRow("⏰ Time Found:", item.timeFound)
            InfoRow("👤 Reported By:", item.reporter?.name ?: "Anonymous")



            InfoRow("🕒 Reported on:", formattedReportTime)




            item.description?.let {
                InfoRow("📝 Description:", it)
            }

            LaunchedEffect(Unit) {
                lostAndFoundViewModel.uiEvent.collect { event ->
                    when (event) {
                        is UiEvent.ShowToast -> {
                            Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                        }
                        is UiEvent.MarkAsMine -> {
                            onMarkAsMine(event.item)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    lostAndFoundUiState.currentUserId?.let { currentUserId ->
                        lostAndFoundViewModel.handleMarkAsMine(item, currentUserId)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Check, contentDescription = "Mark as Mine")
                Spacer(modifier = Modifier.width(6.dp))
                Text("Mark as Mine")
            }

            Spacer(modifier = Modifier.height(16.dp))


        }
    }
}
