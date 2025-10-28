package com.example.stayeasehotel.ui.LostItemUi.LostFoundCenterScreen.MyLostClaimItemScreen

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VerifiedUser

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource

import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.stayeasehotel.data.ClaimData.ClaimEntity
import com.example.stayeasehotel.data.LostItemData.LostItemEntity
import com.example.stayeasehotel.helper.ImageCarousel
import com.example.stayeasehotel.shareFilterScreen.AppliedFiltersSummary
import com.example.stayeasehotel.shareFilterScreen.FilterDialog
import com.example.stayeasehotel.shareFilterScreen.FilterOptions
import com.example.stayeasehotel.shareFilterScreen.SharedFilterSection
import com.example.stayeasehotel.shareFilterScreen.SharedFilterViewModel
import com.example.stayeasehotel.ui.user.LostFoundCenterScreen.LostAndFoundScreen.InfoRow

import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.core.net.toUri
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.stayeasehotel.R
import com.example.stayeasehotel.helper.PreviewImageDialog
import coil.compose.rememberAsyncImagePainter
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyLostClaimItemScreen(navController: NavController, sharedFilterViewModel: SharedFilterViewModel=viewModel(), myLostItemScreenViewModel: MyLostClaimItemScreenViewModel=viewModel() ) {

    val uiState by myLostItemScreenViewModel.uiState.collectAsState()


    val snackbarHostState = remember { SnackbarHostState() }

    // Update filter with all items
    LaunchedEffect(uiState.claimItems) {
        sharedFilterViewModel.setAllClaimItems(uiState.claimItems)
    }

    LaunchedEffect(uiState.lostItems) {
        sharedFilterViewModel.setAllLostItems(uiState.lostItems)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },


                title = { Text("My Lost and Claim Items", style = MaterialTheme.typography.titleMedium) },

                actions = {


                    IconButton(onClick = { sharedFilterViewModel.toggleFilterDialog(true) }) {
                        Icon(Icons.Default.Tune, contentDescription = "Filter")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        content = { innerPadding ->

            Box(modifier = Modifier.padding(innerPadding)) {
                MyLostClaimItemScreenBody(

                    sharedFilterViewModel,
                    myLostItemScreenViewModel
                )
            }

            FilterDialog(
                viewModel = sharedFilterViewModel,
                options = FilterOptions(
                    showCategory = true,
                    showLocation = true,
                    showDateTime = true,
                    showStatus = true
                )
            )

        }
    )
}

@Composable
fun MyLostClaimItemScreenBody(
    sharedFilterViewModel: SharedFilterViewModel,
    myLostItemScreenViewModel: MyLostClaimItemScreenViewModel
) {
    val myLostItemScreenUiState by myLostItemScreenViewModel.uiState.collectAsState()
    val filteredClaimItems by sharedFilterViewModel.filteredClaimItems.collectAsState()
    val filteredLostItems by sharedFilterViewModel.filteredLostItems.collectAsState()
    val sharedFilterUiState by sharedFilterViewModel.uiState.collectAsState()

    var selectedTabIndex by remember { mutableStateOf(0) }

    val tabTitles = listOf("My Lost Items", "My Claim Items")



    Column(modifier = Modifier.fillMaxSize()) {

        TabRow(selectedTabIndex = selectedTabIndex) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title) }
                )
            }
        }

         SharedFilterSection(
            viewModel = sharedFilterViewModel,
            title = "Filters",
            onFilterApplied = { sharedFilterViewModel.performSearch() }
        )

        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val timeFormatter = DateTimeFormatter.ofPattern("hh:mm a")
         val filters = mapOf(
            "Category" to sharedFilterUiState.selectedCategories,
            "Location" to sharedFilterUiState.selectedLocations,
            "Status" to sharedFilterUiState.selectedStatuses,
            "Submitter" to sharedFilterUiState.selectedSubmitters,
             "Start Date" to listOf(sharedFilterUiState.startDate?.format(dateFormatter) ?: "All"),
             "End Date" to listOf(sharedFilterUiState.endDate?.format(dateFormatter) ?: "All"),
             "Start Time" to listOf(sharedFilterUiState.startTime?.format(timeFormatter) ?: "All"),
             "End Time" to listOf(sharedFilterUiState.endTime?.format(timeFormatter) ?: "All")
        )

        AppliedFiltersSummary(
            filters = filters,
            onRemoveFilter = { type, value -> sharedFilterViewModel.removeFilter(type, value) },
            onClearAll = { sharedFilterViewModel.showClearFiltersConfirmationDialog(true) }
        )



         LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            when (selectedTabIndex) {
                0 -> { // My Lost Items
                    if (filteredLostItems.isEmpty()) {
                        item {
                            NoItemsFound(message = "No lost items found")
                        }
                    } else {
                        items(filteredLostItems.size) { index ->
                            val lostItem = filteredLostItems[index]
                            LostItemCard(
                                lostItem = lostItem,
                                viewModel = myLostItemScreenViewModel
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }

                1 -> { // My Claim Items
                    if (filteredClaimItems.isEmpty()) {
                        item {
                            NoItemsFound(message = "No claimed items found")
                        }
                    } else {
                        items(filteredClaimItems.size) { index ->
                            val claimedItem = filteredClaimItems[index]
                            ClaimedItemCard(
                                claimItem = claimedItem,
                                viewModel = myLostItemScreenViewModel
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
        }

        // Lost Item Detail Dialog
        if (myLostItemScreenUiState.showLostDetails && myLostItemScreenUiState.selectedLostItem != null) {
            myLostItemScreenUiState.selectedLostItem?.let { item ->
                MyLostItemDetailScreen(
                    item = item,
                    onClose = { myLostItemScreenViewModel.hideLostItemDetails() },
                    myLostItemScreenViewModel
                )
            }
        }

        // Claim Item Detail Dialog
        if (myLostItemScreenUiState.showClaimDetails && myLostItemScreenUiState.selectedClaimItem != null) {
            myLostItemScreenUiState.selectedClaimItem?.let { item ->
                ClaimItemDetailScreen(
                    item = item,
                    onClose = { myLostItemScreenViewModel.hideClaimItemDetails() }
                )
            }
        }
    }
}


@Composable
fun NoItemsFound(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 100.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Inbox,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(72.dp)
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}


@Composable
fun LostItemCard(lostItem: LostItemEntity, viewModel: MyLostClaimItemScreenViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (!lostItem.imageUrls.isNullOrEmpty()) {
                ImageCarousel(imageUrls = lostItem.imageUrls)
                Spacer(modifier = Modifier.height(12.dp))
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

            Spacer(modifier = Modifier.height(8.dp))

            Text("📦 ${lostItem.itemTitle}", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(onClick = { viewModel.showLostItemDetails(lostItem) }) {
                Icon(Icons.Default.Info, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("View Details")
            }
        }
    }
}

@Composable
fun ClaimedItemCard(claimItem: ClaimEntity, viewModel: MyLostClaimItemScreenViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (!claimItem.item?.imageUrls.isNullOrEmpty()) {
                ImageCarousel(imageUrls = claimItem.item!!.imageUrls)
                Spacer(modifier = Modifier.height(12.dp))
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
                        color = Color.DarkGray
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }


            Text("📦 ${claimItem.item?.itemTitle ?: "Unknown"}", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(onClick = { viewModel.showClaimItemDetails(claimItem) }) {
                Icon(Icons.Default.VerifiedUser, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("View Details")
            }
        }
    }
}


@Composable
fun MyLostItemDetailScreen(
    item: LostItemEntity,
    onClose: () -> Unit,
    myLostItemScreenViewModel: MyLostClaimItemScreenViewModel
) {
    val formattedReportTime = myLostItemScreenViewModel.getFormattedReportTime(item.reportTime)

    Dialog(onDismissRequest = onClose) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {

            Dialog(onDismissRequest = onClose) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(
                        modifier = Modifier.verticalScroll(rememberScrollState()).padding(16.dp)
                    ) {

                         SectionHeader("Item Details")
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                item.itemTitle?.let {
                                    Text(
                                        it,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(Modifier.height(8.dp))
                                }

                                InfoRow("🆔 Report ID:", item.id)
                                item.category?.let {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    InfoRow("📂 Category:", it)
                                }

                                item.description?.takeIf { it.isNotBlank() }?.let {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        "📝 Description:",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        it,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontStyle = FontStyle.Italic,
                                        color = Color.Gray
                                    )
                                }

                                item.foundLocation?.let {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    InfoRow("📍 Reported Location:", it)
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                InfoRow("📅 Date Lost:", item.dateFound)
                                InfoRow("⏰ Time Lost:", item.timeFound)
                                InfoRow("🕒 Reported on:", formattedReportTime)
                            }
                        }




                        SectionHeader("Status & Reason")
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                StatusChip(item.status)
                                Spacer(modifier = Modifier.height(8.dp))
                                InfoRow("Reason:", item.reportReason ?: "N/A")
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        OutlinedButton(
                            onClick = onClose,
                            modifier = Modifier.align(Alignment.End)
                        ) {
                            Text("Close")
                        }
                    }

                }
            }

        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Divider(color = Color.Gray.copy(alpha = 0.3f), thickness = 1.dp)
        Spacer(modifier = Modifier.height(12.dp))
    }
}


@Composable
fun ClaimItemDetailScreen(
    item: ClaimEntity,
    onClose: () -> Unit
) {
    var previewIndex by remember { mutableStateOf(-1) }
    Dialog(onDismissRequest = { onClose() }) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(modifier = Modifier.verticalScroll(rememberScrollState()).padding(16.dp)) {


                SectionHeader("Item Details")
                Card( modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        item.item?.itemTitle?.let {
                            Text(it, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(8.dp))
                        }
                        InfoRow("\uD83C\uDD94 Claim ID:", item.claimId)
                        InfoRow("🆔 Lost ID:", item.item?.id ?: "Unknown")
                        InfoRow("📅 Claim Date:", item.formattedDate)
                        InfoRow("⏰ Claim Time:", item.formattedTime)
                        item.item?.category?.let { InfoRow("📂 Category:", it) }
                        item.item?.foundLocation?.let { InfoRow("📍 Location Found:", it) }
                        item.item?.description?.takeIf { it.isNotBlank() }?.let {
                            Text("📝 Item Description:", style = MaterialTheme.typography.bodyMedium)
                            Text(it, style = MaterialTheme.typography.bodySmall, fontStyle = FontStyle.Italic)
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))



                SectionHeader("Claim Information")
                Card( modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(" 📝 Claim Description:", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            item.claimDescription,
                            style = MaterialTheme.typography.bodySmall,
                            fontStyle = FontStyle.Italic,
                            color = Color.Gray
                        )

                        Text("🔍 Identifying Marks:", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            item.marks,
                            style = MaterialTheme.typography.bodySmall,
                            fontStyle = FontStyle.Italic,
                            color = Color.Gray
                        )

                    }
                }


                SectionHeader("Proof & Notes")

                val hasProof = !item.proofFileUri.isNullOrEmpty()
                val hasNotes = !item.notes.isNullOrBlank()

                if (!hasProof && !hasNotes) {

                    Text(
                        "No proof or notes provided.",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        fontStyle = FontStyle.Italic
                    )
                } else {
                    Text("Proof:", style = MaterialTheme.typography.bodyMedium)

                    if (!hasProof) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.LightGray),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No images\navailable",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.DarkGray,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(4.dp)
                            )
                        }
                    } else {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            itemsIndexed(item.proofFileUri) { index, uriStr ->
                                val uri = uriStr.toUri()
                                AsyncImage(
                                    model = uri,
                                    contentDescription = "Proof Image",

                                    modifier = Modifier
                                        .size(60.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { previewIndex = index }
                                )
                            }
                        }
                    }

                    if (previewIndex >= 0 && previewIndex < item.proofFileUri.size) {
                        PreviewImageDialog(
                            selectedImages = item.proofFileUri.map { Uri.parse(it) },
                            previewIndex = previewIndex,
                            onDismiss = { previewIndex = -1 }
                        )
                    }

                    if (hasNotes) {
                        Spacer(Modifier.height(12.dp))
                        Text("🗒️ Additional Notes:", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            item.notes!!,
                            style = MaterialTheme.typography.bodySmall,
                            fontStyle = FontStyle.Italic,
                            color = Color.Gray
                        )
                    }
                }




                SectionHeader("Status and Reason")
                Spacer(Modifier.height(8.dp))

                StatusChip(item.claimStatus)
                item.claimReason?.takeIf { it.isNotBlank() }?.let { InfoRow("Reason:", it) }


                Spacer(Modifier.height(16.dp))



            OutlinedButton(
                    onClick = onClose,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Close")
                }
            }


        }
    }
}

@Composable
fun InfoRow(iconLabel: String, value: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(2.dp)
    ) {
        Text(
            text = iconLabel,
            style = MaterialTheme.typography.titleSmall,
            fontSize = 13.5.sp
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontSize = 13.5.sp

        )
    }
}





@Composable
fun StatusChip(status: String) {
    val (icon, color) = when (status) {
        "Pending" -> Icons.Default.HourglassTop to MaterialTheme.colorScheme.tertiary  // ⏳ Pending
        "Approved" -> Icons.Default.CheckCircle to MaterialTheme.colorScheme.primary // ✅ Approved
        "Rejected" -> Icons.Default.Cancel to MaterialTheme.colorScheme.error        // ❌ Rejected
        else -> Icons.Default.Info to MaterialTheme.colorScheme.outline              // ℹ️ Fallback
    }

    AssistChip(
        onClick = {   },
        label = { Text(status) },
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = status,
                tint = color
            )
        }
    )
}
