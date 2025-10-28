package com.example.stayeasehotel.ui.staff

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.stayeasehotel.ui.navigation.ReservationNavigation
import com.example.stayeasehotel.ui.navigation.StaffReservationNavigation
//import com.example.stayeasehotel.ui.staff.StaffBottomAppBar

@Composable
fun StaffReservationManagement(navController: NavHostController) {
    val innerNavController = rememberNavController()
    val navBackStackEntry by innerNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomBarRoutes = listOf(
        ReservationNavigation.ReservationRecord.name
    )

    Scaffold(
        bottomBar = {
            if (currentRoute in bottomBarRoutes) {
                StaffBottomAppBar(navController) // Your bottom navigation bar
            }
        }
    ) { innerPadding ->
        StaffReservationNavigation(
            navController = innerNavController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
