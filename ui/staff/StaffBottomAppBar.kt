package com.example.stayeasehotel.ui.staff

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.stayeasehotel.data.StaffButton
import com.example.stayeasehotel.data.StaffButtons

@Composable
fun StaffBottomAppBar(navController: NavHostController) {
    val staffsButtons = StaffButtons.StaffBarList

    BottomAppBar(
        modifier = Modifier.height(100.dp),
        actions = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly // ← Even spacing
            ) {
                staffsButtons.forEachIndexed { index, button ->
                    StaffNavigationButton(
                        button = button,
                        route = when (index) {
                            0 -> "staff_home"
                            1 -> "staff_reservation"
                            2 -> "staff_lostAndFound"
                            else -> "staff_profile"
                        },
                        navController = navController
                    )
                }
            }
        }
    )
}

@Composable
fun StaffNavigationButton(
    button: StaffButton,
    route: String,
    navController: NavHostController
) {
    val currentRoute = currentRoute(navController)
    val isSelected = currentRoute == route

    val tintColor = if (isSelected) MaterialTheme.colorScheme.primary
    else MaterialTheme.colorScheme.onSurface

    Box(
        modifier = Modifier
            .height(100.dp)
            .width(90.dp)
            .clip(RoundedCornerShape(6.dp)) // Square with slightly rounded corners
            .clickable(
                enabled = !isSelected,
                onClick = {
                    navController.navigate(route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(
                painter = painterResource(id = button.buttonStaffImage),
                contentDescription = stringResource(id = button.buttonStaffName),
                tint = Color.Unspecified, // Keep original icon colors
                modifier = Modifier.size(36.dp)
            )

            Text(
                text = stringResource(id = button.buttonStaffName),
                style = MaterialTheme.typography.labelSmall,
                color = tintColor,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}