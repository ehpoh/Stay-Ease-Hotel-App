package com.example.stayeasehotel.ui.management
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stayeasehotel.R

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource


import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter


import androidx.navigation.NavController


import com.example.stayeasehotel.data.ClaimData.ClaimEntity
import com.example.stayeasehotel.data.LostItemData.LostItemEntity
import com.example.stayeasehotel.helper.ImageCarousel
import com.example.stayeasehotel.ui.staff.Management.ClaimAction
import com.example.stayeasehotel.ui.staff.Management.DialogState
import com.example.stayeasehotel.ui.staff.Management.LostAction
import com.example.stayeasehotel.ui.staff.Management.ManagementUiState
import com.example.stayeasehotel.ui.staff.Management.ManagementViewModel
import com.example.stayeasehotel.shareFilterScreen.AppliedFiltersSummary
import com.example.stayeasehotel.shareFilterScreen.FilterDialog
import com.example.stayeasehotel.shareFilterScreen.FilterOptions
import com.example.stayeasehotel.shareFilterScreen.SharedFilterSection
import com.example.stayeasehotel.shareFilterScreen.SharedFilterViewModel


import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManagementScreen(
    navController: NavController,
    sharedFilterViewModel: SharedFilterViewModel = viewModel(),
    managementViewModel: ManagementViewModel = viewModel()
) {
    val uiState by managementViewModel.uiState.collectAsState()
    val sharedUiState by sharedFilterViewModel.uiState.collectAsState()
    val filteredLostItems by sharedFilterViewModel.filteredLostItems.collectAsState()
    val filteredClaimItems by sharedFilterViewModel.filteredClaimItems.collectAsState()


    LaunchedEffect(uiState.lostItems, uiState.claimItems) {
        sharedFilterViewModel.setAllLostItems(uiState.lostItems)
        sharedFilterViewModel.setAllClaimItems(uiState.claimItems)
    }

    var selectedTab by remember { mutableStateOf(0) }
    val tabTitles = listOf("Lost & Found", "Claims")
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                title = {
                    Text(
                        "Lost and Claim Management",
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                actions = {
                    IconButton(onClick = { sharedFilterViewModel.toggleFilterDialog(true) }) {
                        Icon(Icons.Default.Tune, contentDescription = "Filter")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)

        ) {
             TabRow(selectedTabIndex = selectedTab) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
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
                "Category" to sharedUiState.selectedCategories,
                "Location" to sharedUiState.selectedLocations,
                "Status" to sharedUiState.selectedStatuses,
                "Submitter" to sharedUiState.selectedSubmitters,
                "Start Date" to listOf(sharedUiState.startDate?.format(dateFormatter) ?: "All"),
                "End Date" to listOf(sharedUiState.endDate?.format(dateFormatter) ?: "All"),
                "Start Time" to listOf(sharedUiState.startTime?.format(timeFormatter) ?: "All"),
                "End Time" to listOf(sharedUiState.endTime?.format(timeFormatter) ?: "All"),
            )

            AppliedFiltersSummary(
                filters = filters,
                onRemoveFilter = { type, value -> sharedFilterViewModel.removeFilter(type, value) },
                onClearAll = { sharedFilterViewModel.showClearFiltersConfirmationDialog(true) }
            )




            when (selectedTab) {
                0 -> { // Lost items
                    if (filteredLostItems.isEmpty()) {
                        EmptyBox("No lost items found")
                    } else {
                        LazyColumn {
                            items(filteredLostItems, key = { it.id }) { item ->
                                LostItemCard(
                                    item = item,
                                    onApprove = { managementViewModel.setLostAction(item, LostAction.APPROVE) },
                                    onReject  = { managementViewModel.setLostAction(item, LostAction.REJECT) },
                                    onDelete  = { managementViewModel.setLostAction(item, LostAction.DELETE) },
                                    uiState = uiState,
                                    viewModel = managementViewModel
                                )
                                Spacer(Modifier.height(12.dp))
                            }
                        }
                    }
                }
                1 -> { // Claims
                    if (filteredClaimItems.isEmpty()) {
                        EmptyBox("No claims found")
                    } else {
                        LazyColumn {
                            items(filteredClaimItems, key = { it.claimId }) { claim ->
                                ClaimCard(
                                    claim = claim,
                                    expanded = uiState.expandedClaimId == claim.claimId,
                                    onExpandToggle = { managementViewModel.toggleExpandClaim(claim.claimId) },
                                    onApprove  = { managementViewModel.setClaimAction(claim, ClaimAction.APPROVE) },
                                    onReject   = { managementViewModel.setClaimAction(claim, ClaimAction.REJECT) },
                                    onProgress = { managementViewModel.setClaimAction(claim, ClaimAction.IN_PROGRESS) },
                                    onDelete   = { managementViewModel.setClaimAction(claim, ClaimAction.DELETE) },
                                    onRestore  = { managementViewModel.setClaimAction(claim, ClaimAction.RESTORE) }
                                )
                                Spacer(Modifier.height(12.dp))
                            }
                        }
                    }
                }
            }
        }



        FilterDialog(
            viewModel = sharedFilterViewModel,
            options = FilterOptions(
                showCategory = true,
                showLocation = true,
                showDateTime = true,
                showSubmitter = true,
                showStatus = true
            )
        )

        when (val dialog = uiState.dialogState) {
            is DialogState.None -> Unit

            is DialogState.Reason -> {
                AlertDialog(
                    onDismissRequest = { managementViewModel.dismissDialog() },
                    title = { Text(stringResource(R.string.reason_required)) },
                    text = {
                        OutlinedTextField(
                            value = uiState.actionReason,
                            onValueChange = { managementViewModel.setActionReason(it) },
                            label = { Text("Reason") }
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            when {
                                uiState.lostItems.any { it.id == dialog.itemId } -> {
                                    uiState.lostItems.find { it.id == dialog.itemId }?.let {
                                        managementViewModel.performLostAction(
                                            LostAction.valueOf(dialog.action.uppercase()), it
                                        )
                                    }
                                }
                                uiState.claimItems.any { it.claimId == dialog.itemId } -> {
                                    uiState.claimItems.find { it.claimId == dialog.itemId }?.let {
                                        managementViewModel.performClaimAction(
                                            ClaimAction.valueOf(dialog.action.uppercase()), it
                                        )
                                    }
                                }
                            }
                        }) { Text("Confirm") }
                    },
                    dismissButton = {
                        TextButton(onClick = { managementViewModel.dismissDialog() }) { Text("Cancel") }
                    }
                )
            }

            is DialogState.ConfirmClaim -> {
                AlertDialog(
                    onDismissRequest = { managementViewModel.dismissDialog() },
                    title = { Text("Confirm Claim Action") },
                    text = { Text("Are you sure you want to ${dialog.action.name} this claim?") },
                    confirmButton = {
                        TextButton(onClick = {
                            managementViewModel.performClaimAction(dialog.action, dialog.item)
                        }) { Text("Yes") }
                    },
                    dismissButton = {
                        TextButton(onClick = { managementViewModel.dismissDialog() }) { Text("No") }
                    }
                )
            }

            is DialogState.ConfirmLost -> {
                AlertDialog(
                    onDismissRequest = { managementViewModel.dismissDialog() },
                    title = { Text("Confirm Lost Item Action") },
                    text = { Text("Are you sure you want to ${dialog.action.name} this lost item?") },
                    confirmButton = {
                        TextButton(onClick = {
                            managementViewModel.performLostAction(dialog.action, dialog.item)
                        }) { Text("Yes") }
                    },
                    dismissButton = {
                        TextButton(onClick = { managementViewModel.dismissDialog() }) { Text("No") }
                    }
                )
            }
        }
    }
}

@Composable
fun EmptyBox(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Inbox, contentDescription = null, modifier = Modifier.size(72.dp), tint = Color.Gray)
            Spacer(Modifier.height(8.dp))
            Text(message, color = Color.Gray)
        }
    }
}

@Composable
fun LostItemCard(
    item: LostItemEntity,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    onDelete: () -> Unit,
    uiState: ManagementUiState,
    viewModel: ManagementViewModel,
    modifier:Modifier= Modifier
) {

    val statusColor = when (item.status) {
        "Approved" -> Color(0xFF4CAF50)
        "Rejected" -> Color(0xFFF44336)
        "Deleted" -> MaterialTheme.colorScheme.error
        else -> Color(0xFFFFC107)
    }

    val isInfoExpanded = uiState.expandedStates[item.id] ?: false
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {

            if (item.imageUrls.isNotEmpty()) {
                ImageCarousel(  item.imageUrls)
                Spacer(modifier = Modifier.height(8.dp))
            }else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Image not available",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Red
                    )
                }
            }

            InfoRow("\uD83D\uDCE6 ${stringResource(R.string.item_title)}:", item.itemTitle)
            InfoRow("🆔 ${stringResource(R.string.lost_id)}:", item.id)
            InfoRow("📍 Found ${stringResource(R.string.found_location)}:", item.foundLocation)
            InfoRow("📅 Found ${stringResource(R.string.date_found)}:", item.dateFound.toString())

            AnimatedVisibility(visible = isInfoExpanded) {
                Column {
                    InfoRow("📂 ${stringResource(R.string.category)}:", item.category)
                    InfoRow("🕒 Found ${stringResource(R.string.time_found)}:", item.timeFound.toString())
                    if (!item.description.isNullOrEmpty()) {
                        InfoRow("📝 ${stringResource(R.string.description)}:", item.description!!)
                    }
                    val formatter = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
                    val formattedTime = item.reportTime?.let { formatter.format(Date(it)) } ?: "N/A"


                    InfoRow("👤 Reported By", item.reporter?.name ?: "Unknown")

                    InfoRow("✉️ Email", item.reporter?.email ?: "Unknown")

                    if (!item.reporter?.phoneNumber.isNullOrBlank()) {
                        InfoRow("📞 Phone Number", item.reporter?.phoneNumber ?: "")
                    }

                    InfoRow("🗓️ Reported On", formattedTime)

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            TextButton(
                onClick = { viewModel.toggleInfoExpanded(item.id) },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(if (isInfoExpanded) "Hide Details" else "Show More Details")
                Icon(
                    imageVector = if (isInfoExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null
                )
            }

            when(item.status) {
                "Pending" -> {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        // Approve
                        Button(onClick = { viewModel.setLostAction(item, LostAction.APPROVE) }, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Default.Check, contentDescription = "Approve")
                            Spacer(Modifier.width(4.dp))
                            Text("Approve")
                        }
                        // Reject
                        OutlinedButton(onClick = { viewModel.setLostAction(item, LostAction.REJECT)
                        }, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Default.Close, contentDescription = "Reject")
                            Spacer(Modifier.width(4.dp))
                            Text("Reject")
                        }
                        // Delete
                        OutlinedButton(onClick = { viewModel.setLostAction(item, LostAction.DELETE)
                        }, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                            Spacer(Modifier.width(4.dp))
                            Text("Delete")
                        }
                    }
                }
                "Deleted" -> {
                    OutlinedButton(
                        onClick = { viewModel.setLostAction(item, LostAction.RESTORE)
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.6f)
                            .height(50.dp)
                            .align(Alignment.CenterHorizontally)
                    ) {
                        Text(
                            text = "♻️ Restore",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                }
                else -> {
                    Box(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .align(Alignment.CenterHorizontally)
                            .background(
                                color = statusColor.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = item.status,
                            color = statusColor,
                            fontSize = 16.sp,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }


                }
            }


        }
    }



}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClaimCard(
    claim: ClaimEntity,
    expanded: Boolean,
    onExpandToggle: () -> Unit,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    onProgress: () -> Unit,
    onDelete:() -> Unit,
    onRestore:() -> Unit,



    ) {

    val statusColors = when (claim.claimStatus) {
        "Approved" -> listOf(Color(0xFF2E7D32), Color(0xFF66BB6A))
        "Rejected" -> listOf(Color(0xFFC62828), Color(0xFFEF5350))
        "Pending" -> listOf(Color(0xFFFFA000), Color(0xFFFFD54F))
        "In Progress" -> listOf(Color(0xFF1976D2), Color(0xFF64B5F6))
        "Deleted" -> listOf(Color(0xFF616161), Color(0xFFBDBDBD))
        "Restore" -> listOf(Color(0xFF00897B), Color(0xFF4DB6AC))

        else -> listOf(Color.Gray, Color.LightGray)
    }


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onExpandToggle() }
            .animateContentSize(),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            claim.item?.imageUrls?.let { imageList ->
                if (imageList.isNotEmpty()) {
                    ImageCarousel(

                        imageList
                    )
                }else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Image not available",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Red
                        )
                    }
                }
            }






             Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = claim.item?.itemTitle ?: "Untitled Item",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.height(6.dp))
                    InfoRow(icon = Icons.Default.Person, label = "Claimer", value = claim.claimer?.name)
                    InfoRow(icon = Icons.Default.Phone, label = "Contact", value = claim.claimer?.phoneNumber ?: "N/A")
                    InfoRow(icon = Icons.Default.Email, label = "Email", value = claim.claimer?.email ?: "N/A")

                    InfoRow(icon = Icons.Default.Category, label = "Category", value = claim.item?.category)
                    InfoRow(icon = Icons.Default.Place, label = "Location", value = claim.item?.foundLocation)
                    InfoRow(icon = Icons.Default.DateRange, label = "Date Found", value = claim.formattedDate)
                    InfoRow(icon = Icons.Default.AccessTime, label = "Time Found", value = claim.formattedTime)

                }

                ClaimStatusBadge(status = claim.claimStatus, colors = statusColors)

                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Collapse details" else "Expand details",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            if (expanded) {
                Spacer(Modifier.height(16.dp))

                Text("Claim Details", style = MaterialTheme.typography.titleSmall)



                InfoRow(icon = Icons.Default.Description, label = "Description", value = claim.claimDescription)
                InfoRow(icon = Icons.Default.Star, label = "Marks", value = claim.marks)
                InfoRow(icon = Icons.Default.Note, label = "Notes", value = claim.notes.ifBlank { "None" })



                Spacer(Modifier.height(16.dp))

                // Action buttons
                // Action buttons
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    when (claim.claimStatus) {
                        "Pending" -> {
                            Button(onClick = onApprove, modifier = Modifier.fillMaxWidth()) {
                                Icon(Icons.Default.Check, contentDescription = "Approve")
                                Spacer(Modifier.width(4.dp))
                                Text("Approve")
                            }

                            OutlinedButton(onClick = onProgress, modifier = Modifier.fillMaxWidth()) {
                                Icon(Icons.Default.HourglassEmpty, contentDescription = "In Progress")
                                Spacer(Modifier.width(4.dp))
                                Text("In Progress")
                            }

                            OutlinedButton(onClick = onReject, modifier = Modifier.fillMaxWidth()) {
                                Icon(Icons.Default.ThumbDown, contentDescription = "Reject")
                                Spacer(Modifier.width(4.dp))
                                Text("Reject")
                            }

                            OutlinedButton(onClick = onDelete, modifier = Modifier.fillMaxWidth()) {
                                Icon(Icons.Default.Close, contentDescription = "Delete")
                                Spacer(Modifier.width(4.dp))
                                Text("Delete")
                            }
                        }

                        "In Progress" -> {
                            Button(onClick = onApprove, modifier = Modifier.fillMaxWidth()) {
                                Icon(Icons.Default.Check, contentDescription = "Approve")
                                Spacer(Modifier.width(4.dp))
                                Text("Approve")
                            }

                            OutlinedButton(onClick = onReject, modifier = Modifier.fillMaxWidth()) {
                                Icon(Icons.Default.ThumbDown, contentDescription = "Reject")
                                Spacer(Modifier.width(4.dp))
                                Text("Reject")
                            }

                            OutlinedButton(onClick = onDelete, modifier = Modifier.fillMaxWidth()) {
                                Icon(Icons.Default.Close, contentDescription = "Delete")
                                Spacer(Modifier.width(4.dp))
                                Text("Delete")
                            }
                        }

                        "Deleted" -> {
                            OutlinedButton(onClick = onRestore, modifier = Modifier.fillMaxWidth()) {
                                Icon(Icons.Default.Restore, contentDescription = "Restore")
                                Spacer(Modifier.width(4.dp))
                                Text("Restore")
                            }
                        }
                    }
                }







            }
        }
    }
}


@Composable
fun InfoRow(icon: ImageVector, label: String, value: String?) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = "$label icon", tint = Color.Gray, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(4.dp))
        Text("$label: ${value ?: "Unknown"}", style = MaterialTheme.typography.bodySmall)
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
fun ClaimStatusBadge(status: String, colors: List<Color>) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(Brush.horizontalGradient(colors))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = status,
            style = MaterialTheme.typography.labelMedium.copy(color = Color.White)
        )
    }
}





