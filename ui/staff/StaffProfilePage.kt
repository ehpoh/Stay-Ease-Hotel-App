package com.example.stayeasehotel.ui.staff

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.stayeasehotel.R
import com.example.stayeasehotel.ui.viewmodel.StaffViewModel
//import com.example.stayeasehotel.ui.staff.StaffBottomAppBar

@Composable
fun StaffProfilePage(
    navController: NavHostController,
    staffViewModel: StaffViewModel
) {
    Scaffold(
        bottomBar = {
            StaffBottomAppBar(navController) // Your bottom navigation bar
        }
    ) { innerPadding ->
        StaffProfileContent(innerPadding, navController, staffViewModel)
    }
}

@Composable
fun StaffProfileContent(
    padding: PaddingValues,
    navController: NavHostController,
    staffViewModel: StaffViewModel
) {
    val staffData by staffViewModel.staffData.collectAsState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .verticalScroll(rememberScrollState())
    ) {
        // Your home page content here
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp)
                .padding(16.dp)
        ) {
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.edit),
                        contentDescription = "Edit Profile",
                        tint = Color.Unspecified,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                            .size(40.dp)
                            .clickable { navController.navigate("staff_edit") }
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        staffData?.let { staff ->
                            ProfileDetailRow("Staff ID", staff.staffId)
                            Spacer(modifier = Modifier.height(10.dp))
                            ProfileDetailRow("Name", staff.name)
                            Spacer(modifier = Modifier.height(10.dp))
                            ProfileDetailRow("Email", staff.email)
                            Spacer(modifier = Modifier.height(10.dp))
                            ProfileDetailRow("Phone", staff.phoneNum)
                            Spacer(modifier = Modifier.height(10.dp))
                            ProfileDetailRow("Gender", staff.gender)
                            Spacer(modifier = Modifier.height(10.dp))
                            ProfileDetailRow("Position", staff.position)
                        } ?: run {
                            Text(
                                text = "Loading staff information...",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        }
                    }
                }
            }
        }
        StaffProfileButtons(navController, staffViewModel)
    }
}

@Composable
fun ProfileDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            ),
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}