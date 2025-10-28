package com.example.stayeasehotel.ui.staff

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.stayeasehotel.ui.viewmodel.StaffManagementViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.stayeasehotel.data.StaffEntity

//import com.example.stayeasehotel.model.StaffEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffStaffList(navController: NavHostController) {
    val viewModel: StaffManagementViewModel = viewModel()
    val currentStaffPosition by viewModel.currentStaffPosition.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Staff List") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("staff_profile") }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (currentStaffPosition == "Boss" || currentStaffPosition == "Admin") {
                        IconButton(
                            onClick = {
                                navController.navigate("staff_signin") {
                                    popUpTo(0)
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Staff"
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        StaffListContent(innerPadding, viewModel, navController)
    }
}

@Composable
fun StaffListContent(
    padding: PaddingValues,
    viewModel: StaffManagementViewModel,
    navController: NavHostController
) {
    val staffList by viewModel.staffList.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    val currentStaffPosition by viewModel.currentStaffPosition.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadStaff()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        if (loading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (error != null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Error: $error")
            }
        } else if (staffList.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No staff members found")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(staffList) { staff ->
                    StaffListItem(
                        staff = staff,
                        viewModel = viewModel,
                        currentStaffPosition = currentStaffPosition,
                        onUpdate = { viewModel.loadStaff() }
                    )
                }
            }
        }
    }
}

@Composable
fun StaffListItem(
    staff: StaffEntity,
    viewModel: StaffManagementViewModel,
    currentStaffPosition: String?,
    onUpdate: () -> Unit
) {
    var showPositionChangeDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val isBoss = currentStaffPosition == "Boss"
    val isAdmin = currentStaffPosition == "Admin"
    val canAddOrDelete = isBoss || isAdmin
    val canDeleteThisStaff = when {
        isBoss -> staff.position != "Boss"
        isAdmin -> staff.position == "Staff"
        else -> false
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = staff.position,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = staff.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = staff.email,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = staff.phoneNum,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "ID: ${staff.staffId}",
                    fontSize = 12.sp
                )
            }

            if (canAddOrDelete) {
                Spacer(modifier = Modifier.width(8.dp))
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (isBoss && staff.position != "Boss") {
                        Button(
                            onClick = { showPositionChangeDialog = true },
                            modifier = Modifier.width(120.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text(
                                text = if (staff.position == "Staff") "To Admin" else "To Staff",
                                fontSize = 12.sp
                            )
                        }
                    }

                    if (canDeleteThisStaff) {
                        Button(
                            onClick = { showDeleteDialog = true },
                            modifier = Modifier.width(120.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text(
                                text = "Delete",
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }

    if (showPositionChangeDialog && isBoss) {
        AlertDialog(
            onDismissRequest = { showPositionChangeDialog = false },
            title = { Text("Change Position") },
            text = {
                Text("Are you sure you want to change ${staff.name}'s position from ${staff.position} to ${if (staff.position == "Staff") "Admin" else "Staff"}?")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val newPosition = if (staff.position == "Staff") "Admin" else "Staff"
                        viewModel.updateStaffPosition(
                            staffId = staff.staffId,
                            newPosition = newPosition,
                            onSuccess = {
                                showPositionChangeDialog = false
                                onUpdate()
                            },
                            onError = { error ->
                                showPositionChangeDialog = false
                            }
                        )
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showPositionChangeDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showDeleteDialog && canDeleteThisStaff) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Staff") },
            text = { Text("Are you sure you want to delete ${staff.name}? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteStaff(
                            staffId = staff.staffId,
                            onSuccess = {
                                showDeleteDialog = false
                                onUpdate()
                            },
                            onError = { error ->
                                showDeleteDialog = false
                            }
                        )
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}