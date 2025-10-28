package com.example.stayeasehotel.ui.staff

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
//import com.example.stayeasehotel.ui.staff.StaffBottomAppBar
import com.example.stayeasehotel.ui.viewmodel.StaffViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun StaffLogOut(
    navController: NavHostController,
    staffViewModel: StaffViewModel
) {
    // Handle system back button
    BackHandler {
        FirebaseAuth.getInstance().signOut()
        staffViewModel.triggerStateChoiceNavigation()
    }

    Scaffold(
        bottomBar = {
            StaffBottomAppBar(navController)
        }
    ) { innerPadding ->
        StaffLogOutContent(innerPadding, staffViewModel)
    }
}

@Composable
fun StaffLogOutContent(
    padding: PaddingValues,
    staffViewModel: StaffViewModel
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(16.dp)
    ) {
        Text(
            text = "Log Out Successful!",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        Button(
            onClick = {
                FirebaseAuth.getInstance().signOut()
                staffViewModel.clearStaffData()
                staffViewModel.triggerStateChoiceNavigation()
            }
        ) {
            Text("Ok")
        }
    }
}