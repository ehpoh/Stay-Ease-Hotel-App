package com.example.stayeasehotel.ui.staff

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.stayeasehotel.R
import com.example.stayeasehotel.data.StaffProfileBs

import com.example.stayeasehotel.model.StaffProfileB
import com.example.stayeasehotel.ui.viewmodel.StaffViewModel

@Composable
fun StaffProfileButtons(
    navController: NavHostController,
    staffViewModel: StaffViewModel
) {
    val staffProfileButtons = StaffProfileBs.StaffProfileBarList

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        staffProfileButtons.forEach { profileButton ->
            StaffProfileButton(
                staffProfileB = profileButton,
                onClick = {
                    when (profileButton.buttonStaffBName) {
                        R.string.staffP1 -> navController.navigate("user_list")
                        R.string.staffP2 -> navController.navigate("staff_list")
                        R.string.staffP3 -> navController.navigate("staff_logout")
                    }
                }
            )
        }
    }
}

@Composable
fun StaffProfileButton(
    staffProfileB: StaffProfileB,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(RoundedCornerShape(6.dp))
            .clickable(onClick = onClick)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(
                painter = painterResource(id = staffProfileB.buttonStaffBImage),
                contentDescription = stringResource(id = staffProfileB.buttonStaffBName),
                tint = Color.Unspecified,
                modifier = Modifier.size(50.dp)
            )

            //Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = stringResource(id = staffProfileB.buttonStaffBName),
                fontSize = 40.sp,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
        }
    }
}