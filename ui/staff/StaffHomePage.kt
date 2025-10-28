package com.example.stayeasehotel.ui.staff

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
//import com.example.stayeasehotel.ui.staff.StaffBottomAppBar

@Composable
fun StaffHomePage(navController: NavHostController) {
    Scaffold(
        bottomBar = {
            StaffBottomAppBar(navController) // Your bottom navigation bar
        }
    ) { innerPadding ->
        StaffHomeContent(innerPadding)
    }
}

@Composable
fun StaffHomeContent(padding: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .verticalScroll(rememberScrollState())
    ) {
        // Your home page content here
        Text(
            text = "Welcome to Staff Home",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )

        Text(
            text = "Dashboard Overview",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}